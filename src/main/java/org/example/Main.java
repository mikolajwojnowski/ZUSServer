package org.example;
import java.net.ServerSocket;
import java.net.Socket;

//main start class

import java.io.IOException;
import java.net.ServerSocket;

public class Main {
    public static void main(String[] args) throws Exception {

        ServerSocket server = null;

        try{
            //server is listening on port 1234
            server = new ServerSocket(1234);
            server.setReuseAddress(true);

            //runnin infinite loop for getting client request

            while(true)
            {
                Socket client = server.accept();

                System.out.println("New client connected" + client.getInetAddress().getHostAddress());
                ClientHandler clientSock = new ClientHandler(client);
                new Thread(clientSock).start();
            }

        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if(server != null){
                try{
                    server.close();
                } catch(IOException e )
                {
                    e.printStackTrace();
                }
            }
        }



    }
}