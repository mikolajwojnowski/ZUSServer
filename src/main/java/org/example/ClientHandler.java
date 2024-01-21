package org.example;
import java.io.*;
import java.net.Socket;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
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
                   // handleSelectQuery(zapytanieOdKlienta,connection,writer);
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
                    handleRegisterSuccess(userData[0],userData[1],userData[2],userData[3],userData[4],userData[5],userData[6],connection,writer);
                    break;
                case "LOGIN":
                    handleLoginQuery(zapytanieOdKlienta,connection,writer);
                    break;
                case "FORGOT_PASSWORD":
                    ForgotPassword(zapytanieOdKlienta,connection,writer);
                    break;
                case "UPDATE_PASSWORD":
                    updatePassword(zapytanieOdKlienta,connection,writer);
                    break;
                case "PROFILE_INFO":
                    ProfileInfoQuery(zapytanieOdKlienta,connection,writer);
                    break;
                case "OPLAC_SKLADKE":
                    OplacSkladke(zapytanieOdKlienta,connection,writer);
                    break;
                case "WYSLIJ_PODANIE":
                    WyslijPodanie(zapytanieOdKlienta,connection,writer);
                    break;
                case "WPLATY_INFO":
                    WplatyInfo(zapytanieOdKlienta,connection,writer);
                    break;
                case "WPLATY_INFO_AVG":
                    WplatyInfoAvg(zapytanieOdKlienta,connection,writer);
                    break;


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

    //insert wartosci skladki oplaconej

    private void OplacSkladke(String userData,Connection connection, PrintWriter writer) throws  SQLException{
        String zapytanieDoBazy = "INSERT INTO Wplaty (pesel,data_wplaty,wartosc_wplaty) VALUES (?,TO_DATE(?, 'YYYY-MM-DD'),?)";

        String[] userDataArray = userData.split(" ");

        // Extract data from user data
        String  wartosc_wplaty = userDataArray[0];
        String data_wplaty = userDataArray[1];
        String pesel = userDataArray[2];

        PreparedStatement preparedStatement = connection.prepareStatement(zapytanieDoBazy);
        preparedStatement.setString(1, pesel);
        preparedStatement.setString(2,data_wplaty);
        preparedStatement.setString(3,wartosc_wplaty);

        // Execute the insert query
        int rowsAffected = preparedStatement.executeUpdate();

        // Check if the registration was successful
        if (rowsAffected > 0) {
            writer.println("Oplacono");
        } else {
            writer.println("Wystapil blad przy placaniu skaldki");
        }

        // Close resources
        preparedStatement.close();



    }
    private void WyslijPodanie(String userData,Connection connection,PrintWriter writer) throws SQLException{
        String zapytanieDoBazy = "INSERT INTO Liczba_poda(liczba,nazwa) VALUES(?,?)";
        PreparedStatement preparedStatement = connection.prepareStatement(zapytanieDoBazy);
        String[] userDataArray = userData.split(" ");

        // Extract data from user data
        String  pesel = userDataArray[0];
        String nazwa = userDataArray[1];

        preparedStatement.setString(1,pesel);
        preparedStatement.setString(2,nazwa);
        int rowsAffected = preparedStatement.executeUpdate();

        // Check if the registration was successful
        if (rowsAffected > 0) {
            writer.println("DodanoPodanie");
        } else {
            writer.println("Failed.");
        }

        // Close resources
        preparedStatement.close();
    }
    //wyswietlanie informacji o profilu
    private void ProfileInfoQuery(String pesel, Connection connection, PrintWriter writer) throws SQLException {

            String zapytanieDoBazy = "SELECT haslo, email, imie, nazwisko, wiek, plec FROM Podatnik WHERE pesel = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(zapytanieDoBazy);
            preparedStatement.setString(1, pesel);

            // Wykonanie bezpiecznego zapytania do bazy danych
            ResultSet resultSet = preparedStatement.executeQuery();

            // Przetwarzanie wyników
            StringBuilder resultString = new StringBuilder();

            while (resultSet.next()) {
                // Pobieranie danych z kolumn
                String email = resultSet.getString("email");
                String imie = resultSet.getString("imie");
                String nazwisko = resultSet.getString("nazwisko");
                String haslo = resultSet.getString("haslo");
                String wiek = resultSet.getString("wiek");
                String plec = resultSet.getString("plec");

                // Dodawanie danych do ciągu tekstowego
                resultString.append(email).append(" ");
                resultString.append(imie).append(" ");
                resultString.append(nazwisko).append(" ");
                resultString.append(haslo).append(" ");
                resultString.append(wiek).append(" ");
                resultString.append(plec);
            }
            String odp = resultString.toString().trim();
            if(!odp.isEmpty())
            {
                writer.println(odp);
            }else {
                writer.println("Brak danych dla tego peselu");
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
    private void updatePassword(String userData, Connection connection, PrintWriter writer) throws SQLException {

        String zapytanieDoBazy = "UPDATE Podatnik SET haslo = ? WHERE email = ?";

        String[] userDataArray = userData.split(" ");

        // Extract password and email from user data
        String haslo = userDataArray[0];
        String email = userDataArray[1];

        PreparedStatement preparedStatement = connection.prepareStatement(zapytanieDoBazy);
        preparedStatement.setString(1, haslo);
        preparedStatement.setString(2,email);

        int rowsAffected = preparedStatement.executeUpdate();
        if (rowsAffected > 0) {
            writer.println("UPDATED");
        } else {
            writer.println("NO-UPDATED");
        }

    }
    private void selectDataForInformationPanel(String pesel, Connection connection, PrintWriter writer) throws SQLException {
        String zapytanieDoBazy = "SELECT  FROM Podatnik WHERE pesel = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(zapytanieDoBazy);
        preparedStatement.setString(1, pesel);

        // Wykonanie bezpiecznego zapytania do bazy danych
        ResultSet resultSet = preparedStatement.executeQuery();



        // Zamknięcie zasobów
        resultSet.close();
        preparedStatement.close();
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
    private void handleRegisterSuccess(String pesel, String haslo, String email, String imie, String nazwisko,String wiek,String plec, Connection connection, PrintWriter writer) throws SQLException {
        // Insert user information into the database
        String insertQuery = "INSERT INTO Podatnik (pesel, haslo, email, imie, nazwisko,wiek,plec) VALUES (?, ?, ?, ?, ?,?,?)";
        PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
        preparedStatement.setString(1, pesel);
        preparedStatement.setString(2, haslo);
        preparedStatement.setString(3, email);
        preparedStatement.setString(4, imie);
        preparedStatement.setString(5, nazwisko);
        preparedStatement.setString(6,wiek);
        preparedStatement.setString(7,plec);


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

    private void ForgotPassword(String userData, Connection connection, PrintWriter writer) throws SQLException {
        // Split user data received from the client
        String[] userDataArray = userData.split(" ");


        // Extract pesel and password from user data
        String email = userDataArray[0];


        // Query to check if user with given pesel and password exists in the database
        String forgotQuery = "SELECT * FROM Podatnik WHERE email = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(forgotQuery);
        preparedStatement.setString(1, email);

        // Execute the forgotPassword query
        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            // User with pesel exists, now compare the passwords
            String storedEmail = resultSet.getString("email");

            if (email.equals(storedEmail)) {
                // email match, send verification code for change password
                writer.println("existing");
            } else {
                // Passwords don't match, login failed
                writer.println("non-existing");
            }
        }
        else {
            writer.println("non-existing");

        }

        // Close resources
        resultSet.close();
        preparedStatement.close();
    }

    private void WplatyInfo(String pesel, Connection connection, PrintWriter writer) throws SQLException {


        String zapytanieDoBazy = "SELECT SUM(wartosc_wplaty) FROM Wplaty WHERE pesel = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(zapytanieDoBazy);
        preparedStatement.setString(1, pesel);

        // Wykonanie bezpiecznego zapytania do bazy danych
        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            // Access the result using position or uppercase name
            String suma = resultSet.getString(1);
            // or
            // String suma = resultSet.getString("SUM(WARTOSC_WPLATY)");

            writer.println(suma);
        } else {
            // Handle the case when no rows are returned
            writer.println("No data found for the specified condition");
        }




        // Zamknięcie zasobów
        resultSet.close();
        preparedStatement.close();



    }
    private void WplatyInfoAvg(String pesel, Connection connection, PrintWriter writer) throws SQLException {


        String zapytanieDoBazy = "SELECT AVG(wartosc_wplaty) FROM Wplaty WHERE pesel = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(zapytanieDoBazy);
        preparedStatement.setString(1, pesel);

        // Wykonanie bezpiecznego zapytania do bazy danych
        ResultSet resultSet = preparedStatement.executeQuery();


        if (resultSet.next()) {
            // Access the result using position or uppercase name
            String avg = resultSet.getString(1);
            // or
            // String suma = resultSet.getString("SUM(WARTOSC_WPLATY)");

            writer.println(avg);
        } else {
            // Handle the case when no rows are returned
            writer.println("No data found for the specified condition");
        }





        // Zamknięcie zasobów
        resultSet.close();
        preparedStatement.close();



    }

}

