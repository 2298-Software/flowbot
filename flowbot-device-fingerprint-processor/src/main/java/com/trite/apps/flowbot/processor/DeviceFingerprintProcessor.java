package com.trite.apps.flowbot.processor;

import com.google.gson.Gson;
import com.trite.apps.flowbot.processorcore.Processor;
import com.trite.apps.flowbot.result.BooleanResult;
import com.trite.apps.flowbot.result.HashMapResult;
import com.trite.apps.flowbot.result.JsonResult;
import com.trite.apps.flowbot.result.Result;

import java.io.*;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

public class DeviceFingerprintProcessor extends Processor {
    private String interfaceName;
    private String result;

    public DeviceFingerprintProcessor(HashMap<String, String> processorAttribues) {
        super(processorAttribues);
        this.setInterfaceName(processorAttribues.get("interfaceName"));
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public JsonResult run(String stepName, Result[] flowResults) {
        logger.info("running " + this.getClass().getSimpleName());
        HashMap<String, String> resultAttributes = new HashMap<>();
        JsonResult result = new JsonResult();
        JsonResult r = new JsonResult();


        try {
            DeviceFingerprint df = new DeviceFingerprint(this.getInterfaceName());

            logger.info("mac is: " + df.getMac());
            logger.info("os_name is: " + df.getOs_name());
            logger.info("os_version is: " + df.getOs_version());
            logger.info("epoch is: " + df.getEpoch());

            Gson g = new Gson();

            result.setResult(g.toJson(df));
            resultAttributes.put(stepName + "-outcome", "success");
        }
        catch (Exception e) {
            resultAttributes.put(stepName + "-outcome", "failure");
            resultAttributes.put(stepName + "-outcome-message", e.getMessage());
        }

        r.setResult(result.getResult());
        r.setResultAttributes(resultAttributes);
        logger.info("finished " + this.getClass().getSimpleName());

        return r;
    }

    private String file2string(String path) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(new File(path)));
        String st;
        StringBuilder sb = new StringBuilder();
        while ((st = br.readLine()) != null){
            sb.append(st);
        }

        return sb.toString();
    }
}