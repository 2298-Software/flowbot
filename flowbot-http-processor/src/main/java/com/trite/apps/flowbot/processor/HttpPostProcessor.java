package com.trite.apps.flowbot.processor;

import com.trite.apps.flowbot.processorcore.Processor;
import com.trite.apps.flowbot.result.BooleanResult;
import com.trite.apps.flowbot.result.HashMapResult;
import com.trite.apps.flowbot.result.JsonResult;
import com.trite.apps.flowbot.result.Result;
import com.trite.apps.flowbot.util.HttpUtil;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;

public class HttpPostProcessor extends Processor {
    private String url ;
    private String result;
    private String payload;

    public HttpPostProcessor(HashMap<String, String> processorAttribues) {
        super(processorAttribues);
        this.setUrl(processorAttribues.get("url"));
        this.setPayload(processorAttribues.get("payload"));
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public JsonResult run(String stepName, Result[] flowResults) {
        logger.info("running " + this.getClass().getSimpleName());
        JsonResult r = new JsonResult();
        HashMap<String, String> resultAttributes = new HashMap<>();
        JsonResult result = new JsonResult();

        try {

            HttpUtil hu = new HttpUtil();
            hu.setUrl(this.getUrl());
            hu.setPayload(this.getPayload());
            result.setResult(hu.sendPost());
            resultAttributes.put(stepName + "-outcome", "success");
        }
        catch (Exception e) {
            resultAttributes.put(stepName + "-outcome", "failure");
            resultAttributes.put(stepName + "-outcome-message", e.getMessage());
        }

        r.setResult(result.getResult());
        r.setResultAttributes(resultAttributes);
        return r;
    }


}