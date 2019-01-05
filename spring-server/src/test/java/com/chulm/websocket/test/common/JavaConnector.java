package com.chulm.websocket.test.common;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class JavaConnector {

    private HttpURLConnection connection = null;

    public void connect(String host, int port) throws IOException {
        URL url = new URL("http://" + host + ":" + port + "/topic");
        connection = (HttpURLConnection)url.openConnection();

        connection.setRequestMethod("GET");
        connection.setRequestProperty("Connection", "Keep-alive");
        connection.setRequestProperty("Host","loalhost:8080");

        connection.setUseCaches(false);
        connection.setDoOutput(true);
        connection.setDoInput(true);

        int responseCode = connection.getResponseCode();

        System.out.println("Response Code :" + responseCode);

    }
}
