package com.trite.apps.flowbot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.trite.apps.flowbot.exception.ProcessorNotImplementedException;
import com.trite.apps.flowbot.exception.StepFailedException;

import com.trite.apps.flowbot.processor.*;
import com.trite.apps.flowbot.processorcore.*;
import com.trite.apps.flowbot.processorcore.CommandProcessor;
import com.trite.apps.flowbot.result.BooleanResult;
import com.trite.apps.flowbot.result.HashMapResult;
import com.trite.apps.flowbot.result.Result;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.HashMap;

public class Controller {
    public static void main(String args[]) throws Exception {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

        String propFileName = args[0];
        Flow flow = mapper.readValue(new File(propFileName), Flow.class);
        System.out.println(ReflectionToStringBuilder.toString(flow, ToStringStyle.MULTI_LINE_STYLE));
        Result results[] = new Result[flow.getSteps().size()];
        Result r = new Result();
        boolean stepSuccess = false;
        String stepMsg = "no message";
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
            Processor p;

            System.out.println("class " + clazz.getName());

            if(o.getClass().isInstance(CommandProcessor.class)){
                p =((CommandProcessor) o);
            }else if(CheckFileProcessor.class.isInstance(o)){
                p = ((CheckFileProcessor) o);
            }else if(CreateFsProcessor.class.isInstance(o)){
                p = (CreateFsProcessor) o;
            }else  if(DownloadFileProcessor.class.isInstance(o)){
                p = (DownloadFileProcessor) o;
            }else  if(UnTarFileProcessor.class.isInstance(o)){
                p = (UnTarFileProcessor) o;
            } else {
                throw new ProcessorNotImplementedException(processorNameString + " not yet implemented!");
            }

            r = p.run(currentStep.getName(), results);

            if(HashMapResult.class.isInstance(r)){
                Result hmr = r;
                stepSuccess = hmr.getResultAttributes().get(currentStep.getName() + "-outcome").equals("success");
                stepMsg = hmr.getResultAttributes().get(currentStep.getName() + "-outcome-message");
            } else if(BooleanResult.class.isInstance(r)){
                BooleanResult br = (BooleanResult)r;
                stepSuccess = br.getResultAttributes().get(currentStep.getName() + "-outcome").equals("success");
                stepMsg = br.getResultAttributes().get(currentStep.getName() + "-outcome-message");
            } else {
                throw new Exception("result type not implemented!");
            }

            r.getResultAttributes().forEach((key, value) -> System.out.println(key + " = " + value));
            results[stepIdx] = r;

            if(currentStep.getOn_success().equals("end")){
                break;
            } 

            if(!stepSuccess){
                throw new StepFailedException(currentStep.getName() + " failed with the following message: " + stepMsg);
            }
            stepIdx++;
            currentStep.setStatus(1);

            System.out.println(ReflectionToStringBuilder.toString(currentStep, ToStringStyle.MULTI_LINE_STYLE));

        }



    }

}
