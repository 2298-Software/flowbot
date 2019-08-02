package com.trite.apps.flowbot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.trite.apps.flowbot.exception.StepFailedException;

import com.trite.apps.flowbot.result.BooleanResult;
import com.trite.apps.flowbot.result.HashMapResult;
import com.trite.apps.flowbot.result.Result;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.log4j.Logger;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;

public class Controller {
    static Logger logger = Logger.getLogger(Controller.class.getName());

    public static void main(String args[]) throws Exception {
        logger.info("Starting Flowbot Controller");

        if(args.length != 1){
            printUsage();
            System.exit(1);
        }

        File configFile = new File(args[0]);
        if(!configFile.exists()){
            printUsage();
            System.exit(1);
        }

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        Flow flow = mapper.readValue(configFile, Flow.class);
        logger.info(ReflectionToStringBuilder.toString(flow, ToStringStyle.MULTI_LINE_STYLE));
        Result results[] = new Result[flow.getSteps().size()];
        Result r;
        boolean stepSuccess;
        String stepMsg;
        Step firstStep = flow.getSteps().get(0);
        Step currentStep = null;

        int stepIdx = 0;
        while(flow.hasMoreSteps()){

            if(firstStep.getStatus() == 0){
                currentStep = firstStep;
            } else {
                currentStep = flow.getNextStep(currentStep);
            }

            String processorNameString = currentStep.getProcessorAttributes().get("type");
            Class clazz = Class.forName("com.trite.apps.flowbot.processor." + processorNameString);
            Constructor constructor = clazz.getConstructor(HashMap.class);
            Object o =  constructor.newInstance(currentStep.getProcessorAttributes());
            Method m = clazz.getMethod("run", String.class, Result[].class);

            r = (Result)m.invoke(o, currentStep.getName(), results);

            if(HashMapResult.class.isInstance(r)){
                Result hmr = (HashMapResult)r;
                stepSuccess = hmr.getResultAttributes().get(currentStep.getName() + "-outcome").equals("success");
                stepMsg = hmr.getResultAttributes().get(currentStep.getName() + "-outcome-message");
            } else if(BooleanResult.class.isInstance(r)){
                BooleanResult br = (BooleanResult)r;
                stepSuccess = br.getResultAttributes().get(currentStep.getName() + "-outcome").equals("success");
                stepMsg = br.getResultAttributes().get(currentStep.getName() + "-outcome-message");
            } else {
                throw new Exception("result type not implemented!");
            }

            r.getResultAttributes().forEach((key, value) -> logger.info(key + " = " + value));
            results[stepIdx] = r;

            if(currentStep.getOn_success().equals("end") && stepSuccess){
                logger.info("Flow has reached the end on step " + currentStep.getName());
                break;
            }

            if(currentStep.getOn_failure().equals("end") && !stepSuccess){
                logger.info("Flow has reached the end on step " + currentStep.getName());
                break;
            }

            if(!stepSuccess && currentStep.getOn_failure().equals("error")){
                throw new StepFailedException(currentStep.getName() + " failed with the following message: " + stepMsg);
            }
            stepIdx++;
            currentStep.setStatus(1);

            logger.info(ReflectionToStringBuilder.toString(currentStep, ToStringStyle.MULTI_LINE_STYLE));
        }
    }

    private static void printUsage() {
        logger.info("Failed to start Flowbot. Please provide the configuration yaml.");
        logger.info("java flowbot.jar configFile.yaml");
    }

}
