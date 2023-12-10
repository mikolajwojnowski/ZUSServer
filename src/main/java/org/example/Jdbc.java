package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class Jdbc {
    String zapytanie (String pesel) throws Exception {

        String wynik = new String();
        String pytanie = new String(pesel);
        try {

//step1 load the driver class
            Class.forName("oracle.jdbc.driver.OracleDriver");

//step2 create  the connection object
            Connection con = DriverManager.getConnection(
                    "system@//localhost:1521/orcl", "system", "student");

//step3 create the statement object
            Statement stmt = con.createStatement();

//step4 execute query
            ResultSet rs = stmt.executeQuery(pytanie);
            while (rs.next())
                wynik = rs.getString(2);
            System.out.println(wynik);
//step5 close the connection object
            con.close();

        } catch (Exception e) {
            System.out.println(e);
        }

        return wynik;
    }

    void podatnikInsert ( String values ) throws Exception
    {


        try
        {

//step1 load the driver class
            Class.forName("oracle.jdbc.driver.OracleDriver");

//step2 create  the connection object
            Connection con = DriverManager.getConnection(
                    "system@//localhost:1521/orcl", "system", "student");


            Statement statement = con.createStatement();

            String insert = new String();
            insert = values;
            statement.executeUpdate(insert);
//step3 create the statement object

//step5 close the connection object
            con.close();

        }
        catch (Exception e)
        {
            System.out.println(e);
        }


    }
    String zapytaniePesel (String pesel) throws Exception {

        String wynik = new String();
        String pytanie = new String(pesel);
        try {

//step1 load the driver class
            Class.forName("oracle.jdbc.driver.OracleDriver");

//step2 create  the connection object
            Connection con = DriverManager.getConnection(
                    "system@//localhost:1521/orcl", "system", "student");

//step3 create the statement object
            Statement stmt = con.createStatement();

//step4 execute query
            ResultSet rs = stmt.executeQuery(pytanie);
            while (rs.next())
                wynik = rs.getString(1);
            System.out.println(wynik);
//step5 close the connection object
            con.close();

        } catch (Exception e) {
            System.out.println(e);
        }

        return wynik;
    }
    String zapytaniePeselimie (String pesel) throws Exception {

        String wynik = new String();
        String pytanie = new String(pesel);
        try {

//step1 load the driver class
            Class.forName("oracle.jdbc.driver.OracleDriver");

//step2 create  the connection object
            Connection con = DriverManager.getConnection(
                    "system@//localhost:1521/orcl", "system", "student");

//step3 create the statement object
            Statement stmt = con.createStatement();

//step4 execute query
            ResultSet rs = stmt.executeQuery(pytanie);
            while (rs.next())
                wynik = rs.getString(4);
            System.out.println(wynik);
//step5 close the connection object
            con.close();

        } catch (Exception e) {
            System.out.println(e);
        }

        return wynik;
    }
    String zapytanieNazwisko(String pesel) throws Exception {

        String wynik = new String();
        String pytanie = new String(pesel);
        try {

//step1 load the driver class
            Class.forName("oracle.jdbc.driver.OracleDriver");

//step2 create  the connection object
            Connection con = DriverManager.getConnection(
                    "system@//localhost:1521/orcl", "system", "student");

//step3 create the statement object
            Statement stmt = con.createStatement();

//step4 execute query
            ResultSet rs = stmt.executeQuery(pytanie);
            while (rs.next())
                wynik = rs.getString(5);
            System.out.println(wynik);
//step5 close the connection object
            con.close();

        } catch (Exception e) {
            System.out.println(e);
        }

        return wynik;
    }
}
