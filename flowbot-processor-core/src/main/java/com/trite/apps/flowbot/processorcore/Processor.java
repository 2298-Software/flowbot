package com.trite.apps.flowbot.processorcore;

import com.trite.apps.flowbot.result.Result;

import java.util.HashMap;
import org.apache.log4j.Logger;

public class Processor{
    public Logger logger = Logger.getLogger(this.getClass().getName());

    private String name;
    private String description;
    private HashMap<String, String> processorAttribues;
    private Result r;

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

    public Result run(String stepName, Result[] stuff) {
        logger.info("running " + this.getClass().getSimpleName());
        return r;
    }

}