package com._2298software.apps.flowbot.processor;

import com._2298software.apps.flowbot.result.ProcessorResult;
import com.google.gson.Gson;
import com._2298software.apps.flowbot.processorcore.Processor;

import java.io.*;
import java.util.HashMap;

public class DeviceFingerprintProcessor extends Processor {
    private String interfaceName;

    public DeviceFingerprintProcessor(HashMap<String, String> processorAttribues) {
        super(processorAttribues);
        this.setInterfaceName(processorAttribues.get("interfaceName"));
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public ProcessorResult run(String stepName) {
        logger.info("running " + this.getClass().getSimpleName());
        ProcessorResult result = new ProcessorResult(false, "");

        try {
            DeviceFingerprint df = new DeviceFingerprint(this.getInterfaceName());

            logger.info("mac is: " + df.getMac());
            logger.info("os_name is: " + df.getOs_name());
            logger.info("os_version is: " + df.getOs_version());
            logger.info("epoch is: " + df.getEpoch());

            Gson g = new Gson();

            //(g.toJson(df)) TODO save this to file

            result.setResult(true);
        }
        catch (Exception e) {
            result.setResult(false);
            result.setResultMsg(e.getMessage());
        }

        logger.info("finished " + this.getClass().getSimpleName());
        return result;
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