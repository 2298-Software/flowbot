package com._2298software.apps.flowbot.processor;

import com._2298software.apps.flowbot.processorcore.Processor;
import com._2298software.apps.flowbot.result.ProcessorResult;

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

    public ProcessorResult run() {
        logger.info("running " + this.getClass().getSimpleName());
        ProcessorResult pr = new ProcessorResult(false, "");
        String s;

        try {
            Process p = Runtime.getRuntime().exec(this.getCommand());

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

            BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            // read the output from the command
            while ((s = stdInput.readLine()) != null) {
                logger.info(s);
            }

            // read any errors from the attempted command
            while ((s = stdError.readLine()) != null) {
               logger.error(s);
            }

            pr.setResult(true);
        }
        catch (IOException e) {
            pr.setResult(false);
            pr.setResultMsg(e.getMessage());
        }

        return pr;
    }
}