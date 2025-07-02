package controller;

import com.google.gson.Gson;
import database.mysql.UserDAO;
import database.mysql.DBAccess;
import javacouchdb.CouchDBAccess;
import javacouchdb.UserCouchDBDAO;
import model.User;
import view.Main;

public class JordiNoSQLLauncher {
    public static void main(String[] args) {

        // toegang databases en DAO's
        DBAccess dbAccess = Main.getDBaccess();
        UserDAO userDAO = new UserDAO(dbAccess);
        CouchDBAccess couchDBaccess = new CouchDBAccess("*****", "*****", "*****");
        UserCouchDBDAO userCouchDBDAO = new UserCouchDBDAO(couchDBaccess);
        Gson gson = new Gson();

        // user uit mySQL ophalen
        User user = userDAO.getOneById(201);

        // Create
        userCouchDBDAO.saveSingleUser(user);

        // Read
        User user1 = userCouchDBDAO.getUserByUserId(201);
        System.out.println("\nGebruiker uit CouchDB: \n" + user1);

        // Update
        user1.setUsername("Gewijzigde naam");
        userCouchDBDAO.updateUser(user1);
        System.out.println("\nGewijzigde gebruiker: \n"  + user1);
        userCouchDBDAO.getUserByUserId(64);
        System.out.println("\nGebruiker in DB: \n" + user1);

        // Delete
        userCouchDBDAO.deleteUser(user1);

    }
}
