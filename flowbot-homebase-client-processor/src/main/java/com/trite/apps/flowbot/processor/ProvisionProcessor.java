package com.trite.apps.flowbot.processor;

import com.trite.apps.flowbot.exception.*;
import com.trite.apps.flowbot.processorcore.Processor;
import com.trite.apps.flowbot.result.BooleanResult;
import com.trite.apps.flowbot.result.Result;
import com.trite.apps.flowbot.util.Encryption;
import com.trite.apps.flowbot.util.HttpUtil;
import com.trite.apps.flowbot.util.NetworkUtil;
import com.trite.apps.flowbot.util.Util;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;

public class ProvisionProcessor extends Processor {
    private Logger logger = Logger.getLogger(this.getClass().getName());
    private String hbUrl;
    private String hbFbaGetEp;
    private String hbFbaPostEp;
    private String hbPcrepEp;
    private String firstBootLockFilePath;
    private boolean firstBoot;
    private String deviceSerialNumber;
    private String mac;
    private String osInfo;
    private String epoch;
    private String fbaB64encodedValue;
    private String interfaceName;
    private String downloadPath;
    private File tarball;

    public String getHbUrl() {
        return hbUrl;
    }

    public void setHbUrl(String hbUrl) {
        this.hbUrl = hbUrl;
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

    public String getDownloadPath() {
        return downloadPath;
    }

    public void setDownloadPath(String downloadPath) {
        this.downloadPath = downloadPath;
    }

    public File getTarball() {
        return tarball;
    }

    public void setTarball(File tarball) {
        this.tarball = tarball;
    }

    private HashMap<String, String> resultAttributes = new HashMap<>();

    public ProvisionProcessor(HashMap<String, String> processorAttribues) {
        super(processorAttribues);
        this.hbUrl = processorAttribues.get("hbUrl");
        this.hbFbaGetEp = processorAttribues.get("hbFbaGetEp");
        this.hbFbaPostEp = processorAttribues.get("hbFbaPostEp");
        this.hbPcrepEp = processorAttribues.get("hbPcrepEp");
        this.interfaceName = processorAttribues.get("interfaceName");
        this.firstBootLockFilePath = processorAttribues.get("firstBootLockFilePath");
        this.downloadPath = processorAttribues.get("downloadPath");
    }

    public BooleanResult run(String stepName, Result[] flowResults) throws Exception {
        logger.info("running " + this.getClass().getSimpleName());

        // Check for first boot lock file
        resultAttributes.put("step-1-start", "check-first-boot-lock");
        File fbLock = new File(this.firstBootLockFilePath);
        HttpUtil hu = new HttpUtil();

        try {
            if (fbLock.exists()) {
                throw new FirstBootLockFileExistsException("Unable to continue, the first boot lock file already exists");
            }
        } catch (Exception e) {
            throw new FirstBootLockFileExistsException("Unable to continue due to: " + Util.getStackTraceString(e));
        }


        try {
            // Send get to fba endpoint
            resultAttributes.put("step-2", "send-get-to-fba-endpoint-start");
            logger.info("running step-2,send-get-to-fba-endpoint-start");
            hu.setUrl(this.getHbUrl() + this.getHbFbaGetEp());
            hu.getFba();
            resultAttributes.put("step-2", "send-get-to-fba-endpoint-end");
        } catch (Exception e) {
            throw new FirstBootAnnouncementException("Unable to complete first boot announcement due to: " + Util.getStackTraceString(e));
        }


        // prepare fba payload
        resultAttributes.put("step-3", "prepare-fba-payload-start");
        String fbaPayload;
        try {
            fbaPayload = getFbaMessage();
        } catch (Exception e) {
            throw new GenerateFbaPayloadException("Unable to generate fba payload due to " + Util.getStackTraceString(e));
        }
        resultAttributes.put("step-3", "prepare-fba-payload-end");

        // send get to Fba endpoint
        final String secretKey;
        resultAttributes.put("step-4", "send-fba-payload-start");
        try {
            hu.setUrl(this.getHbUrl() + this.getHbFbaPostEp());
            hu.setPayload(fbaPayload);
            secretKey = hu.postFba();
            logger.info("secretKey: " + secretKey);
        } catch (Exception e) {
            throw new PostFbaPayloadException("Unable to generate fba payload due to " + Util.getStackTraceString(e));
        }
        resultAttributes.put("step-4", "send-fba-payload-end");

        // get encrypted payload on client side
        resultAttributes.put("step-5", "encrypt-fba-payload-start");
        String clientEncryptedPayload = Encryption.encrypt(fbaPayload, secretKey);
        logger.info("encryptedPayload: " + clientEncryptedPayload);
        resultAttributes.put("step-5", "encrypt-fba-payload-end");

        // pass encrypted payload to homebase api
        resultAttributes.put("step-6", "send-encrypted-fba-payload-challenge-start");
        String downloadUrl = getDownloadUrl(hu, clientEncryptedPayload);
        resultAttributes.put("step-6", "send-encrypted-fba-payload-challenge-end");

        //download the tarball to app path
        resultAttributes.put("step-7", "download-tarball-start");
        if (!downloadTarball(downloadUrl)) {
            throw new Exception("tarball file does not exist");
        }
        resultAttributes.put("step-7", "download-tarball-end");

        //expand the tarball
        resultAttributes.put("step-8", "extract-tarball-start");
        expandTheTarball();
        resultAttributes.put("step-8", "extract-tarball-end");

        //remove the downloaded file
        resultAttributes.put("step-9", "delete-tarball-start");
        cleanUpTempFiles();
        resultAttributes.put("step-9", "delete-tarball-end");

        resultAttributes.put("step-10", "create-lock-file-start");
        fbLock.createNewFile();
        resultAttributes.put("step-10", "create-lock-file-end");

        resultAttributes.put("end", "flow-completed");

        return new BooleanResult();
}

    private void cleanUpTempFiles() {
        tarball = new File(this.downloadPath);
        tarball.delete();
    }

    private String getDownloadUrl(HttpUtil hu, String clientEncryptedPayload) throws Exception {
        hu.setUrl(this.getHbUrl() + this.getHbPcrepEp());
        hu.setPayload(clientEncryptedPayload);
        return hu.sendPcrep();
    }

    private boolean downloadTarball(String downloadUrl) {
        try {
            BufferedInputStream in = new BufferedInputStream(new URL(downloadUrl).openStream());
            FileOutputStream fileOutputStream = new FileOutputStream(downloadPath);
            byte dataBuffer[] = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
            in.close();
            fileOutputStream.close();

            tarball = new File(this.downloadPath);
            return tarball.exists();
        } catch (ConnectException ce) {
            resultAttributes.put("step-7-outcome", "failure");
            resultAttributes.put("step-7-message", Util.getStackTraceString(ce));
            return false;
        } catch (MalformedURLException me) {
            resultAttributes.put("step-7-outcome", "failure");
            resultAttributes.put("step-7-message", Util.getStackTraceString(me));
            return false;
        } catch (IOException ie) {
            resultAttributes.put("step-7-outcome", "failure");
            resultAttributes.put("step-7-message", Util.getStackTraceString(ie));
            return false;
        }
    }

    private void expandTheTarball() throws IOException {
        Path pathInput = Paths.get(this.downloadPath);
        Path pathOutput = pathInput.getParent();

        TarArchiveInputStream tararchiveinputstream = new TarArchiveInputStream(new GzipCompressorInputStream(new BufferedInputStream(Files.newInputStream(pathInput))));

        TarArchiveEntry entry;
        while ((entry = tararchiveinputstream.getNextTarEntry()) != null) {
            if (entry.isDirectory()) {
                continue;
            }
            File curfile = new File(pathOutput.toAbsolutePath().toString(), entry.getName());
            File parent = curfile.getParentFile();
            if (!parent.exists()) {
                parent.mkdirs();
            }
            IOUtils.copy(tararchiveinputstream, new FileOutputStream(curfile));
        }

        tararchiveinputstream.close();
    }

    private String getFbaMessage() throws SocketException {
        this.setMac(NetworkUtil.getMacAddress(this.interfaceName));
        this.setOsInfo(System.getProperty("os.name") + " " + System.getProperty("os.version"));

        Date d = new Date();
        this.setEpoch(Long.toString(d.getTime()));

        this.setDeviceSerialNumber("561465165161");

        String fbaPayloadValue = String.format("{" +
                "\"macAddress\":\"%s\"," +
                "\"osInfo\":\"%s\"," +
                "\"epochTime\":\"%s\"," +
                "}", this.getMac(), this.getOsInfo(), this.getEpoch());

        logger.info("fbaPayloadValue: " + fbaPayloadValue);

        String fbaPayloadB64 = java.util.Base64.getEncoder().encode(fbaPayloadValue.getBytes()).toString();
        logger.info("fbaPayloadB64: " + fbaPayloadB64);

        String fbaPayload = String.format("{\"key\":\"%s\", \"value\":\"{%s}\"}", this.getDeviceSerialNumber(), fbaPayloadB64);
        logger.info("fbaPayload: " + fbaPayload);
        return fbaPayload;
    }


}