package controller;

import database.mysql.*;
import model.*;
import view.Main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ElineSQLLauncher {
    private final static DBAccess dbAccess = Main.getDBaccess();
    private final static UserDAO userDAO = new UserDAO(dbAccess);
    private final static CourseDAO courseDAO = new CourseDAO(dbAccess);
    private final static QuizDAO quizDAO = new QuizDAO(dbAccess);
    private final static QuestionDAO questionDAO = new QuestionDAO(dbAccess);
    private final static GroupDAO groupDAO = new GroupDAO(dbAccess);
    private final static RoleDAO roleDAO = new RoleDAO(dbAccess);

    public static void main(String[] args) {
        saveRoles();
        saveUsers(new File(
                "src/main/resources/CSV bestanden/Gebruikers.csv"));
        saveCourses(new File(
                "src/main/resources/CSV bestanden/Cursussen.csv"));
        saveQuizzes(new File(
                "src/main/resources/CSV bestanden/Quizzen.csv"));
        saveQuestions(new File(
                "src/main/resources/CSV bestanden/Vragen.csv"));
        saveGroups(new File(
                "src/main/resources/CSV bestanden/Groepen.csv"));
    }

    /**
     *  Saves roles into database.
     */
    public static void saveRoles() {
        for (Role role : Role.values()) {
            roleDAO.storeOne(role.toString());
        }
    }

    /**
     * Reads file and saves users in database.
     *
     * @param file to read.
     */
    private static void saveUsers(File file) {
        List<User> userList = new ArrayList<>();
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String[] rs = scanner.nextLine().split(",");
                userList.add(new User(rs[0], rs[1], rs[2], rs[3],
                        rs[4], Role.valueOf(rs[5])));
            }
        } catch (FileNotFoundException ex) {
            getError();
        }
        for (User user : userList) {
            userDAO.storeOne(user);
        }
    }

    /**
     * Reads file and saves courses in database.
     *
     * @param file to read.
     */
    private static void saveCourses(File file) {
        List<Course> courseList = new ArrayList<>();
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String[] rs = scanner.nextLine().split(",");
                courseList.add(new Course(Level.valueOf(rs[1]), rs[0],
                        userDAO.getUserPerUsername(rs[2])));
            }
        } catch (FileNotFoundException ex) {
            getError();
        }
        for (Course course : courseList) {
            courseDAO.storeOne(course);
        }
    }

    /**
     * Reads file and saves quizzes to database.
     *
     * @param file to read.
     */
    public static void saveQuizzes(File file) {
        List<Quiz> quizzesList = new ArrayList<>();
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String[] rs = scanner.nextLine().split(",");
                quizzesList.add(new Quiz(rs[0], Level.valueOf(rs[1]),
                        courseDAO.getCoursePerName(rs[3]), Integer.parseInt(rs[2])));
            }
        } catch (FileNotFoundException ex) {
            getError();
        }
        for (Quiz quiz : quizzesList) {
            quizDAO.storeOne(quiz);
        }
    }

    /**
     * Reads file and saves questions to database.
     *
     * @param file to read.
     */
    public static void saveQuestions(File file) {
        List<Question> questionList = new ArrayList<>();
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String[] rs = scanner.nextLine().split(";");
                questionList.add(new Question(rs[0], rs[1],
                rs[2], rs[3], rs[4], quizDAO.getQuizPerName(rs[5])));
            }
        } catch (FileNotFoundException ex) {
            getError();
        }
        for (Question question : questionList) {
            questionDAO.storeOne(question);
        }
    }

    /**
     * Reads file and saves groups to database.
     *
     * @param file to read.
     */
    public static void saveGroups(File file) {
        List<Group> groupList = new ArrayList<>();
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String[] regelSplit = scanner.nextLine().split(",");
                groupList.add(new Group (regelSplit[0],
                        Integer.parseInt(regelSplit[1]),
                        userDAO.getUserPerUsername(regelSplit[2]),
                        courseDAO.getCoursePerName(regelSplit[3])));
            }
        } catch (FileNotFoundException ex) {
            getError();
        }
        for (Group group : groupList) {
            groupDAO.storeOne(group);
        }
    }

    /**
     * Shows error when file is not found.
     */
    public static void getError(){
        System.out.println("Bestand niet gevonden");
    }

}

