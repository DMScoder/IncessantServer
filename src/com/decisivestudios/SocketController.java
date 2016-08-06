package com.decisivestudios;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Immortan on 8/1/2016.
 */
public class SocketController implements Runnable{

    private ArrayList<SocketBundle> socketArrayList;

    public SocketController() {

        Thread thread = new Thread(this);
        socketArrayList = new ArrayList<SocketBundle>();
        System.out.println("Initialized");
        thread.start();
        try {
            ServerSocket listener = new ServerSocket(9090);
            while (true) {
                Thread.sleep(100);
                Socket socket = listener.accept();
                processConnection(socket);
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter writer = new PrintWriter(socket.getOutputStream(),true);
                writer.println("Connection established");
                SocketBundle socketBundle = new SocketBundle(socket,writer,reader);
                getSocketArrayList().add(socketBundle);
            }
        }catch (IOException e){
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private synchronized ArrayList<SocketBundle> getSocketArrayList(){
        return socketArrayList;
    }

    private void processConnection(Socket socket) {

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            if(!reader.readLine().equals("VerifiedUser"))
                socket.close();
            else {/*
                //Track the ip in a log file
                File file = new File("ipLogs.txt");
                if(!file.exists())
                    file.createNewFile();
                Scanner scan = new Scanner(file);
                PrintWriter fileOut = new PrintWriter(file);

                String temp;
                boolean found = false;

                while(scan.hasNext()){
                    temp = scan.next();
                    if(temp.equals(socket.getInetAddress().getHostAddress())) {
                        found=true;
                        int num = scan.nextInt();
                        fileOut.print(num+"\n");
                    }
                    else
                        fileOut.print(temp);
                }

                if(!found)
                    fileOut.println(socket.getInetAddress().getHostName()+" "+socket.getInetAddress().getHostAddress()+"1");

                //Send a response

                scan.close();
                fileOut.close();*/

                System.out.println("Connection established with "+socket.getInetAddress().getHostName());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Loop to continue interacting with all data
    private void processData() {
        while(true) {
            try {
                Thread.sleep(100);
                ArrayList<SocketBundle> socketList = getSocketArrayList();
                for (int i = 0; i < socketList.size(); i++) {
                    System.out.println("Processing");
                    SocketBundle socketBundle = socketList.get(i);
                    String string = socketBundle.bufferedReader.readLine();

                    if (string.startsWith("Entering")) {
                        string = string.substring(8, string.length());
                        socketBundle.active = true;
                        socketBundle.displayName = string;
                        System.out.println("Chat room entered by" + socketBundle.displayName);

                        for (int j = 0; j < socketList.size(); j++) {
                            SocketBundle receivingBundle = socketList.get(j);
                            if (receivingBundle != socketBundle && receivingBundle.active)
                                receivingBundle.printWriter.println(string + " has joined");
                        }
                    }

                    else if (string.startsWith("Message")) {
                        string = string.substring(7, string.length());
                        System.out.println(string + " " + socketBundle.displayName);
                        for (int j = 0; j < socketList.size(); j++) {
                            SocketBundle receivingBundle = socketList.get(j);
                            if (receivingBundle != socketBundle && receivingBundle.active)
                                receivingBundle.printWriter.println(socketBundle.displayName + ": " + string);
                        }
                    }

                    else if (string.startsWith("Leaving")) {
                        socketBundle.active = false;
                        System.out.println(socketBundle.displayName + " is leaving");
                        for (int j = 0; j < socketList.size(); j++) {
                            SocketBundle receivingBundle = socketList.get(j);
                            if (receivingBundle != socketBundle && receivingBundle.active)
                                receivingBundle.printWriter.println(socketBundle.displayName + " has left");
                        }
                    }

                    else if (string.startsWith("Exit")) {
                        socketList.remove(i);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        processData();
    }
}
