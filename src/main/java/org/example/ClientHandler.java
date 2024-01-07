package org.example;
import java.io.*;
import java.net.Socket;
import java.sql.*;
import java.util.Random;
import org.mindrot.jbcrypt.BCrypt;


public class ClientHandler implements Runnable {


    private final Socket clientSocket;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    //tutaj obsluga zapytania po stronie serwera i zabezpieczenia dt zapytan
    @Override
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(),true);


            // Odczytanie typu polecenia od klienta
            String commandType = reader.readLine();

            //odczytanie zapytania od klienta
            String zapytanieOdKlienta = reader.readLine();

            //filtracja danych od klietna
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:orcl","system","student");

            switch(commandType)
            {
                case "SELECT":
                    handleSelectQuery(zapytanieOdKlienta,connection,writer);
                    break;
                case "INSERT":
                    break;
                case "UPDATE":
                    break;
                case "REGISTER":
                    handleRegistrationQuery(zapytanieOdKlienta,connection,writer);
                    break;
                case "REGISTER_SUCCESS":
                    String[] userData = zapytanieOdKlienta.split(" ");
                    handleRegisterSuccess(userData[0],userData[1],userData[2],userData[3],userData[4],connection,writer);
                    break;
                case "LOGIN":
                    handleLoginQuery(zapytanieOdKlienta,connection,writer);
                default:
                    writer.println("unsupported command type");

            }




            //zamkniecie zasobow

            connection.close();
            writer.close();
            reader.close();
            clientSocket.close();



        }

        catch(IOException | SQLException | ClassNotFoundException e){
            e.printStackTrace();

        }




    }

    //TODO dla jakiegos zapytania
    private void handleSelectQuery(String query, Connection connection, PrintWriter writer) throws SQLException {

        String zapytanieDoBazy = "SELECT * FROM Podatnik WHERE kolumna = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(zapytanieDoBazy);
        preparedStatement.setString(1, query);

        // Wykonanie bezpiecznego zapytania do bazy danych
        ResultSet resultSet = preparedStatement.executeQuery();

        // Przetwarzanie wyników i wysyłanie odpowiedzi do klienta
        while (resultSet.next()) {
            // Przykładowe przetwarzanie wyników
            String wynik = resultSet.getString("kolumna");
            // Możesz wysłać wynik do klienta
            writer.println(wynik);
        }

        // Zamknięcie zasobów
        resultSet.close();
        preparedStatement.close();
    }

    //FUNKCJA DO INSERT TODO insert
    private void handleInsertQuery(String query, Connection connection, PrintWriter writer) throws SQLException {
        // Obsługa zapytania INSERT
        // ...
        // Możesz dostosować tę część kodu do obsługi zapytania INSERT
        writer.println("INSERT operation executed");
    }


    //funkcja do UPDATE TODO update
    private void handleUpdateQuery(String query, Connection connection, PrintWriter writer) throws SQLException {
        // Obsługa zapytania UPDATE
        // ...
        // Możesz dostosować tę część kodu do obsługi zapytania UPDATE
        writer.println("UPDATE operation executed");
    }


    //FUNKCJA DO SPRAWDZENIE CZY PESEL JEST JUZ ZAREJESTROWANY
    private void handleRegistrationQuery(String pesel, Connection connection, PrintWriter writer) throws SQLException {
        String zapytanieDoBazy = "SELECT * FROM Podatnik WHERE pesel = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(zapytanieDoBazy);
        preparedStatement.setString(1, pesel);

        // Wykonanie bezpiecznego zapytania do bazy danych
        ResultSet resultSet = preparedStatement.executeQuery();

        // Przetwarzanie wyników i wysyłanie odpowiedzi do klienta
        if (resultSet.next()) {
            // Jeżeli istnieje użytkownik o podanym PESEL, wysyłamy informację do klienta
            writer.println("false");
        } else {
            // W przeciwnym razie informujemy klienta, że PESEL jest dostępny
            writer.println("true");
        }

        // Zamknięcie zasobów
        resultSet.close();
        preparedStatement.close();
    }

    //FUNKCJA DO PRZESLANIA DANYCH GDY REJESTRACJA PRZEBIEGLA POMYSLNIE
    private void handleRegisterSuccess(String pesel, String haslo, String email, String imie, String nazwisko, Connection connection, PrintWriter writer) throws SQLException {
        // Insert user information into the database
        String insertQuery = "INSERT INTO Podatnik (pesel, haslo, email, imie, nazwisko) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
        preparedStatement.setString(1, pesel);
        preparedStatement.setString(2, haslo);
        preparedStatement.setString(3, email);
        preparedStatement.setString(4, imie);
        preparedStatement.setString(5, nazwisko);

        // Execute the insert query
        int rowsAffected = preparedStatement.executeUpdate();

        // Check if the registration was successful
        if (rowsAffected > 0) {
            writer.println("Registration successful");
        } else {
            writer.println("Registration failed. Please try again.");
        }

        // Close resources
        preparedStatement.close();
    }

    private void handleLoginQuery(String userData, Connection connection, PrintWriter writer) throws SQLException {
        // Split user data received from the client
        String[] userDataArray = userData.split(" ");

        // Extract pesel and password from user data
        String pesel = userDataArray[0];
        String haslo = userDataArray[1];  // Assuming password is at the fifth position in the string

        // Query to check if user with given pesel and password exists in the database
        String loginQuery = "SELECT * FROM Podatnik WHERE pesel = ? AND haslo = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(loginQuery);
        preparedStatement.setString(1, pesel);
        preparedStatement.setString(2, haslo);

        // Execute the login query
        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            // User with pesel exists, now compare the passwords
            String storedPassword = resultSet.getString("haslo");

            if (checkPassword(haslo, storedPassword)) {
                // Passwords match, login successful
                writer.println("success");
            } else {
                // Passwords don't match, login failed
                writer.println("Login failed. Please check your credentials.");
            }
        } else {
            // User with given pesel not found
            writer.println("User not found. Please check your credentials.");
        }

        // Close resources
        resultSet.close();
        preparedStatement.close();
    }

    //checking password TODO hashing for security reasons now simple equals for string comaprision
    private boolean checkPassword(String enteredPassword, String storedPassword) {
        return enteredPassword.equals(storedPassword);
    }



}
