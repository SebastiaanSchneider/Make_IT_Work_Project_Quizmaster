package controller;

import com.google.gson.Gson;

import database.mysql.DBAccess;
import database.mysql.QuizDAO;

import javacouchdb.CouchDBAccess;
import javacouchdb.GroupCouchDBDAO;
import javacouchdb.QuizCouchDBDAO;

import model.Quiz;

import view.Main;


/**
 * Writes example quizResult to local CouchDB
 */
public class SebastiaanNOSQLLauncher {
    public static void main(String[] args) {
        // initialise database access and DAO objects
        DBAccess dbAccess = Main.getDBaccess();
        CouchDBAccess couchDBaccess = new CouchDBAccess("*****", "*****", "*****");
        QuizDAO quizDAO = new QuizDAO(dbAccess);
        QuizCouchDBDAO quizCouchDBDAO = new QuizCouchDBDAO(couchDBaccess);


        Gson gson = new Gson();
        Quiz quiz = quizDAO.getOneById(1);

        // turn Quiz object into Gson object
        String quizJson = gson.toJson(quiz);
        System.out.println("Quiz als json");
        System.out.println(quizJson);
        System.out.println();

        // turn Gson object into Quiz object
        Quiz quizFromJson = gson.fromJson(quizJson, Quiz.class);
        System.out.println("Json omgezet naar Quiz");
        System.out.println(quizFromJson);
        System.out.println();

        // write Quiz object to CouchDB
        quizCouchDBDAO.saveSingleQuiz(quiz);

        // read CouchDB database into console
        Quiz JavaBasisQuiz = quizCouchDBDAO.getQuizByQuizId(1);
        System.out.println(JavaBasisQuiz);
    }
}
