/**
 * Class for database functionality for quizzes
 *
 * @author Michiel van Haren
 */

package database.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Course;
import model.Level;
import model.Quiz;
import model.User;
import view.Main;


public class QuizDAO extends AbstractDAO implements GenericDAO<Quiz> {
    /**
     * Constructor
     */
    public QuizDAO(DBAccess dbAccess) {
        super(dbAccess);
    }


    /**
     * Stores a new quiz or updates an existing quiz in the database
     *
     * @param quiz a Quiz object to store in the database.
     */
    @Override
    public void storeOne(Quiz quiz) {
        String sql = "INSERT INTO Quiz (quizId, quiz_name, quiz_level, course_name, success_definition) VALUES (?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE quiz_name = VALUES(quiz_name), quiz_level = VALUES(quiz_level), course_name = VALUES(course_name), success_definition = VALUES(success_definition)";
        try {
            setupPreparedStatementWithKey(sql);
            preparedStatement.setInt(1, quiz.getQuizId());
            preparedStatement.setString(2, quiz.getQuizName());
            preparedStatement.setString(3, quiz.getQuizLevel().toString());
            preparedStatement.setString(4, quiz.getCourse().getCourseName());
            preparedStatement.setInt(5, quiz.getSuccessDefinition());

            executeInsertStatementWithKey();
        } catch (SQLException sqlFout) {
            System.out.println("SQL fout " + sqlFout.getMessage());
        }
    } // storeOne


    public void writeQuizzesListToDatabase(List<Quiz> quizzesList) {
        for (Quiz quiz : quizzesList) {
            storeOne(quiz);
        }
    } // writeQuizzesListToDatabase

    /**
     * Gets all quizzes from database and returns a list of quizzes
     *
     * @return a list of all Quizzes in the database
     */
    @Override
    public List<Quiz> getAll() {
        List<Quiz> quizzesList = new ArrayList<>();
        String sql = "SELECT * FROM Quiz";
        CourseDAO courseDAO = new CourseDAO(dbAccess);
        try {
            setupPreparedStatement(sql);
            ResultSet resultSet = executeSelectStatement();
            while (resultSet.next()) { // resultaten ophalen en in lijst zetten
                int quizId = resultSet.getInt("quizId");
                String quizName = resultSet.getString("quiz_name");
                Level quizLevel = Level.valueOf(resultSet.getString("quiz_level"));
                String courseName = resultSet.getString("course_name");
                int successDefinition = resultSet.getInt("success_definition");
                Course course = courseDAO.getCoursePerName(courseName);
                Quiz quiz = new Quiz(quizId, quizName, quizLevel, course, successDefinition);
                quizzesList.add(quiz);
            }
        } catch (SQLException sqlFout) {
            System.out.println("SQL fout " + sqlFout.getMessage());
        }
        return quizzesList;
    } // end getAll


    /**
     * Gets all quizzes from database, belonging to courses coordinated by given user
     * and returns a list of quizzes.
     *
     * @param coordinator the user (with role coordinator) whose quizzes we should return
     * @return a list of Quizzes being coordinated by the specified coordinator
     */
    public List<Quiz> getAllByCoordinator(User coordinator) {
        CourseDAO courseDAO = new CourseDAO(dbAccess);
        String coordinator_username = coordinator.getUsername();
        List<Quiz> quizzesList = new ArrayList<>();
        String sql = "SELECT * FROM Quiz";
        try {
            setupPreparedStatement(sql);
            ResultSet resultSet = executeSelectStatement();
            while (resultSet.next()) { // resultaten ophalen en in lijst zetten
                int quizId = resultSet.getInt("quizId");
                String quizName = resultSet.getString("quiz_name");
                Level quizLevel = Level.valueOf(resultSet.getString("quiz_level"));
                String courseName = resultSet.getString("course_name");
                int successDefinition = resultSet.getInt("success_definition");
                Course course = courseDAO.getCoursePerName(courseName);

                if (course.getCoordinator().getUsername().equals(coordinator_username)) {
                    Quiz quiz = new Quiz(quizId, quizName, quizLevel, course, successDefinition);
                    quizzesList.add(quiz);
                }
            }
        } catch (SQLException sqlFout) {
            System.out.println("SQL fout " + sqlFout.getMessage());
        }
        return quizzesList;
    } // getAllByCoordinator


    /**
     * Returns a list of quizzes from the database
     * belonging to the course with the given course name.
     *
     * @param courseName the name of the course we want to get quizzes from
     * @return a list of Quizzes belonging to the specified course
     */
    public List<Quiz> getQuizzesByCourseName(String courseName) {
        CourseDAO courseDAO = new CourseDAO(dbAccess);
        List<Quiz> quizzesList = new ArrayList<>();
        String sql = "SELECT * FROM Quiz WHERE course_name = ?";
        try {
            setupPreparedStatement(sql);
            preparedStatement.setString(1, courseName);
            ResultSet resultSet = executeSelectStatement();
            Quiz quiz;
            while (resultSet.next()) {
                int quizId = resultSet.getInt("quizId");
                String quizName = resultSet.getString("quiz_name");
                Level quizLevel = Level.valueOf(resultSet.getString("quiz_level"));
                int successDefinition = resultSet.getInt("success_definition");
                Course course = courseDAO.getCoursePerName(courseName);
                quiz = new Quiz(quizId, quizName, quizLevel, course, successDefinition);
                quizzesList.add(quiz);
            }
        } catch (SQLException sqlFout) {
            System.out.println("SQL fout " + sqlFout.getMessage());
        }
        return quizzesList;
    } // getQuizzesByCourseName


    /**
     * Gets a quiz from database by quizId
     *
     * @param quizId the id (int) of the quiz in the database
     * @return a Quiz object with the specified id
     */
    @Override
    public Quiz getOneById(int quizId) {
        String sql = "SELECT * FROM Quiz WHERE quizId = ?";
        CourseDAO courseDAO = new CourseDAO(dbAccess);
        Quiz quiz = null;
        try {
            setupPreparedStatement(sql);
            preparedStatement.setInt(1, quizId);
            ResultSet resultSet = executeSelectStatement();
            while (resultSet.next()) {
                String quizName = resultSet.getString("quiz_name");
                Level quizLevel = Level.valueOf(resultSet.getString("quiz_level"));
                String courseName = resultSet.getString("course_name");
                int successDefinition = resultSet.getInt("success_definition");
                Course course = courseDAO.getCoursePerName(courseName);
                quiz = new Quiz(quizId, quizName, quizLevel, course, successDefinition);
            }
        } catch (SQLException sqlFout) {
            System.out.println("SQL fout " + sqlFout.getMessage());
        }
        return quiz;
    } // end getOneById


    /**
     * Gets quiz from database by quiz_name and returns it as a Quiz object
     *
     * @param quizName The name of the quiz in the database
     * @return a Quiz object with the specified name
     */
    public Quiz getQuizPerName(String quizName) {
        String sql = "SELECT * FROM Quiz WHERE quiz_name = ?";
        CourseDAO courseDAO = new CourseDAO(dbAccess);
        Quiz quiz = null;
        try {
            setupPreparedStatement(sql);
            preparedStatement.setString(1, quizName);
            ResultSet resultSet = executeSelectStatement();
            while (resultSet.next()) {
                int quizId = resultSet.getInt("quizId");
                Level quizLevel = Level.valueOf(resultSet.getString("quiz_level"));
                String courseName = resultSet.getString("course_name");
                Course course = courseDAO.getCoursePerName(courseName);
                int successDefinition = resultSet.getInt("success_definition");
                quiz = new Quiz(quizId, quizName, quizLevel, course, successDefinition);
            }
        } catch (SQLException sqlFout) {
            System.out.println("SQL fout " + sqlFout.getMessage());
        }
        return quiz;
    } // end getQuizPerName


    /**
     * Deletes one quiz by Id
     *
     * @param quiz a quiz to be deleted from the database
     */
    public void deleteOne(Quiz quiz) {

        String sql = "DELETE FROM Quiz WHERE quizId = ?";
        try {
            setupPreparedStatement(sql);
            preparedStatement.setInt(1, quiz.getQuizId());
            executeManipulateStatement();
        } catch (SQLException sqlFout) {
            System.out.println("SQL fout " + sqlFout.getMessage());
        }
    } // deleteOne


    /**
     * Checks if quiz with the given quizname but a different id already exists in the database
     *
     * @param quizname the name of the quiz
     * @param quizId   the id of the quiz
     * @return whether a quiz with the specified name already exists in the database
     */
    public boolean quizNameExists(String quizname, int quizId) {
        String sql = "SELECT COUNT(*) FROM Quiz WHERE quiz_name = ? AND quizId != ?";
        try {
            setupPreparedStatement(sql);
            preparedStatement.setString(1, quizname);
            preparedStatement.setInt(2, quizId);
            ResultSet resultSet = executeSelectStatement();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException sqlFout) {
            System.out.println("SQL fout " + sqlFout.getMessage());
        }
        return false;
    } // end quizExists


    /**
     * Returns the number of questions in a quiz as an int
     * (database does the selection)
     *
     * @param quiz the quiz to count the number of questions from
     * @return the number of questions belonging to the specified quiz
     */
    public int countNumberOfQuestions(Quiz quiz) {
        int number_of_questions = 0;
        String sql = "SELECT COUNT(*) FROM Question WHERE quiz_name = ?";
        try {
            setupPreparedStatement(sql);
            preparedStatement.setString(1, quiz.getQuizName());
            ResultSet resultSet = executeSelectStatement();
            if (resultSet.next()) {
                number_of_questions = resultSet.getInt(1);
            }
        } catch (SQLException sqlFout) {
            System.out.println("SQL fout " + sqlFout.getMessage());
        }
        return number_of_questions;
    } // countNumberOfQuestions

}
