package com._2298software.apps.flowbot.processor;

import com._2298software.apps.flowbot.processorcore.Processor;
import com._2298software.apps.flowbot.result.ProcessorResult;
import com._2298software.apps.flowbot.util.HttpUtil;

import java.util.HashMap;

public class HttpGetProcessor extends Processor {
    private String url;
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

    public ProcessorResult run() {
        logger.info("running " + this.getClass().getSimpleName());
        ProcessorResult pr = new ProcessorResult(false, "initial value");

        try {

            HttpUtil hu = new HttpUtil();
            hu.setUrl(this.getUrl());

            if (hu.sendGet()) {
                pr.setResult(true);
            } else {
                pr.setResult(false);
                pr.setResultMsg("Unable to post to: " + this.getUrl());
            }

        } catch (Exception e) {
            pr.setResult(false);
            pr.setResultMsg(e.getMessage());
        }

        return pr;
    }


}