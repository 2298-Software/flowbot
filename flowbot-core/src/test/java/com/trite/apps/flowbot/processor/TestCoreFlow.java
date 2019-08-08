package com.trite.apps.flowbot.processor;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.trite.apps.flowbot.processor.HttpGetProcessor;
import com.trite.apps.flowbot.processor.HttpPostProcessor;
import com.trite.apps.flowbot.result.JsonResult;
import com.trite.apps.flowbot.result.Result;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.HashMap;

import static org.junit.Assert.assertTrue;

/**
 * Created by joe on 8/7/2019.
 */
public class TestCoreFlow {
    HttpServer httpServer;

    @Before
    public void setupTestServer() throws IOException{
        httpServer = HttpServer.create(new InetSocketAddress(8000), 0);

        httpServer.createContext("/api/endpoint", new HttpHandler() {
            public void handle(HttpExchange exchange) throws IOException {
                byte[] response = "{\"success\": true}".getBytes();
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.length);
                exchange.getResponseBody().write(response);
                exchange.close();
            }
        });

        httpServer.createContext("/api/fba", new HttpHandler() {
            public void handle(HttpExchange exchange) throws IOException {
                byte[] response = "{\"seed\": 10000}".getBytes();
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.length);
                exchange.getResponseBody().write(response);
                exchange.close();
            }
        });
        httpServer.createContext("/api/challenge", new HttpHandler() {
            public void handle(HttpExchange exchange) throws IOException {
                byte[] response = "{\"seed\": 10000}".getBytes();
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.length);
                exchange.getResponseBody().write(response);
                exchange.close();
            }
        });
        httpServer.createContext("/api/download", new HttpHandler() {
            public void handle(HttpExchange exchange) throws IOException {
                byte[] response = "{\"seed\": 10000}".getBytes();
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
    public void TestProvisioningProcess() throws SocketException {
            Result results[] = new Result[5];

        //step 1: check for lock file
        HashMap<String, String> cfpAttributes = new HashMap<>();
        cfpAttributes.put("path", "src/test/resources/files/firstboot.lock");
        cfpAttributes.put("desiredOutcome", "not-exists");
        CheckFileProcessor cfp = new CheckFileProcessor(cfpAttributes);
        Result checkFileProcessorResults = cfp.run("test", results);
        results[0] = checkFileProcessorResults;
        checkFileProcessorResults.getResultAttributes().get("test-outcome").equals("success");
        assertTrue(checkFileProcessorResults.getResultAttributes().get("test-outcome").equals("success"));

        //step 2: generate device fingerprint
        HashMap<String, String> dfAttributes = new HashMap<>();
        dfAttributes.put("interfaceName", "wlan0");
        DeviceFingerprintProcessor dfp = new DeviceFingerprintProcessor(dfAttributes);
        Result dfpProcessorResults = dfp.run("test", results);
        results[1] = dfpProcessorResults;
        assertTrue(dfpProcessorResults.getResultAttributes().get("test-outcome").equals("success"));

        //step 3: first boot announcement
        HashMap<String, String> httpAttributes = new HashMap<>();
        httpAttributes.put("url", "http://localhost:8000/api/fba");
        JsonResult r = (JsonResult)results[1];
        httpAttributes.put("payload", r.getResult());
        HttpPostProcessor p = new HttpPostProcessor(httpAttributes);
        Result httpPostProcessorResults = p.run("test", results);
        results[3] = httpPostProcessorResults;
        assertTrue(httpPostProcessorResults.getResultAttributes().get("test-outcome").equals("success"));
    }

}
