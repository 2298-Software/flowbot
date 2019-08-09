package com.trite.apps.flowbot.processor;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.trite.apps.flowbot.result.Result;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestProvisionProcessor {
    HttpServer httpServer;

    @Before
    public void setupTestServer() throws IOException{
        httpServer = HttpServer.create(new InetSocketAddress(8000), 0);

        httpServer.createContext("/api/fba/post", new HttpHandler() {
            public void handle(HttpExchange exchange) throws IOException {
                byte[] response = "ssshhhhhhhhhhh!!!!".getBytes();
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.length);
                exchange.getResponseBody().write(response);
                exchange.close();
            }
        })   ;

        httpServer.createContext("/api/fba/get", new HttpHandler() {
            public void handle(HttpExchange exchange) throws IOException {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_UNAUTHORIZED,0 );
                exchange.getResponseBody().write("".getBytes());
                exchange.close();
            }
        })   ;

        httpServer.createContext("/api/hbPcreq", new HttpHandler() {
            public void handle(HttpExchange exchange) throws IOException {
                byte[] response = "https://www-eu.apache.org/dist/nifi/minifi/0.5.0/minifi-toolkit-0.5.0-bin.tar.gz".getBytes();
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.length);
                exchange.getResponseBody().write(response);
                exchange.close();
            }
        });
        httpServer.createContext("/api/hbPcrepEp", new HttpHandler() {
            public void handle(HttpExchange exchange) throws IOException {
                byte[] response = "https://www-eu.apache.org/dist/nifi/minifi/0.5.0/minifi-toolkit-0.5.0-bin.tar.gz".getBytes();
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
    public void TestFba()  {
            Result results[] = new Result[1];
            HashMap<String, String> processorAttributes = new HashMap<>();
            processorAttributes.put("hbUrl", "http://localhost:8000");
            processorAttributes.put("hbFbaGetEp", "/api/fba/get");
            processorAttributes.put("hbFbaPostEp", "/api/fba/post");
            processorAttributes.put("hbPcreqEp", "/api/hbPcreq");
            processorAttributes.put("hbPcrepEp", "/api/hbPcrep");
            processorAttributes.put("interfaceName", "wlan0");
            processorAttributes.put("firstBootLockFilePath","flowbot-homebase-client-processor/src/test/resources/firstBoot.lock");
            processorAttributes.put("appPath","src/test/resources/app");

            File f = new File(processorAttributes.get("appPath"));
        if (f.exists()) {
            f.delete();
        }
            f.mkdir();
        f.setWritable(true);

            ProvisionProcessor p = new ProvisionProcessor(processorAttributes);
            Result r = p.run("test", results);

            Boolean stepSuccess = r.getResultAttributes().get("test-outcome").equals("success");

            assertTrue(stepSuccess);
    }

}
