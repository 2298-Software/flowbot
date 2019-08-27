package com._2298software.apps.flowbot.processorcore;

import com._2298software.apps.flowbot.result.ProcessorResult;
import java.lang.*;
import java.util.HashMap;
import org.apache.log4j.Logger;

public class Processor implements Runnable{
    public Logger logger = Logger.getLogger(this.getClass().getName());
    private String name;
    private String description;
    private HashMap<String, String> processorAttribues;
    private ProcessorResult r;

    public Processor(HashMap<String, String> processorAttribues) {
        logger.info("instantiating " + this.getClass().getSimpleName());
        this.processorAttribues = processorAttribues;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public HashMap<String, String> getProcessorAttribues() {
        return processorAttribues;
    }

    public void setProcessorAttribues(HashMap<String, String> processorAttribues) {
        this.processorAttribues = processorAttribues;
    }

    public ProcessorResult run() throws Exception {
        logger.info("running " + this.getClass().getSimpleName());
        return r;
    }

}