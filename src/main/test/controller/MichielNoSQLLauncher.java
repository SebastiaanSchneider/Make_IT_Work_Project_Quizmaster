/**
 * Launcher klasse voor testen en demonstratie van NoSQL/CouchDB functionaliteit
 * @author Michiel van Haren
 */

package controller;

import com.google.gson.Gson;
import database.mysql.DBAccess;
import database.mysql.QuizDAO;
import database.mysql.UserDAO;
import javacouchdb.CouchDBAccess;
import javacouchdb.QuizCouchDBDAO;
import javacouchdb.QuizResultCouchDBDAO;
import model.QuizResult;
import view.Main;

import java.time.LocalDateTime;
import java.util.List;


public class  MichielNoSQLLauncher {

    public static void main(String[] args) {
        // Initialise database access and DAO objects
        DBAccess dbAccess = Main.getDBaccess();
        CouchDBAccess couchDBAccess = Main.getCouchDBAccess();
        QuizDAO quizDAO = new QuizDAO(dbAccess);
        UserDAO userDAO = new UserDAO(dbAccess);
        QuizCouchDBDAO quizCouchDBDAO = new QuizCouchDBDAO(couchDBAccess);
        QuizResultCouchDBDAO quizResultCouchDBDAO = new QuizResultCouchDBDAO(couchDBAccess);


        // Demonstratie werken met Json objecten en CRUD functionaliteit CouchDB
//        Gson gson = new Gson();
//
//        // 1. Maak een nieuw Quiz object aan door een quiz uit de SQL database op te vragen
//        Quiz quiz = quizDAO.getOneById(1);
//
//        // 2. Zet de Quiz om naar een Json string mbv de Gson library
//        String quizJson = gson.toJson(quiz);
//        System.out.println("Quiz als json");
//        System.out.println(quizJson);
//        System.out.println();
//
//        // 3. Maak een Quiz object van een json string
//        Quiz quizFromJson = gson.fromJson(quizJson, Quiz.class);
//        System.out.println("Json omgezet naar Quiz");
//        System.out.println(quizFromJson);
//        System.out.println();
//
//        // 4. Schrijf een quiz object weg naar de (lokale) CouchDB database (Create)
//        quizCouchDBDAO.saveSingleQuiz(quiz);
//
//        // 5. lees een quiz uit de CouchDB database en maak hier een Quiz object van (Read)
//        Quiz JavaBasisQuiz = quizCouchDBDAO.getQuizByQuizId(1);
//        System.out.println(JavaBasisQuiz);
//
//        // 6a. Pas quizNaam aan
//        JavaBasisQuiz.setQuizName("Vette Quiz");
//
//        // 6b. update een quiz in de CouchDB database (Update) met nieuwe quiznaam
//        quizCouchDBDAO.updateQuiz(JavaBasisQuiz);
//
//        // 7. verwijder een quiz uit de CouchDB database (Delete)
//        quizCouchDBDAO.deleteQuiz(JavaBasisQuiz);
//


        // ****************************************************************************************************


        // En nu met QuizResults
//        Gson gson = new Gson();

        // attributes for creating a quizResult object
//        int userId = 201;
//        int quizId = 1;
//        int score = 33;
//        LocalDateTime dateTime = LocalDateTime.now();

        // 1. create quizResult object
//        QuizResult quizResult = new QuizResult(quizId, userId, dateTime, score);

//        // 2. zet een QuizResult om naar een Json string mbv de Gson library
//        String quizResultJson = gson.toJson(quizResult);
//        System.out.println("QuizResult als json");
//        System.out.println(quizResultJson);
//        System.out.println();
//
//        // 3. Maak een QuizResult object van een json string
//        QuizResult quizResultFromJson = gson.fromJson(quizResultJson, QuizResult.class);
//        System.out.println("Json omgezet naar QuizResult");
//        System.out.println(quizResultJson);
//        System.out.println();
//
        // 4. Schrijf een quizResult object weg naar de (lokale) CouchDB database (Create)
//        quizResultCouchDBDAO.saveSingleQuizResult(quizResult);



        // 5. lees een lijst quizResults uit de CouchDB database op basis van userId (Read)
//        List<QuizResult> myQuizResults = quizResultCouchDBDAO.getQuizResultsByUserId(1);
//        for (QuizResult qr : myQuizResults) {
//            System.out.println(qr);
//            System.out.println();
//        }



        // Hier genereren we even 80 verschillende quizresults om makkelijker te kunnen testen
        Gson gson = new Gson();
        int[] userIds = {201, 203, 204, 205, 206, 207, 209, 211, 212, 213, 214, 215, 217, 218, 221, 222, 223, 224, 225, 227, 201, 203, 204, 205, 206, 207, 209, 211, 212, 213, 214, 215, 217, 218, 221, 222, 223, 224, 225, 227, 201, 203, 204, 205, 206, 207, 209, 211, 212, 213, 214, 215, 217, 218, 221, 222, 223, 224, 225, 227, 201, 203, 204, 205, 206, 207, 209, 211, 212, 213, 214, 215, 217, 218, 221, 222, 223, 224, 225, 227};
        int[] quizIds = {1, 2, 3, 4, 5, 6, 7, 8, 33};
        int[] successDefinitions = {7, 5, 6, 6, 6, 8, 8, 8, 6};
        for (int i = 0; i < userIds.length; i++) {
            int userId = userIds[i];
            int quizIndex = i % quizIds.length;
            int quizId = quizIds[quizIndex];
            int successThreshold = successDefinitions[quizIndex];
            // score tussen 0 en 10, kans op slagen of falen
            int score = (int) (Math.random() * 11);
            // Simuleer een realistisch tijdstip (nu - willekeurig aantal dagen)
            LocalDateTime dateTime = LocalDateTime.now().minusDays((long) (Math.random() * 60));
            QuizResult quizResult = new QuizResult(quizId, userId, dateTime, score);
            quizResultCouchDBDAO.saveSingleQuizResult(quizResult);
        }



    } // main


}