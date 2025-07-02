package controller;

import database.mysql.DBAccess;
import database.mysql.QuestionDAO;
import database.mysql.QuizDAO;
import model.Question;
import model.Quiz;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BurhanQuizmasterLauncher {
    public static void main(String[] args) {
        DBAccess dBaccess = new DBAccess("*****", "*****", "*****");
        dBaccess.openConnection();

        // Initialize Quiz Data Access Object for database operations related to quizzes
        QuizDAO quizDAO = new QuizDAO(dBaccess);

        // functionaliteit voor uitlezen Vragen.csv
        File questionFile = new File ("src/main/resources/CSV bestanden/Vragen.csv");
        List<Question> questionList = new ArrayList<>();
        try {
            // Read the CSV file line by line
            String line;
            Scanner scanner = new Scanner(questionFile);
            while (scanner.hasNextLine()){
                line = scanner.nextLine();
                // Split the CSV line by semicolon
                String[] regelSplit = line.split(";");
                String questionText = regelSplit[0];
                String correctAnswer = regelSplit[1];
                String incorrectAnswer1 = regelSplit[2];
                String incorrectAnswer2 = regelSplit[3];
                String incorrectAnswer3 = regelSplit[4];
                String quizName = regelSplit[5];

                // Retrieve the Quiz object from the database by its name
                Quiz quiz = quizDAO.getQuizPerName(quizName);

                // Create a new Question object and add it to the question list
                questionList.add(new Question(
                        questionText,
                        correctAnswer,
                        incorrectAnswer1,
                        incorrectAnswer2,
                        incorrectAnswer3,
                        quiz                // Quiz object
                ));
            }
        } catch (FileNotFoundException bestandFout) {
            System.out.println("Bestand niet gevonden.");
        }

        QuestionDAO questiondao = new QuestionDAO(dBaccess);

        // Store each question read from the file into the database
        for (Question question : questionList){
            questiondao.storeOne(question);
        }
        System.out.println("Alle vragen zijn opgeslagen. ");

        dBaccess.closeConnection();


    }
}
