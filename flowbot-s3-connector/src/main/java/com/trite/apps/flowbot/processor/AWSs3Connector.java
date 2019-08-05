package com.trite.apps.flowbot.processor;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.trite.apps.flowbot.processorcore.Processor;
import com.trite.apps.flowbot.result.BooleanResult;
import com.trite.apps.flowbot.result.Result;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;

public class AWSs3Connector extends Processor {
    private String bucketName ;
    private String keyName;
    private String outputPath;
    private String result;

    public AWSs3Connector(HashMap<String, String> processorAttribues) {
        super(processorAttribues);
        this.setBucketName(processorAttribues.get("bucketName"));
        this.setKeyName(processorAttribues.get("keyName"));
        this.setOutputPath(processorAttribues.get("outputPath"));
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
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

            AmazonS3 s3Client = new AmazonS3Client(new ProfileCredentialsProvider());
            S3Object fileObj = s3Client.getObject(new GetObjectRequest(this.getBucketName(), this.getKeyName()));


            BufferedInputStream in = new BufferedInputStream(fileObj.getObjectContent());

            FileOutputStream fileOutputStream = new FileOutputStream(this.getOutputPath());
            byte dataBuffer[] = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }

            File localFile = new File(this.getOutputPath());
            result = localFile.exists();

            if(result){
                result = true;
                resultAttributes.put(stepName + "-outcome", "success");
            } else {
                result = false;
                resultAttributes.put(stepName + "-outcome", "failure");
                resultAttributes.put(stepName + "-outcome-message", "Output path was not created: " + this.getOutputPath());
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