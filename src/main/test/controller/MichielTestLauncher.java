package controller;

import database.mysql.DBAccess;
import database.mysql.QuizDAO;
import javacouchdb.CouchDBAccess;
import model.Quiz;
import util.MichielFileHandler;
import view.Main;

import java.io.File;
import java.util.List;


public class MichielTestLauncher {
    public static void main(String[] args) {
        MichielFileHandler fileHandler = new MichielFileHandler();
        DBAccess dbAccess = Main.getDBaccess();
        QuizDAO quizDAO = new QuizDAO(dbAccess);
        CouchDBAccess couchDBaccess = Main.getCouchDBAccess();


        // Uses a method from MichielFileHandler to read
        // data from Quizzes.csv convert it to a list of Quiz objects
        File quizzesfile = new File("src/main/resources/CSV bestanden/Quizzen.csv");
        List<Quiz> quizzesList = fileHandler.createListOfQuizzesFromFile(quizzesfile);

        for (Quiz quiz : quizzesList) {
            System.out.println(quiz);
            System.out.println();
        }

        // Write the list of quizzes from the CSV file to the database
        // quizDAO.writeQuizzesListToDatabase(quizzesList);

        // testing count number of questions method of the quiz class
        Quiz quiz1 = quizDAO.getOneById(1);
        System.out.println(quiz1);
        System.out.println("id = " + quiz1.getQuizId());
        int numberOfQuestions = quiz1.countNumberOfQuestionsAlternative();
        System.out.println("number of questions of quiz 1 = " + numberOfQuestions);

    } // main


}
