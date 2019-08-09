package com.trite.apps.flowbot.util;

import com.trite.apps.flowbot.exception.FbaInvalidReponseCodeException;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by joe on 8/7/2019.
 */
public class HttpUtil {
    public Logger logger = Logger.getLogger(this.getClass().getName());

    String url;
    String payload;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    // HTTP GET request
    public boolean sendGet() throws Exception {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        //add request header
        //con.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        System.out.println(response.toString());

        return responseCode == 200;
    }

    // HTTP POST request
    public String sendPost() throws Exception {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        //add reuqest header
        con.setRequestMethod("POST");
        //con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        con.setRequestProperty("Content-Type", "application/json; utf-8");
        con.setRequestProperty("Accept", "application/json");


        // Send post request
        con.setDoOutput(true);

        logger.info("\nSending 'POST' request to URL : " + url);

        try(OutputStream os = con.getOutputStream()) {
            byte[] input = this.getPayload().getBytes("utf-8");
            os.write(input, 0, input.length);
        }
        int responseCode = con.getResponseCode();
        logger.info("Response Code : " + responseCode);

        StringBuilder response = new StringBuilder();

        try(BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }


        logger.info("Response : " + response.toString());


        return response.toString();

    }

    // HTTP POST request
    public String postFba() throws Exception {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        //add reuqest header
        con.setRequestMethod("POST");
        //con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        con.setRequestProperty("Content-Type", "application/json; utf-8");
        con.setRequestProperty("Accept", "text/plain");

        // Send post request
        con.setDoOutput(true);

        logger.info("\nSending 'POST' request to URL : " + url);

        try(OutputStream os = con.getOutputStream()) {
            byte[] input = this.getPayload().getBytes("utf-8");
            os.write(input, 0, input.length);
        }
        int responseCode = con.getResponseCode();
        logger.info("Response Code : " + responseCode);

        if(responseCode != 200){
            throw new FbaInvalidReponseCodeException("Client expecting 200 but got " + responseCode);
        }

        StringBuilder response = new StringBuilder();

        try(BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }
        return response.toString();
    }

    // HTTP GET request
    public boolean getFba() throws Exception {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        //add reuqest header
        con.setRequestMethod("GET");
        //con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        con.setDoOutput(false);

        logger.info("\nSending 'GET' request to URL : " + url);

        int responseCode = con.getResponseCode();
        logger.info("Response Code : " + responseCode);

        if(responseCode != 401){
            throw new FbaInvalidReponseCodeException("Client expecting 401 but got " + responseCode);
        }

        return true;
    }


    // HTTP POST request
    public String sendPcrep() throws Exception {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        //add reuqest header
        con.setRequestMethod("POST");
        //con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        con.setRequestProperty("Content-Type", "text/plaintext; utf-8");
        con.setRequestProperty("Accept", "text/plaintext");


        // Send post request
        con.setDoOutput(true);

        logger.info("\nSending 'POST' request to URL : " + url);

        try(OutputStream os = con.getOutputStream()) {
            byte[] input = this.getPayload().getBytes("utf-8");
            os.write(input, 0, input.length);
        }
        int responseCode = con.getResponseCode();
        logger.info("Response Code : " + responseCode);

        if(responseCode != 200){
            throw new FbaInvalidReponseCodeException("Client expecting 200 but got " + responseCode);
        }
        StringBuilder response = new StringBuilder();

        try(BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }
        return response.toString();
    }

}
