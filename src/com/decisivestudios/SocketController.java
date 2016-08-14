package com.decisivestudios;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Immortan on 8/1/2016.
 */
public class SocketController {

    private ArrayList<SocketBundle> socketArrayList;

    public SocketController() {

        System.out.println("Initialized");

        try {
            ServerSocket listener = new ServerSocket(9090);
            socketArrayList = new ArrayList<>(2);
            while (true) {
                Socket socket = listener.accept();

                //processConnection(socket);

                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter writer = new PrintWriter(socket.getOutputStream(),true);

                System.out.println("Connection established with "+socket.getInetAddress().getHostAddress());
                writer.println("Connection established");

                SocketBundle socketBundle = new SocketBundle(socket,writer,reader,this);
                getSocketArrayList().add(socketBundle);
                Thread.sleep(100);
            }
        }catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private synchronized ArrayList<SocketBundle> getSocketArrayList(){
        return socketArrayList;
    }

    public synchronized void removeSocketBundle(SocketBundle socketBundle) {
        socketArrayList.remove(socketBundle);
    }

    public synchronized void sendSearchingMessage(String string, SocketBundle host) {
        for(int i=0;i<socketArrayList.size();i++) {
            SocketBundle receiver = socketArrayList.get(i);
            if (receiver.isSearching()&&receiver!=host)
                receiver.getPrintWriter().println(string);
        }
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
}
