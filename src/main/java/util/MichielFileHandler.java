package util;

import controller.WelcomeController;
import database.mysql.CourseDAO;
import database.mysql.DBAccess;
import database.mysql.QuizDAO;
import model.Course;
import model.Level;
import model.Quiz;
import model.User;
import view.Main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class MichielFileHandler {
    CourseDAO courseDAO ;
    DBAccess dbAccess = Main.getDBaccess();
    QuizDAO quizDAO;

    public MichielFileHandler() {
        this.courseDAO = new CourseDAO(dbAccess);
        this.quizDAO = new QuizDAO(dbAccess);
    }

    /**
     * Creates a list of Quiz objects from a (properly formatted) CSV file
     *
     * @param quizzesFile a properly formatted CSV file with data corresponding to the Quiz object
     * @return a list of quizzes
     */
    public List<Quiz> createListOfQuizzesFromFile(File quizzesFile) {

        try {
            // creates a new ArrayList for storing Quiz objects
            List<Quiz> quizzesList = new ArrayList<>();
            String line;
            Scanner scanner = new Scanner(quizzesFile);
            while (scanner.hasNextLine()) {
                // read the data from quizzesfile
                line = scanner.nextLine();
                // split the data on comma's
                String[] regelSplit = line.split(",");
                String quizName = regelSplit[0];
                Level quizLevel = Level.valueOf(regelSplit[1]);
                String successDefinition = regelSplit[2];
                String courseName = regelSplit[3];
                // create a Course object from the courseName
                Course course = courseDAO.getCoursePerName(courseName);
                // create a new Quiz object from this data and add to the quizzeslist
                quizzesList.add(new Quiz(quizName, quizLevel, course, Integer.parseInt(successDefinition)));
            }
            return quizzesList;
        } catch (FileNotFoundException bestandFout) {
            System.out.println("Bestand niet gevonden.");
        }
        return null;
    } // createListOfQuizzesFromFile


    /**
     * Create a textfile containing an overview of all quizzes in the courses coordinated by the
     * currently logged in user. The textfile shows the amount of questions per quiz, as well as
     * the average amount of questions per quiz.
     */
    public void exportQuizzesFile() {
        User coordinator = WelcomeController.getUser();
        File quizzesFile = new File("src/main/resources/export/" + coordinator.getUsername() + "-quizzen.txt");

        // First get all the courses coordinated by the current user
        List<Course> courseListFromDB = courseDAO.getCoursesByUsername(coordinator);

        // initialise a list to hold all the quizzes belonging to these courses
        List<Quiz> quizzesListByCoordinator = new ArrayList<>();

        // loop through the list of courses, and then add all the quizzes for each course
        // to the quizzesListByCoordinator
        for (Course course : courseListFromDB) {
            List<Quiz> quizzesFromCourse = quizDAO.getQuizzesByCourseName(course.getCourseName());
            quizzesListByCoordinator.addAll(quizzesFromCourse);
        }
        try {
            PrintWriter printWriter = new PrintWriter(quizzesFile);
            int totalNumberOfQuestions = 0;
            for (Quiz quiz : quizzesListByCoordinator) {
                printWriter.println(quiz);
                printWriter.println("Aantal vragen: " + quiz.countNumberOfQuestionsAlternative());
                printWriter.println();
                totalNumberOfQuestions += quiz.countNumberOfQuestionsAlternative();
            }
            double averageNumberOfQuestions = (double) totalNumberOfQuestions / quizzesListByCoordinator.size();
            printWriter.printf("Het gemiddeld aantal vragen per quiz is %.1f", averageNumberOfQuestions);
            printWriter.close();
        } catch (FileNotFoundException bestandNietGemaakt) {
            System.out.println("Het bestand kan niet worden aangemaakt.");
        }
    } // exportQuizzesFile

}
