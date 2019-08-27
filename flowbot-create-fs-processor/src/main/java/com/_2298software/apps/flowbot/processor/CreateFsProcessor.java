package com._2298software.apps.flowbot.processor;

import com._2298software.apps.flowbot.exception.MissingOrInvalidAttributeException;
import com._2298software.apps.flowbot.processorcore.Processor;
import com._2298software.apps.flowbot.result.ProcessorResult;

import java.io.File;
import java.util.HashMap;

public class CreateFsProcessor extends Processor {
    private String path;
    private String objectType;
    private String result;

    public CreateFsProcessor(HashMap<String, String> processorAttribues) {
        super(processorAttribues);
        this.setPath(processorAttribues.get("path"));
        this.setObjectType(processorAttribues.get("objectType"));
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public ProcessorResult run(String stepName) {
        logger.info("running " + this.getClass().getSimpleName());
        ProcessorResult pr = new ProcessorResult(false,"");

        try {
            switch(objectType) {
                case "file":
                    File f = new File(this.getPath());
                    if(f.createNewFile()){
                        pr.setResult(true);
                    } else {
                        pr.setResult(false);
                        pr.setResultMsg("the file " + this.getPath() + " does not exist!");
                    }
                    break;
                case "directory":
                    File dir = new File(this.getPath());
                    if(dir.mkdir()){
                        pr.setResult(true);
                    } else {
                        pr.setResult(false);
                        pr.setResultMsg("the file " + this.getPath() + " does not exist!");
                    }
                    break;
                default: throw new MissingOrInvalidAttributeException("objectType must be file or directory");
            }

        }
        catch (Exception e) {
            pr.setResult(false);
            pr.setResultMsg(e.getMessage());
        }

        return pr;
    }


}