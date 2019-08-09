package com.trite.apps.flowbot.processor;

import com.trite.apps.flowbot.exception.FbaInvalidReponseCodeException;
import com.trite.apps.flowbot.processorcore.Processor;
import com.trite.apps.flowbot.result.BooleanResult;
import com.trite.apps.flowbot.result.Result;
import com.trite.apps.flowbot.util.Encryption;
import com.trite.apps.flowbot.util.HttpUtil;
import com.trite.apps.flowbot.util.NetworkUtil;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;

public class ProvisionProcessor extends Processor {
    private String hbUrl ;
    private String hbFbaGetEp;
    private String hbFbaPostEp;
    private String hbPcreqEp;
    private String hbPcrepEp;
    private String firstBootLockFilePath;
    private boolean firstBoot;
    private String deviceSerialNumber;
    private String mac;
    private String osInfo;
    private String epoch;
    private String fbaB64encodedValue;
    private String interfaceName;
    private String appPath;

    public String getHbUrl() {
        return hbUrl;
    }

    public void setHbUrl(String hbUrl) {
        this.hbUrl = hbUrl;
    }

    public String getHbPcreqEp() {
        return hbPcreqEp;
    }

    public void setHbPcreqEp(String hbPcreq) {
        this.hbPcreqEp = hbPcreq;
    }

    public String getHbPcrepEp() {
        return hbPcrepEp;
    }

    public void setHbPcrepEp(String hbPcrepEp) {
        this.hbPcrepEp = hbPcrepEp;
    }

    public String getFirstBootLockFilePath() {
        return firstBootLockFilePath;
    }

    public void setFirstBootLockFilePath(String firstBootLockFilePath) {
        this.firstBootLockFilePath = firstBootLockFilePath;
    }

    public boolean isFirstBoot() {
        return firstBoot;
    }

    public void setFirstBoot(boolean firstBoot) {
        this.firstBoot = firstBoot;
    }

    public String getDeviceSerialNumber() {
        return deviceSerialNumber;
    }

    public void setDeviceSerialNumber(String deviceSerialNumber) {
        this.deviceSerialNumber = deviceSerialNumber;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getOsInfo() {
        return osInfo;
    }

    public void setOsInfo(String osInfo) {
        this.osInfo = osInfo;
    }

    public String getEpoch() {
        return epoch;
    }

    public void setEpoch(String epoch) {
        this.epoch = epoch;
    }

    public String getFbaB64encodedValue() {
        return fbaB64encodedValue;
    }

    public void setFbaB64encodedValue(String fbaB64encodedValue) {
        this.fbaB64encodedValue = fbaB64encodedValue;
    }

    public String getHbFbaGetEp() {
        return hbFbaGetEp;
    }

    public void setHbFbaGetEp(String hbFbaGetEp) {
        this.hbFbaGetEp = hbFbaGetEp;
    }

    public String getHbFbaPostEp() {
        return hbFbaPostEp;
    }

    public void setHbFbaPostEp(String hbFbaPostEp) {
        this.hbFbaPostEp = hbFbaPostEp;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getAppPath() {
        return appPath;
    }

    public void setAppPath(String appPath) {
        this.appPath = appPath;
    }

    public ProvisionProcessor(HashMap<String, String> processorAttribues) {
        super(processorAttribues);
        this.hbUrl = processorAttribues.get("hbUrl");
        this.hbFbaGetEp = processorAttribues.get("hbFbaGetEp");
        this.hbFbaPostEp = processorAttribues.get("hbFbaPostEp");
        this.hbPcreqEp = processorAttribues.get("hbPcreqEp");
        this.hbPcrepEp = processorAttribues.get("hbPcrepEp");
        this.interfaceName = processorAttribues.get("interfaceName");
        this.firstBootLockFilePath = processorAttribues.get("firstBootLockFilePath");
        this.appPath = processorAttribues.get("appPath");

    }

    public BooleanResult run(String stepName, Result[] flowResults) {
        logger.info("running " + this.getClass().getSimpleName());
        BooleanResult r = new BooleanResult();
        HashMap<String, String> resultAttributes = new HashMap<>();
        Boolean result;

        try {

            // Check for first boot lock file
            File fbLock = new File(this.firstBootLockFilePath);
            if(!fbLock.exists()){
                firstBoot = true;
            }

            // Send get to fba endpoint
            HttpUtil hu = new HttpUtil();
            hu.setUrl(this.getHbUrl() + this.getHbFbaGetEp());
            hu.getFba();

            /** prepare fba payload */
            this.setMac(NetworkUtil.getMacAddress(this.interfaceName));
            this.setOsInfo(System.getProperty("os.name") + " " + System.getProperty("os.version"));

            Date d = new Date();
            this.setEpoch(Long.toString(d.getTime()));

            this.setDeviceSerialNumber("561465165161");

            String fbaPayloadValue = String.format("{" +
                                        "\"macAddress\":\"%s\"," +
                                        "\"osInfo\":\"%s\"," +
                                        "\"epochTime\":\"%s\"," +
                                        "}",  this.getMac(), this.getOsInfo(), this.getEpoch());

            logger.info("fbaPayloadValue: " + fbaPayloadValue);

            String fbaPayloadB64 = java.util.Base64.getEncoder().encode(fbaPayloadValue.getBytes()).toString();
            logger.info("fbaPayloadB64: " + fbaPayloadB64);

            String fbaPayload = String.format("{\"key\":\"%s\", \"value\":\"{%s}\"}", this.getDeviceSerialNumber(), fbaPayloadB64);
            logger.info("fbaPayload: " + fbaPayload);

            // send get to Fba endpoint
            hu.setUrl(this.getHbUrl() + this.getHbFbaPostEp());
            hu.setPayload(fbaPayload);
            final String secretKey = hu.postFba();
            logger.info("secretKey: " + secretKey);

            // get encrypted payload on client side

            String clientEncryptedPayload = Encryption.encrypt(fbaPayload, secretKey);
            logger.info("encryptedPayload: " + clientEncryptedPayload);

            // pass encrypted payload to homebase api
            hu.setUrl(this.getHbUrl() + this.getHbPcreqEp());
            hu.setPayload(clientEncryptedPayload);
            String downloadUrl = hu.sendPcrep();

            //download the tarball to app path
            BufferedInputStream in = new BufferedInputStream(new URL(downloadUrl).openStream());
            FileOutputStream fileOutputStream = new FileOutputStream(appPath);
            byte dataBuffer[] = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }

            //expand the tarball
            Path pathInput = Paths.get(this.appPath);

            TarArchiveInputStream tararchiveinputstream = new TarArchiveInputStream(new GzipCompressorInputStream(new BufferedInputStream( Files.newInputStream(pathInput))));

            TarArchiveEntry entry;
            while ((entry = tararchiveinputstream.getNextTarEntry()) != null) {
                if (entry.isDirectory()) {
                    continue;
                }
                File curfile = new File(appPath, entry.getName());
                File parent = curfile.getParentFile();
                if (!parent.exists()) {
                    parent.mkdirs();
                }
                IOUtils.copy(tararchiveinputstream, new FileOutputStream(curfile));
            }


            tararchiveinputstream.close();

            result = true;
            if(result){
                result = true;
                resultAttributes.put(stepName + "-outcome", "success");
            } else {
                result = false;
                resultAttributes.put(stepName + "-outcome", "failure");
                resultAttributes.put(stepName + "-outcome-message", "Unable to post to: " + this.getHbUrl());
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