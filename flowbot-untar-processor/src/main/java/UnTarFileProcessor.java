import com.trite.apps.flowbot.processor.Processor;
import com.trite.apps.flowbot.result.BooleanResult;
import com.trite.apps.flowbot.result.Result;
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
            Path pathInput = Paths.get(this.tarPath);
            Path pathOutput = Paths.get(this.outputPath);

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