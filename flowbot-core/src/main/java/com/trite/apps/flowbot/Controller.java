package com.trite.apps.flowbot;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.trite.apps.flowbot.exception.StepFailedException;
import com.trite.apps.flowbot.result.BooleanResult;
import com.trite.apps.flowbot.result.HashMapResult;
import com.trite.apps.flowbot.result.JsonResult;
import com.trite.apps.flowbot.result.Result;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

@JsonIgnoreProperties(value = { "confFile" })
public class Controller implements Runnable {
    private static Logger logger = Logger.getLogger(Controller.class.getName());

    private int controllerMaxThreads;
    private long controllerLoopThrottleMills;
    private long controllerLoopBackoffMills;

    private String metricsReportingURL;
    private long metricsReportingScheduleMills;

    private String confDir;
    private String confFile;

    public int getControllerMaxThreads() {
        return controllerMaxThreads;
    }

    public void setControllerMaxThreads(int controllerMaxThreads) {
        this.controllerMaxThreads = controllerMaxThreads;
    }

    public long getControllerLoopThrottleMills() {
        return controllerLoopThrottleMills;
    }

    public void setControllerLoopThrottleMills(long controllerLoopThrottleMills) {
        this.controllerLoopThrottleMills = controllerLoopThrottleMills;
    }

    public String getMetricsReportingURL() {
        return metricsReportingURL;
    }

    public void setMetricsReportingURL(String metricsReportingURL) {
        this.metricsReportingURL = metricsReportingURL;
    }

    public long getMetricsReportingScheduleMills() {
        return metricsReportingScheduleMills;
    }

    public void setMetricsReportingScheduleMills(long metricsReportingScheduleMills) {
        this.metricsReportingScheduleMills = metricsReportingScheduleMills;
    }

    public String getConfDir() {
        return confDir;
    }

    public void setConfDir(String confDir) {
        this.confDir = confDir;
    }

    public String getConfFile() {
        return confFile;
    }

    public void setConfFile(String confFile) {
        this.confFile = confFile;
    }

    public long getControllerLoopBackoffMills() {
        return controllerLoopBackoffMills;
    }

    public void setControllerLoopBackoffMills(long controllerLoopBackoffMills) {
        this.controllerLoopBackoffMills = controllerLoopBackoffMills;
    }

    @Override
    public void run() {
        logger.info("Starting Flowbot Controller");
        try {

            File configFile = new File(this.getConfFile());
            if (!configFile.exists()) {
                throw new Exception("Configuration file not found: + " + this.getConfFile());
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
            while (flow.hasMoreSteps()) {

                if (firstStep.getStatus() == 0) {
                    currentStep = firstStep;
                } else {
                    currentStep = flow.getNextStep(currentStep);
                }

                String processorNameString = currentStep.getProcessorAttributes().get("type");
                Class clazz = Class.forName("com.trite.apps.flowbot.processor." + processorNameString);
                Constructor constructor = clazz.getConstructor(HashMap.class);
                Object o = constructor.newInstance(currentStep.getProcessorAttributes());
                Method m = clazz.getMethod("run", String.class, Result[].class);

                r = (Result) m.invoke(o, currentStep.getName(), results);

                if (HashMapResult.class.isInstance(r)) {
                    Result hmr = (HashMapResult) r;
                    stepSuccess = hmr.getResultAttributes().get(currentStep.getName() + "-outcome").equals("success");
                    stepMsg = hmr.getResultAttributes().get(currentStep.getName() + "-outcome-message");
                } else if (BooleanResult.class.isInstance(r)) {
                    BooleanResult br = (BooleanResult) r;
                    stepSuccess = br.getResultAttributes().get(currentStep.getName() + "-outcome").equals("success");
                    stepMsg = br.getResultAttributes().get(currentStep.getName() + "-outcome-message");
                } else if (JsonResult.class.isInstance(r)) {
                    JsonResult jr = (JsonResult) r;
                    stepSuccess = jr.getResultAttributes().get(currentStep.getName() + "-outcome").equals("success");
                    stepMsg = jr.getResultAttributes().get(currentStep.getName() + "-outcome-message");
                    logger.info("JSON result is: " + jr.getResult());
                } else {
                    throw new Exception("result type not implemented!");
                }

                r.getResultAttributes().forEach((key, value) -> logger.info(key + " = " + value));
                results[stepIdx] = r;

                if (currentStep.getOn_success().equals("end") && stepSuccess) {
                    logger.info("Flow has reached the end on step " + currentStep.getName());
                    break;
                }

                if (currentStep.getOn_failure().equals("end") && !stepSuccess) {
                    logger.info("Flow has reached the end on step " + currentStep.getName());
                    break;
                }

                if (!stepSuccess && currentStep.getOn_failure().equals("error")) {
                    throw new StepFailedException(currentStep.getName() + " failed with the following message: " + stepMsg);
                }
                stepIdx++;
                currentStep.setStatus(1);

                logger.info(ReflectionToStringBuilder.toString(currentStep, ToStringStyle.MULTI_LINE_STYLE));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (StepFailedException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
