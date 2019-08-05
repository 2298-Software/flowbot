package com.trite.apps.flowbot.processor;

import com.trite.apps.flowbot.processorcore.Processor;
import com.trite.apps.flowbot.result.HashMapResult;
import com.trite.apps.flowbot.result.Result;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class CommandProcessor extends Processor {
    private String command;
    private String result;

    public CommandProcessor(HashMap<String, String> processorAttribues) {
        super(processorAttribues);
        this.command = processorAttribues.get("command");
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public HashMapResult run(String stepName, Result[] flowResults) {
        logger.info("running " + this.getClass().getSimpleName());
        HashMap<String, String> result = new HashMap<>();
        HashMap<String, String> resultAttributes = new HashMap<>();

        String s;

        try {
            Process p = Runtime.getRuntime().exec(this.getCommand());

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

            BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            // read the output from the command
            StringBuilder stdOut = new StringBuilder();
            while ((s = stdInput.readLine()) != null) {
                stdOut.append(s);
                stdOut.append(System.lineSeparator());
            }
            result.put("stdOut", stdOut.toString());

            // read any errors from the attempted command
            StringBuilder stdErr = new StringBuilder();
            while ((s = stdError.readLine()) != null) {
               stdErr.append(s);
               stdErr.append(System.lineSeparator());
            }
            result.put("stdErr", stdErr.toString());

            resultAttributes.put(stepName + "-outcome", "success");

        }
        catch (IOException e) {
            resultAttributes.put(stepName + "-outcomeMessage", e.getMessage());
            resultAttributes.put(stepName + "-outcome", "failure");
        }
        HashMapResult r = new HashMapResult();

        r.setResultAttributes(resultAttributes);
        r.setResult(result);
        return r;
    }


}