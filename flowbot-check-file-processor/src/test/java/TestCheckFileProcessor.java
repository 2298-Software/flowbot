import com._2298software.apps.flowbot.processor.CheckFileProcessor;

import com._2298software.apps.flowbot.result.ProcessorResult;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestCheckFileProcessor {
    @Test
    public void TestFileExistsAndDesiredExists() throws IOException {
        File testFile = File.createTempFile("flowbot-test", ".txt");

        HashMap<String, String> processorAttributes = new HashMap<>();
        processorAttributes.put("path", testFile.getAbsolutePath());
        processorAttributes.put("desiredOutcome", "exists");

        CheckFileProcessor p = new CheckFileProcessor(processorAttributes);
        ProcessorResult pr = p.run();
        assertTrue(pr.getResult());
    }

    @Test
    public void TestFileDoesNotExistsAndDesiredExists() throws IOException {
        File testFile = File.createTempFile("flowbot-test", ".txt");

        HashMap<String, String> processorAttributes = new HashMap<>();
        processorAttributes.put("path", testFile.getAbsolutePath());
        processorAttributes.put("desiredOutcome", "exists");

        CheckFileProcessor p = new CheckFileProcessor(processorAttributes);

        //remove the file prior to run
        testFile.delete();

        ProcessorResult pr = p.run();
        assertFalse(pr.getResult());
    }

    @Test
    public void TestFileExistsAndDesiredNotExists() throws IOException {
        File testFile = File.createTempFile("flowbot-test", ".txt");

        HashMap<String, String> processorAttributes = new HashMap<>();
        processorAttributes.put("path", testFile.getAbsolutePath());
        processorAttributes.put("desiredOutcome", "not-exists");

        CheckFileProcessor p = new CheckFileProcessor(processorAttributes);
        ProcessorResult pr = p.run();
        assertFalse(pr.getResult());
    }
}

