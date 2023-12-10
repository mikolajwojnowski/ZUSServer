package org.example;
import java.io.*;
import java.net.Socket;
import java.util.Random;

public class ClientHandler implements Runnable {


    private final Socket clientSocket;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }


    @Override
    public void run() {
        Jdbc sql = new Jdbc();
        PrintWriter out = null;
        BufferedReader in = null;
        try
        {

            // get the outputstream of client
            out = new PrintWriter(
                    clientSocket.getOutputStream(), true);

            // get the inputstream of client
            in = new BufferedReader(
                    new InputStreamReader(
                            clientSocket.getInputStream()));

            String line;
            while ((line = in.readLine()) != null)
            {

                // writing the received message from
                // client
                if(line.charAt(0) == 'S')
                {
                    System.out.printf(
                            " Sent from the client: %s\n",
                            line);
                    out.println(sql.zapytanie(line));
                }
                if(line.charAt(0) == 'I')
                {
                    sql.podatnikInsert(line);
                    System.out.println(line);
                    System.out.println("dodano podatnika");
                    out.println("dodano uzytkownika");
                }
                if(line.charAt(0) == 'P')
                {
                    String zapytanie = line.substring(1);
                    out.println(sql.zapytaniePesel(zapytanie));
                    System.out.println(zapytanie);
                }
//                if(line.charAt(0)=='G')
//                {
//                    System.out.println("mail sending");
//                    System.out.println(line);
//                    String email = line.substring(1);
//                    byte[] array = new byte[7];
//                    new Random().nextBytes(array);
//                    int randomNumber = new Random().nextInt(9000) + 1000;
//                    MailSender wyslij = new MailSender();
//                    wyslij.zawartość = wyslij.zawartość + randomNumber;
//                    wyslij.emailAddr = email;
//                    wyslij.uzytkownik();
//                    out.println(randomNumber);
//                }
                if(line.charAt(0)=='F')
                {
                    out.println("pobieranie pliku");
                    Jdbc select = new Jdbc();
                    String s = select.zapytaniePesel("SELECT COUNT(*) FROM LICZBA_PODA");
                    System.out.println(s);
                    FileSender send = new FileSender(s);
                    String nazwaPliku = "Podanie" + s;
                    String insert = "INSERT INTO LICZBA_PODA values ('x','"+nazwaPliku+"')";
                    select.podatnikInsert(insert);

                }
                if(line.charAt(0)=='L')
                {
                    String zapytanie = line.substring(1);
                    out.println(sql.zapytaniePeselimie(zapytanie));
                    System.out.println(zapytanie);

                }
                if(line.charAt(0)=='N')
                {
                    String zapytanie = line.substring(1);
                    out.println(sql.zapytanieNazwisko(zapytanie));
                    System.out.println(zapytanie);

                }
                if(line.charAt(0) == 'u')
                {
                    sql.podatnikInsert(line);
                    System.out.println(line);
                    System.out.println("zmieniono dane");
                    out.println("zmieniono dane");
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                    clientSocket.close();
                    System.out.println("socket closed");
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
