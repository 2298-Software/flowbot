package com.trite.apps.flowbot.processor;

import com.trite.apps.flowbot.exception.MissingOrInvalidAttributeException;
import com.trite.apps.flowbot.processorcore.Processor;
import com.trite.apps.flowbot.result.BooleanResult;
import com.trite.apps.flowbot.result.Result;

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

    public BooleanResult run(String stepName, Result[] flowResults) {
        logger.info("running " + this.getClass().getSimpleName());
        BooleanResult r = new BooleanResult();
        HashMap<String, String> resultAttributes = new HashMap<>();
        Boolean result;

        try {
            switch(objectType) {
                case "file":
                    File f = new File(this.getPath());
                    if(f.createNewFile()){
                        result = true;
                        resultAttributes.put(stepName + "-outcome", "success");
                    } else {
                        result = false;
                        resultAttributes.put(stepName + "-outcome", "failure");
                        resultAttributes.put(stepName + "-outcome-message", "the file " + this.getPath() + " does not exist!");
                    }
                    break;
                case "directory":
                    File dir = new File(this.getPath());
                    if(dir.mkdir()){
                        result = true;
                        resultAttributes.put(stepName + "-outcome", "success");
                    } else {
                        result = false;
                        resultAttributes.put(stepName + "-outcome", "failure");
                        resultAttributes.put(stepName + "-outcome-message", "the file " + this.getPath() + " does not exist!");
                    }
                    break;
                default: throw new MissingOrInvalidAttributeException("objectType must be file or directory");
            }

        }
        catch (Exception e) {
            result = false;
            resultAttributes.put(stepName + "-outcome", "failure");
            resultAttributes.put(stepName + "-outcome-message", e.getMessage());
        }

        r.setResult(result);
        r.setResultAttributes(resultAttributes);
        return r;
    }


}