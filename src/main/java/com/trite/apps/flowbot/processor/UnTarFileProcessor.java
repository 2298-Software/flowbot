package com.trite.apps.flowbot.processor;

import com.trite.apps.flowbot.result.BooleanResult;
import com.trite.apps.flowbot.result.Result;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

import java.io.BufferedInputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

public class UnTarFileProcessor extends Processor {
    private String tarPath;
    private String outputPath;
    private String result;

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

    public BooleanResult run(String stepName, Result[] stuff) {
        System.out.println("Running UnTarFileProcessor");
        BooleanResult r = new BooleanResult();
        HashMap<String, String> resultAttributes = new HashMap<>();
        Boolean result;
        String s;

        try {
            File f = new File(this.outputPath);
            if(!f.exists()){
                f.mkdir();
            }

            Path pathInput = Paths.get(this.tarPath);
            Path pathOutput = Paths.get(f.getParent());

            TarArchiveInputStream tararchiveinputstream = new TarArchiveInputStream(new GzipCompressorInputStream(new BufferedInputStream( Files.newInputStream(pathInput))));

            ArchiveEntry archiveentry = null;
            while( (archiveentry = tararchiveinputstream.getNextEntry()) != null ) {
                Path pathEntryOutput = pathOutput.resolve( archiveentry.getName() );
                if( archiveentry.isDirectory() ) {
                    if( !Files.exists( pathEntryOutput ) )
                        Files.createDirectory( pathEntryOutput );
                }
                else
                    Files.copy( tararchiveinputstream, pathEntryOutput );
            }

            tararchiveinputstream.close();


            result = true;
            resultAttributes.put(stepName + "-outcome", "success");
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