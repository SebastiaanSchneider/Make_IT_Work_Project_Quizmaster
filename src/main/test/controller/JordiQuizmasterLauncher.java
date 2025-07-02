package controller;

import database.mysql.DBAccess;
import database.mysql.UserDAO;
import model.Role;
import model.User;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class JordiQuizmasterLauncher {

    /**
     * Hoofdmethode die gebruikers uit een CSV-bestand inleest en opslaat in de database.
     */
    public static void main(String[] args) {
        // Maak verbinding met de database
        DBAccess dbAccess = new DBAccess("*****", "*****", "*****");
        dbAccess.openConnection();

        // Verwijs naar het CSV-bestand met gebruikers
        File gebruikerbestand = new File("src/main/resources/CSV bestanden/Gebruikers.csv");
        List<User> userList = new ArrayList<>();

        try {
            String line;
            Scanner scanner = new Scanner(gebruikerbestand); // Open het CSV-bestand

            // Lees het bestand regel voor regel
            while (scanner.hasNextLine()) {
                line = scanner.nextLine(); // Lees een regel
                String[] regelSplit = line.split(","); // Split de regel op komma's

                // Haal gegevens op uit gesplitste regel
                String username = regelSplit[0];
                String password = regelSplit[1];
                String firstname = regelSplit[2];
                String infix = regelSplit[3];
                String lastname = regelSplit[4];
                Role role = Role.valueOf(regelSplit[5]);

                // Maak User-object aan en voeg toe aan lijst
                userList.add(new User(username, password, firstname, infix, lastname, role));
            }

        } catch (FileNotFoundException bestandFout) {
            // Foutmelding als bestand niet wordt gevonden
            System.out.println("Bestand niet gevonden.");
        }

        // Sla alle gebruikers op in de database
        UserDAO userDAO = new UserDAO(dbAccess);
        for (User user : userList) {
            userDAO.storeOne(user);
        }

        // Sluit de databaseverbinding
        dbAccess.closeConnection();
    }
}
