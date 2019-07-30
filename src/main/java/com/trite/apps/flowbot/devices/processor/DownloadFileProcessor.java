package com.trite.apps.flowbot.devices.processor;

import com.trite.apps.flowbot.devices.result.BooleanResult;
import com.trite.apps.flowbot.devices.result.Result;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.HashMap;

public class DownloadFileProcessor extends Processor {
    private String remotePath;
    private String localPath;
    private String result;

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

    public BooleanResult run(String stepName, Result[] stuff) {
        System.out.println("Running DownloadFileProcessor");
        BooleanResult r = new BooleanResult();
        HashMap<String, String> resultAttributes = new HashMap<>();
        Boolean result;
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
            result = localFile.exists();
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