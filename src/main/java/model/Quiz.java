/**
 * A class representing a quiz in the database
 * @author Michiel van Haren
 */


package model;

import database.mysql.QuizDAO;
import view.Main;

import java.util.List;

public class Quiz {
    // Attributes --------------------------------------------------------------
    private int quizId;
    private String quizName;
    private Level quizLevel;
    private Course course;
    private int successDefinition;


    // Getters & setters -------------------------------------------------------
    public int getQuizId() {
        return quizId;
    }

    public String getQuizName() {
        return quizName;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }


    public int getSuccessDefinition() {
        return successDefinition;
    }

    public Level getQuizLevel() {
        return quizLevel;
    }


    // Constructors -----------------------------------------------------------
    public Quiz(int quizId, String quizName, Level quizLevel, Course course, int successDefinition) {
        this.quizId = quizId;
        this.quizName = quizName;
        this.quizLevel = quizLevel;
        this.course = course;
        this.successDefinition = successDefinition;
    }


    public Quiz(String quizName, Level quizLevel, Course course, int successDefinition) {
        this.quizName = quizName;
        this.quizLevel = quizLevel;
        this.course = course;
        this.successDefinition = successDefinition;
    }


    public Quiz(String quizName, Level quizLevel, Course course) {
        this.quizName = quizName;
        this.quizLevel = quizLevel;
        this.course = course;
    }


    // Methods ---------------------------------------------------------------

    /**
     * Returns the number of questions in a quiz as an int
     * (database returns the whole list and we do the selection in Java)
     * @return the number of questions in the quiz
     */
    public int countNumberOfQuestionsAlternative() {
        QuizDAO quizDAO = new QuizDAO(Main.getDBaccess());
        String mpQuizName = this.getQuizName();
        int number_of_questions = 0;
        List<Quiz> quizzesList = quizDAO.getAll();
        for (Quiz quiz : quizzesList) {
            if (quiz.getQuizName().equals(mpQuizName)) {
                number_of_questions++;
            }
        } // for loop
        return number_of_questions;
    } // countNumberOfQuestionsAlternative

    /**
     * Checks if two Quiz objects are equal based on their quizId.
     * Returns true if:
     * - The objects are the same instance, or
     * - The other object is also a Quiz and has the same quizId.
     */
    @Override
    public boolean equals(Object obj) {
        // ( comparing the same Quiz instance with itself)
        if (this == obj) return true;
        // If the passed object is null or not of the same class (i.e., not a Quiz), return false
        if (obj == null || getClass() != obj.getClass()) return false;
        //  we can access its fields
        Quiz other = (Quiz) obj;
        // Compare the quizId values
        return this.quizId == other.quizId;
    }


    @Override
    public String toString() {
        return "Naam: " + quizName +
                "\nNiveau: " + quizLevel +
                "\nCursus: " + course.getCourseName() +
                "\nCesuur: " + successDefinition;
    }


}

