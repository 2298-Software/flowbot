import com._2298software.apps.flowbot.result.ProcessorResult;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;

import com._2298software.apps.flowbot.processor.HttpGetProcessor;
import com._2298software.apps.flowbot.processor.HttpPostProcessor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
            HashMap<String, String> processorAttributes = new HashMap<>();
            processorAttributes.put("url", "http://localhost:8000/api/endpoint");

            HttpPostProcessor p = new HttpPostProcessor(processorAttributes);
            ProcessorResult r = p.run();
            assertTrue(r.getResult());
    }

    @Test
    public void TestHttpGet()  {
        HashMap<String, String> processorAttributes = new HashMap<>();
        processorAttributes.put("url", "http://localhost:8000/api/endpoint");

        HttpGetProcessor p = new HttpGetProcessor(processorAttributes);
        ProcessorResult r = p.run();
        assertTrue(r.getResult());
    }

}
