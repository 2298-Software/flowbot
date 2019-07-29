package com.trite.apps.flowbot.devices.processor;

import com.trite.apps.flowbot.devices.result.BooleanResult;
import com.trite.apps.flowbot.devices.result.Result;

import java.io.File;
import java.util.HashMap;

public class CheckFileProcessor extends Processor {
    private String path;
    private String result;

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

    public BooleanResult run(String stepName, Result[] stuff) {
        System.out.println("Running CheckFileProcessor");
        BooleanResult r = new BooleanResult();
        HashMap<String, String> resultAttributes = new HashMap<>();
        Boolean result;
        String s;

        try {
            File f = new File(this.getPath());
            result = f.exists();
            resultAttributes.put(stepName + "-outcome", "success");
        }
        catch (Exception e) {
            result = false;
            resultAttributes.put(stepName + "-outcome", "failure");
        }

        r.setResult(result);
        r.setResultAttributes(resultAttributes);
        return r;
    }


}