package com.trite.apps.flowbot.processor;

import com.trite.apps.flowbot.processorcore.Processor;
import com.trite.apps.flowbot.result.BooleanResult;
import com.trite.apps.flowbot.result.Result;
import com.trite.apps.flowbot.util.HttpUtil;

import java.util.HashMap;

public class HttpGetProcessor extends Processor {
    private String url ;
    private String result;

    public HttpGetProcessor(HashMap<String, String> processorAttribues) {
        super(processorAttribues);
        this.setUrl(processorAttribues.get("url"));
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

            HttpUtil hu = new HttpUtil();
            hu.setUrl(this.getUrl());
            result = hu.sendGet();

            if(result){
                result = true;
                resultAttributes.put(stepName + "-outcome", "success");
            } else {
                result = false;
                resultAttributes.put(stepName + "-outcome", "failure");
                resultAttributes.put(stepName + "-outcome-message", "Unable to post to: " + this.getUrl());
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