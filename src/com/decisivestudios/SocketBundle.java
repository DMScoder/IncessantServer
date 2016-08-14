package com.decisivestudios;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Immortan on 8/1/2016.
 */
public class SocketBundle extends Thread{
    private Socket socket;
    private PrintWriter printWriter;
    private BufferedReader bufferedReader;
    private boolean searching = false;
    private boolean active = true;
    private String displayName = "";
    private ArrayList<SocketBundle> pairedSocketBundles;
    private SocketController socketController;

    public SocketBundle(Socket socket, PrintWriter printWriter, BufferedReader bufferedReader, SocketController socketController) {
        this.socket=socket;
        this.printWriter=printWriter;
        this.bufferedReader=bufferedReader;
        this.socketController = socketController;
        pairedSocketBundles = new ArrayList<>(1);
        this.start();
    }

    @Override
    public void run() {
        processData();
    }

    //Loop to continue interacting with all data
    private void processData() {
        while(active) {
            try {
                    String string = bufferedReader.readLine();

                    if (string.startsWith("Entering")) {
                        string = string.substring(8, string.length());
                        searching = true;
                        displayName = string;
                        System.out.println("Chat room entered by" + displayName);

                        socketController.sendSearchingMessage(string + " has joined",this);
                    }

                    else if (string.startsWith("Message")) {
                        string = string.substring(7, string.length());
                        System.out.println(displayName + ": " + string);

                        socketController.sendSearchingMessage(displayName+": "+string,this);
                    }

                    else if (string.startsWith("Leaving")) {
                        searching = false;
                        System.out.println(displayName + " is leaving");
                        socketController.sendSearchingMessage(displayName+" is leaving",this);
                    }

                    else if (string.startsWith("Exit")) {
                        searching = false;
                        System.out.println(displayName + " is leaving");
                        socketController.sendSearchingMessage(displayName+" is leaving",this);
                        active = false;
                        socket.close();
                        socketController.removeSocketBundle(this);
                    }
                Thread.sleep(100);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized Socket getSocket() {
        return socket;
    }

    public synchronized void setSocket(Socket socket) {
        this.socket = socket;
    }

    public synchronized PrintWriter getPrintWriter() {
        return printWriter;
    }

    public synchronized void setPrintWriter(PrintWriter printWriter) {
        this.printWriter = printWriter;
    }

    public synchronized BufferedReader getBufferedReader() {
        return bufferedReader;
    }

    public synchronized void setBufferedReader(BufferedReader bufferedReader) {
        this.bufferedReader = bufferedReader;
    }

    public synchronized boolean isSearching() {
        return searching;
    }

    public synchronized void setSearching(boolean searching) {
        this.searching = searching;
    }

    public synchronized String getDisplayName() {
        return displayName;
    }

    public synchronized void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public synchronized ArrayList<SocketBundle> getPairedSockets() {
        return pairedSocketBundles;
    }

    public synchronized void addPairedSocket(SocketBundle socketBundle) {
        pairedSocketBundles.add(socketBundle);
    }
}
