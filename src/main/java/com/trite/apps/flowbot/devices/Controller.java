package com.trite.apps.flowbot.devices;

import java.io.File;

import com.trite.apps.flowbot.devices.processor.CheckFileProcessor;
import com.trite.apps.flowbot.devices.processor.CommandProcessor;
import com.trite.apps.flowbot.devices.processor.DownloadFileProcessor;
import com.trite.apps.flowbot.devices.result.BooleanResult;
import com.trite.apps.flowbot.devices.result.Result;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class Controller {

    public static void main(String args[]) throws Exception {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

        String propFileName = args[0];
        Flow flow = mapper.readValue(new File(propFileName), Flow.class);
        System.out.println(ReflectionToStringBuilder.toString(flow, ToStringStyle.MULTI_LINE_STYLE));
        Result results[] = new Result[flow.getSteps().size()];
        Result r = new Result();
        boolean stepSuccess = false;
        Step firstStep = flow.getSteps().get(0);
        Step currentStep = null;

        int stepIdx = 0;
        while(flow.hasMoreSteps()){

            if(firstStep.getStatus() == 0){
                currentStep = firstStep;
            } else {
                currentStep = flow.getNextStep(currentStep);
            }

            System.out.println(ReflectionToStringBuilder.toString(currentStep, ToStringStyle.MULTI_LINE_STYLE));
            System.out.println(ReflectionToStringBuilder.toString(currentStep.getProcessorAttributes(), ToStringStyle.MULTI_LINE_STYLE));

            switch (currentStep.getProcessorAttributes().get("type")) {
                case "CommandProcessor":
                    CommandProcessor p = new CommandProcessor();
                    p.setCommand(currentStep.getProcessorAttributes().get("command"));
                    Result hmr;

                    hmr = p.run(currentStep.getName(), results);
                    stepSuccess = hmr.getResultAttributes().get(currentStep.getName() + "-outcome").equals("success");
                    results[stepIdx] = r;

                    hmr.getResultAttributes().forEach((key, value) -> System.out.println(key + " = " + value));
                    break;
                case "CheckFileProcessor" :
                    CheckFileProcessor cfp = new CheckFileProcessor();
                    BooleanResult br;

                    cfp.setPath(currentStep.getProcessorAttributes().get("path"));
                    br = cfp.run(currentStep.getName(), results);
                    stepSuccess = br.getResultAttributes().get(currentStep.getName() + "-outcome").equals("success");
                    results[stepIdx] = r;

                    br.getResultAttributes().forEach((key, value) -> System.out.println(key + " = " + value));
                    break;
                case "DownloadFileProcessor" :
                    DownloadFileProcessor dfp = new DownloadFileProcessor();
                    BooleanResult dfpbr;

                    dfp.setLocalPath(currentStep.getProcessorAttributes().get("localPath"));
                    dfp.setRemotePath(currentStep.getProcessorAttributes().get("remotePath"));

                    dfpbr = dfp.run(currentStep.getName(), results);
                    stepSuccess = dfpbr.getResultAttributes().get(currentStep.getName() + "-outcome").equals("success");
                    results[stepIdx] = r;

                    dfpbr.getResultAttributes().forEach((key, value) -> System.out.println(key + " = " + value));
                    break;
        }
            
            if(currentStep.getOn_success().equals("end")){
                break;
            } 

            stepIdx++;
            currentStep.setStatus(1);
        }



    }

}
