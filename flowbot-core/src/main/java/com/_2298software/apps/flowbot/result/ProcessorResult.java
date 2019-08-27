package com._2298software.apps.flowbot.result;

public class ProcessorResult {
    private boolean result;
    private String resultMsg;

    public ProcessorResult(boolean result, String resultMsg) {
        this.result = result;
        this.resultMsg = resultMsg;
    }

    public boolean getResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }
}