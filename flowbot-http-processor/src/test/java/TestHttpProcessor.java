import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;

import com.trite.apps.flowbot.processor.HttpGetProcessor;
import com.trite.apps.flowbot.processor.HttpPostProcessor;
import com.trite.apps.flowbot.result.JsonResult;
import com.trite.apps.flowbot.result.Result;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by joe on 8/7/2019.
 */
public class TestHttpProcessor {
    HttpServer httpServer;

    @Before
    public void setupTestServer() throws IOException{
        httpServer = HttpServer.create(new InetSocketAddress(8000), 0);

        httpServer.createContext("/api/endpoint", new HttpHandler() {
            public void handle(HttpExchange exchange) throws IOException {
                byte[] response = exchange.getRequestBody().toString().getBytes();
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.length);
                exchange.getResponseBody().write(response);
                exchange.close();
            }
        });
        httpServer.start();

    }

    @After
    public void destroyTestServer() throws IOException{
        httpServer.stop(0);
    }


    @Test
    public void TestHttpPost()  {
            Result results[] = new Result[1];
            HashMap<String, String> processorAttributes = new HashMap<>();
            processorAttributes.put("url", "http://localhost:8000/api/endpoint");

            HttpPostProcessor p = new HttpPostProcessor(processorAttributes);
            Result r = p.run("test", results);

            Boolean stepSuccess = r.getResultAttributes().get("test-outcome").equals("success");

            assertTrue(stepSuccess);
    }

    @Test
    public void TestHttpGet()  {
        Result results[] = new Result[1];
        HashMap<String, String> processorAttributes = new HashMap<>();
        processorAttributes.put("url", "http://localhost:8000/api/endpoint");

        HttpGetProcessor p = new HttpGetProcessor(processorAttributes);
        Result r = p.run("test", results);

        Boolean stepSuccess = r.getResultAttributes().get("test-outcome").equals("success");

        assertTrue(stepSuccess);
    }

    @Test
    public void TestHttpPostJson()  {
        Result results[] = new Result[1];
        String jsonInputString = "{\"name\": \"joe\", \"job\": \"software engineer\"}";
        HashMap<String, String> processorAttributes = new HashMap<>();
        processorAttributes.put("url", "http://localhost:8000/api/endpoint");
        processorAttributes.put("payload", jsonInputString);
        HttpPostProcessor p = new HttpPostProcessor(processorAttributes);
        JsonResult r = p.run("test", results);

        Boolean stepSuccess = r.getResultAttributes().get("test-outcome").equals("success");
        String jsonResult = r.getResult();

        assertTrue(stepSuccess);
        assertEquals(jsonInputString, jsonResult);
}
}
