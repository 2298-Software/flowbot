package com._2298software.apps.flowbot.processor;

import com._2298software.apps.flowbot.processorcore.Processor;
import com._2298software.apps.flowbot.result.ProcessorResult;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

public class UnTarFileProcessor extends Processor {
    private String tarPath;
    private String outputPath;
    private String result;

    public UnTarFileProcessor(HashMap<String, String> processorAttribues) {
        super(processorAttribues);
        this.setTarPath(processorAttribues.get("tarPath"));
        this.setOutputPath(processorAttribues.get("outputPath"));
    }

    public String getTarPath() {
        return tarPath;
    }

    public void setTarPath(String tarPath) {
        this.tarPath = tarPath;
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

    public ProcessorResult run() {
        logger.info("running " + this.getClass().getSimpleName());
        ProcessorResult pr = new ProcessorResult(false,"");
        String s;

        try {
            Path pathInput = Paths.get(this.tarPath);

            TarArchiveInputStream tararchiveinputstream = new TarArchiveInputStream(new GzipCompressorInputStream(new BufferedInputStream( Files.newInputStream(pathInput))));

                TarArchiveEntry entry;
                while ((entry = tararchiveinputstream.getNextTarEntry()) != null) {
                    if (entry.isDirectory()) {
                        continue;
                    }
                    File curfile = new File(outputPath, entry.getName());
                    File parent = curfile.getParentFile();
                    if (!parent.exists()) {
                        parent.mkdirs();
                    }
                    IOUtils.copy(tararchiveinputstream, new FileOutputStream(curfile));
                }


            tararchiveinputstream.close();


            pr.setResult(true);
        }
        catch (Exception e) {
            pr.setResult(false);
            pr.setResultMsg(e.getMessage());
        }

        return pr;
    }


}