package com.github.iarellano.rest_client.support.net;

import java.io.IOException;
import java.net.ServerSocket;

public class ProxyServer {

    private final int port;
    private ServerSocket server;
    private boolean run = true;

    public ProxyServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) {
        ProxyServer proxyServer = new ProxyServer(8089);
        proxyServer.start();
    }

    public void start() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    server = new ServerSocket(port);
                    while (run) {
                        new ProxyChannel(server.accept());
                    }

                } catch (IOException e) {
                    // Do nothing
                }
            }
        }).start();
    }

    public void stop() {
        run = false;
        try {
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
