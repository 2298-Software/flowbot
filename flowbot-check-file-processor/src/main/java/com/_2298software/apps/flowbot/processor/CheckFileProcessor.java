package com._2298software.apps.flowbot.processor;

import com._2298software.apps.flowbot.processorcore.Processor;
import com._2298software.apps.flowbot.result.*;

import java.io.File;
import java.util.HashMap;

public class CheckFileProcessor extends Processor {
    private String path;
    private String desiredOutcome;
    private String result;

    public CheckFileProcessor(HashMap<String, String> processorAttribues) {
        super(processorAttribues);
        this.setPath(processorAttribues.get("path"));
        this.setDesiredOutcome(processorAttribues.get("desiredOutcome"));
    }

    private String getPath() {
        return path;
    }

    private void setPath(String path) {
        this.path = path;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public ProcessorResult run() {
        logger.info("running " + this.getClass().getSimpleName());
        ProcessorResult pr = new ProcessorResult(false, "");

        try {
            File f = new File(this.getPath());
            if(f.exists() && this.getDesiredOutcome().equals("exists")) {
                pr.setResult(true);
            } else if(f.exists() && this.getDesiredOutcome().equals("not-exists")) {
                pr.setResult(false);
                pr.setResultMsg("file does exist, but is desired to not exist: " + this.getPath());
            } else  if(!f.exists() && this.desiredOutcome.equals("not-exists")) {
                pr.setResult(true);
            } else  if(!f.exists() && this.desiredOutcome.equals("exists")) {
                    pr.setResult(false);
                    pr.setResultMsg("file does not exist, but is desired to exist: " + this.getPath());
            } else {
                pr.setResult(false);
                pr.setResultMsg("unable to determine what happened!");
            }
        }
        catch (Exception e) {
            pr.setResult(false);
            pr.setResultMsg(e.getMessage());
        }

        return pr;
    }


    private String getDesiredOutcome() {
        return desiredOutcome;
    }

    private void setDesiredOutcome(String desiredOutcome) {
        this.desiredOutcome = desiredOutcome;
    }
}