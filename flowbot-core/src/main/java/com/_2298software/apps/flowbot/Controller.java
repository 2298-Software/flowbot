package com._2298software.apps.flowbot;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com._2298software.apps.flowbot.exception.StepFailedException;
import com._2298software.apps.flowbot.result.ProcessorResult;
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
            ProcessorResult r;
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
                Class clazz = Class.forName("com._2298software.apps.flowbot.processor." + processorNameString);
                Constructor constructor = clazz.getConstructor(HashMap.class);
                Object o = constructor.newInstance(currentStep.getProcessorAttributes());
                Method m = clazz.getMethod("run");

                r = (ProcessorResult) m.invoke(o);

                if (currentStep.getOn_success().equals("end") && r.getResult()) {
                    logger.info("Flow has reached the end on step " + currentStep.getName());
                    break;
                }

                if (currentStep.getOn_failure().equals("end") && !r.getResult()) {
                    logger.info("Flow has reached the end on step " + currentStep.getName());
                    break;
                }

                if (!r.getResult() && currentStep.getOn_failure().equals("error")) {
                    throw new StepFailedException(currentStep.getName() + " failed with the following message: " + r.getResultMsg());
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
