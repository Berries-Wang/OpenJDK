package com.greetings;

import com.socket.NetworkSocket;
/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        NetworkSocket s = NetworkSocket.open();
            System.out.println(s.getClass());
    }
}
