package com._2298software.apps.flowbot.processor;

import com._2298software.apps.flowbot.processorcore.Processor;
import com._2298software.apps.flowbot.result.ProcessorResult;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.HashMap;


/**
 * The Download file processor is responsible for downloading files.
 */
public class DownloadFileProcessor extends Processor {
    private String remotePath;
    private String localPath;
    private String result;

    public DownloadFileProcessor(HashMap<String, String> processorAttribues) {
        super(processorAttribues);
        this.setLocalPath(processorAttribues.get("localPath"));
        this.setRemotePath(processorAttribues.get("remotePath"));
    }

    public String getRemotePath() {
        return remotePath;
    }

    public void setRemotePath(String remotePath) {
        this.remotePath = remotePath;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public ProcessorResult run() {
        logger.info("running " + this.getClass().getSimpleName());
        ProcessorResult pr = new ProcessorResult(false,"initial value");
        String s;

        try {
            BufferedInputStream in = new BufferedInputStream(new URL(remotePath).openStream());
            FileOutputStream fileOutputStream = new FileOutputStream(localPath);
                byte dataBuffer[] = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                    fileOutputStream.write(dataBuffer, 0, bytesRead);
                }

            File localFile = new File(localPath);
            pr.setResult(localFile.exists());
        }
        catch (Exception e) {
            pr.setResult(false);
            pr.setResultMsg(e.getMessage());
        }

        return pr;
    }



}