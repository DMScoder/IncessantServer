package com.decisivestudios;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Immortan on 8/1/2016.
 */
public class SocketBundle {
    public Socket socket;
    public PrintWriter printWriter;
    public BufferedReader bufferedReader;
    public boolean active=false;
    public String displayName = "";

    public SocketBundle(Socket socket, PrintWriter printWriter, BufferedReader bufferedReader) {
        this.socket=socket;
        this.printWriter=printWriter;
        this.bufferedReader=bufferedReader;
    }
}
