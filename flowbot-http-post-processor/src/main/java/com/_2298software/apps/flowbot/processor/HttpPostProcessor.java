package com._2298software.apps.flowbot.processor;

import com._2298software.apps.flowbot.processorcore.Processor;
import com._2298software.apps.flowbot.result.ProcessorResult;
import com._2298software.apps.flowbot.util.HttpUtil;

import java.util.HashMap;

public class HttpPostProcessor extends Processor {
    private String url;
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

    public ProcessorResult run() {
        logger.info("running " + this.getClass().getSimpleName());
        ProcessorResult pr = new ProcessorResult(false, "initial value");

        try {
            HttpUtil hu = new HttpUtil();
            hu.setUrl(this.getUrl());
            hu.setPayload(this.getPayload());
            pr.setResult(hu.sendPost());
        } catch (Exception e) {
            pr.setResult(false);
            pr.setResultMsg(e.getMessage());
        }

        return pr;
    }
}