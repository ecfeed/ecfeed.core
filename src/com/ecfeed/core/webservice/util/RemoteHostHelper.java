package com.ecfeed.core.webservice.util;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class RemoteHostHelper {
	
    public boolean isHostAlive(String host, int port, int timeout) {
    	
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), timeout);
            return true;
        } catch (IOException e) {
            return false;
        }
    }	

}
