import com.trite.apps.flowbot.processor.CheckFileProcessor;
import com.trite.apps.flowbot.result.BooleanResult;
import com.trite.apps.flowbot.result.Result;
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

        Result results[] = new Result[1];
        HashMap<String, String> processorAttributes = new HashMap<>();
        processorAttributes.put("path", testFile.getAbsolutePath());
        processorAttributes.put("desiredOutcome", "exists");

        CheckFileProcessor p = new CheckFileProcessor(processorAttributes);
        Result r = p.run("test", results);

        Boolean stepSuccess = r.getResultAttributes().get("test-outcome").equals("success");

        assertTrue(stepSuccess);
    }

    @Test
    public void TestFileDoesNotExistsAndDesiredExists() throws IOException {
        File testFile = File.createTempFile("flowbot-test", ".txt");

        Result results[] = new Result[1];
        HashMap<String, String> processorAttributes = new HashMap<>();
        processorAttributes.put("path", testFile.getAbsolutePath());
        processorAttributes.put("desiredOutcome", "exists");

        CheckFileProcessor p = new CheckFileProcessor(processorAttributes);

        //remove the file prior to run
        testFile.delete();
        Result r = p.run("test", results);

        Boolean stepSuccess = r.getResultAttributes().get("test-outcome").equals("success");

        assertFalse(stepSuccess);
    }

    @Test
    public void TestFileExistsAndDesiredNotExists() throws IOException {
        File testFile = File.createTempFile("flowbot-test", ".txt");

        Result results[] = new Result[1];
        HashMap<String, String> processorAttributes = new HashMap<>();
        processorAttributes.put("path", testFile.getAbsolutePath());
        processorAttributes.put("desiredOutcome", "not-exists");

        CheckFileProcessor p = new CheckFileProcessor(processorAttributes);
        Result r = p.run("test", results);

        Boolean stepSuccess = r.getResultAttributes().get("test-outcome").equals("success");

        assertFalse(stepSuccess);
    }
}

