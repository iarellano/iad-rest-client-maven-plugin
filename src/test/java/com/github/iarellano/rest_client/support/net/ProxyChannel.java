package com.github.iarellano.rest_client.support.net;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ProxyChannel implements Runnable {

    private static final String CLRF = "\r\n";
    private final Socket client;
    private final Thread localThread;

    public ProxyChannel(Socket incomingClient) {
        this.client = incomingClient;
        this.localThread = new Thread(this);
        this.localThread.start();
    }


    @Override
    public void run() {
        System.out.println("Running client request");
        try {

            final OutputStream os = client.getOutputStream();
            final BufferedReader requestReader = new BufferedReader(new InputStreamReader(client.getInputStream()));

            final String requestLine = requestReader.readLine();
            final String method = requestLine.substring(0, requestLine.indexOf(" ")).trim();
            final String targetUrl = requestLine.substring(requestLine.indexOf(" "), requestLine.lastIndexOf(" ")).trim();
            final String targetProtocol = "HTTP/1.0";

            Map<String, String> headers = new HashMap<>();
            byte[] requestContent = readInputStream(requestReader, headers, true);

            URL url = new URL(targetUrl);
            int targetPort = url.getPort() == -1
                    ? 80
                    : url.getPort();

            Socket socket = new Socket(url.getHost(), targetPort);

            OutputStream proxiedRequest = socket.getOutputStream();
            InputStream proxiedResponse = socket.getInputStream();
            proxiedRequest.write((method + " " + url.getPath() + " " + targetProtocol + CLRF).getBytes());
            for (String headerName : headers.keySet()) {
                proxiedRequest.write((headerName + ": " + headers.get(headerName) + CLRF).getBytes());
            }
            proxiedRequest.write(CLRF.getBytes());
            if (requestContent != null) {
                proxiedRequest.write(requestContent);
            }
            proxiedRequest.flush();

            // Let's get server response
            BufferedReader responseReader = new BufferedReader(new InputStreamReader(proxiedResponse));
            Map<String, String> responseHeaders = new HashMap<>();
            String responseLine = responseReader.readLine().trim();
            byte[] severResponse = readInputStream(responseReader, responseHeaders, false);
            socket.close();

            os.write((responseLine + CLRF).getBytes());
            for (String header : responseHeaders.keySet()) {
                os.write((header + ": " + responseHeaders.get(header) + CLRF).getBytes());
            }

            if (severResponse != null && severResponse.length > 0) {
                os.write(CLRF.getBytes());
                os.write(severResponse);
            }

            os.flush();
            os.close();
            client.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public byte[] readInputStream(BufferedReader reader, Map<String, String> headers, boolean checkContentLength) throws IOException {
        String nextLine = null;
        while (!(nextLine = reader.readLine()).equals("")) {
            String headerName = nextLine.substring(0, nextLine.indexOf(":")).trim();
            String headerValue = nextLine.substring(nextLine.indexOf(":") + 1).trim();
            headers.put(headerName, headerValue);
        }
        int contentLength = getContentLength(headers);
        StringBuilder sb = null;
        if (checkContentLength == false || contentLength > 0) {
            sb = new StringBuilder();
            final int BUFFER_SIZE = 1024;
            char[] buffer = new char[BUFFER_SIZE];
            int charsRead = 0;
            while ((sb.length() == 0 || sb.length() < contentLength) && (charsRead = reader.read(buffer, 0, BUFFER_SIZE)) != -1) {
                sb.append(buffer, 0, charsRead);
            }
            reader.close();
            return sb.toString().getBytes();
        }
        return null;
    }

    public int getContentLength(Map<String, String> headers) {
        for (String headerName : headers.keySet()) {
            if (headerName.equalsIgnoreCase("content-length")) {
                return Integer.valueOf(headers.get(headerName));
            }
        }
        return 0;
    }
}
