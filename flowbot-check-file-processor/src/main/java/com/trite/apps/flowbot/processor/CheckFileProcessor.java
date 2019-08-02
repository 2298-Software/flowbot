package com.trite.apps.flowbot.processor;

import com.trite.apps.flowbot.processorcore.Processor;
import com.trite.apps.flowbot.result.BooleanResult;
import com.trite.apps.flowbot.result.Result;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class CheckFileProcessor extends Processor {
    private String path;
    private String desiredOutcome;
    private String result;

    public CheckFileProcessor(HashMap<String, String> processorAttribues) {
        super(processorAttribues);
        this.setPath(processorAttribues.get("path"));
        this.setDesiredOutcome(processorAttribues.get("desiredOutcome"));
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

    @Override
    public Result run(String stepName, Result[] stuff) {
        logger.info("running " + this.getClass().getSimpleName());
        BooleanResult r = new BooleanResult();
        HashMap<String, String> resultAttributes = new HashMap<>();
        Boolean result;

        try {
            File f = new File(this.getPath());
            if(f.exists() && this.getDesiredOutcome().equals("exists")) {
                result = true;
                resultAttributes.put(stepName + "-outcome", "success");
            } else if(f.exists() && this.getDesiredOutcome().equals("not-exists")) {
                result = false;
                resultAttributes.put(stepName + "-outcome", "failure");
                resultAttributes.put(stepName + "-outcome-message", "file does exist, but is desired to not exist: " + this.getPath());
            } else  if(!f.exists() && this.desiredOutcome.equals("not-exists")) {
                result = true;
                resultAttributes.put(stepName + "-outcome", "success");
            } else  if(!f.exists() && this.desiredOutcome.equals("exists")) {
                    result = false;
                    resultAttributes.put(stepName + "-outcome", "failure");
                    resultAttributes.put(stepName + "-outcome-message", "file does not exist, but is desired to exist: " + this.getPath());
            } else {
                result = false;
                resultAttributes.put(stepName + "-outcome", "failure");
                resultAttributes.put(stepName + "-outcome-message", "unable to determine what happened!");
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


    public String getDesiredOutcome() {
        return desiredOutcome;
    }

    public void setDesiredOutcome(String desiredOutcome) {
        this.desiredOutcome = desiredOutcome;
    }
}