package org.example;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

//main start class

import java.net.ServerSocket;


//polaczenie kleint serwer przez socket
public class Main {
    public static void main(String[] args) throws IOException
    {
        ServerSocket serverSocket = null;
        try{
            //obiekt klasy server socket uzyty na porcie 5000 do polaczenia klient-serwer
             serverSocket = new ServerSocket(5000);
            serverSocket.setReuseAddress(true);




            while(true)
            {
                Socket clientSocket = serverSocket.accept();

                System.out.println("client connected");
                System.out.println("New client connected" + clientSocket.getInetAddress().getHostAddress());
                //tworzymy obiekt klasy ClientHandler do komunikacji
                ClientHandler client = new ClientHandler(clientSocket);
                //tworzymy nowy wątek na klienta
                new Thread(client).start();
            }



        }catch(IOException e)
        {
            e.printStackTrace();
        }
        finally {
            if(serverSocket!=null)
            {
                try{
                    serverSocket.close();
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }
            }
        }





    }
}