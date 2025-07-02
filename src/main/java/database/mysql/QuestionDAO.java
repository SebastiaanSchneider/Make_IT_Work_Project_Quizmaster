package database.mysql;

import model.Question;
import model.Quiz;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class QuestionDAO extends AbstractDAO implements GenericDAO <Question> {

    /**
     * Constructor initializing the DAO with the database access object.
     * @param dbAccess The database access handler
     */
    public QuestionDAO(DBAccess dbAccess) {
        super(dbAccess);
    }

    /**
     * Stores a Question object into the database.
     * If the question text already exists, it will not insert a duplicate.
     * Uses SQL INSERT with ON DUPLICATE KEY UPDATE to update existing entries.
     *
     * @param question The Question to store
     */
    @Override
    public void storeOne(Question question) {
        if (questionExists(question.getQuestionText())) {
            System.out.println("De vraag " + question.getQuestionText() + " bestaat al. ");
            return;
        }
        String sql = "INSERT INTO Question (idQuestion, question_text, correct_answer, incorrect_answer1, " +
                "incorrect_answer2, incorrect_answer3, quiz_name) VALUES(?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE question_text = VALUES(question_text), correct_answer = VALUES(correct_answer), " +
                "incorrect_answer1 = VALUES(incorrect_answer1), incorrect_answer2=VALUES(incorrect_answer2), " +
                "incorrect_answer3=VALUES(incorrect_answer3),  quiz_name=VALUES( quiz_name)";
        try {
            setupPreparedStatementWithKey(sql);
            preparedStatement.setInt(1, question.getQuestionId());
            preparedStatement.setString(2, question.getQuestionText());
            preparedStatement.setString(3, question.getCorrectAnswer());
            preparedStatement.setString(4, question.getIncorrectAnswer1());
            preparedStatement.setString(5, question.getIncorrectAnswer2());
            preparedStatement.setString(6, question.getIncorrectAnswer3());
            preparedStatement.setString(7, question.getQuiz().getQuizName());
            executeInsertStatementWithKey();
        } catch (SQLException sqlFout) {
            System.out.println(sqlFout);
        }
    }

    /**
     * Retrieves all questions stored in the database.
     * For each question, it also fetches the related Quiz object.
     *
     * @return List of all Question objects
     */
    @Override
    public List<Question> getAll() {
        List<Question> questionList = new ArrayList<>();
        String sql = "SELECT * FROM Question;";
        QuizDAO quizDAO = new QuizDAO(dbAccess); // open connection BEFORE while loop, only has to open one time
        try {
            setupPreparedStatement(sql);
            ResultSet resultSet = executeSelectStatement();
            while (resultSet.next()) {
                Quiz quiz = quizDAO.getQuizPerName(resultSet.getString("quiz_name"));
                Question question = new Question(
                        resultSet.getString("question_text"),
                        resultSet.getString("correct_answer"),
                        resultSet.getString("incorrect_answer1"),
                        resultSet.getString("incorrect_answer2"),
                        resultSet.getString("incorrect_answer3"),
                        quiz);
                question.setQuestionId(resultSet.getInt("idQuestion"));
                questionList.add(question);
            }
        } catch (SQLException foutmelding) {
            System.out.println(foutmelding.getMessage());
        }
        return questionList;
    } // end getAll


    /**
     * Checks if a question with the given text already exists in the database.
     * Used to prevent duplicate entries.
     *
     * @param questiontext The text of the question to check
     * @return true if question exists, false otherwise
     */
    public boolean questionExists(String questiontext) {
        String sql = "SELECT COUNT(*) FROM Question WHERE question_text = ?";
        try {
            setupPreparedStatement(sql);
            preparedStatement.setString(1, questiontext);
            ResultSet rs = executeSelectStatement();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException sqlfout) {
            System.out.println("SQL fout!");
        }
        return false;
    } // end exists


    /**
     * Deletes a Question from the database based on its ID.
     *
     * @param question The Question object to delete
     */
    public void deleteOne(Question question) {
        String sql = "DELETE FROM Question WHERE idQuestion = ?";

        try {
            setupPreparedStatement(sql);
            preparedStatement.setInt(1, question.getQuestionId());
            executeManipulateStatement(); // Execute delete
        } catch (SQLException e) {
            throw new RuntimeException("Error while deleting question: " + e.getMessage());
        }
    }



    /**
     * Retrieves a Question object from the database by its ID.
     * Fetches the related Quiz object as well.
     *
     * @param id The question ID
     * @return The Question object or null if not found
     */
    @Override
    public Question getOneById(int id) {
        String sql = "SELECT * FROM Question WHERE idQuestion = ?";
        QuizDAO quizDAO = new QuizDAO(dbAccess);
        Question question = null;

        try {
            setupPreparedStatement(sql);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = executeSelectStatement();

            if (resultSet.next()) {
                Quiz quiz = quizDAO.getQuizPerName(resultSet.getString("quiz_name"));

                question = new Question(
                        resultSet.getString("question_text"),
                        resultSet.getString("correct_answer"),
                        resultSet.getString("incorrect_answer1"),
                        resultSet.getString("incorrect_answer2"),
                        resultSet.getString("incorrect_answer3"),
                        quiz
                );
                question.setQuestionId(resultSet.getInt("idQuestion"));
            }
        } catch (SQLException e) {
            System.out.println("Fout bij ophalen van vraag met id " + id + ": " + e.getMessage());
        }

        return question;
    }



    /**
     * Retrieves all Question objects that belong to a specific quiz by quiz name.
     *
     * @param quizName The name of the quiz
     * @return List of Question objects for the quiz
     */
    public List<Question> getQuestionsByQuizName(String quizName) {
        List<Question> questionList = new ArrayList<>();
        String sql = "SELECT * FROM Question WHERE quiz_name = ?;";
        QuizDAO quizDAO = new QuizDAO(dbAccess);

        try {
            setupPreparedStatement(sql);
            preparedStatement.setString(1, quizName);
            ResultSet resultSet = executeSelectStatement();

            while (resultSet.next()) {
                Quiz quiz = quizDAO.getQuizPerName(resultSet.getString("quiz_name"));

                Question question = new Question(
                        resultSet.getString("question_text"),
                        resultSet.getString("correct_answer"),
                        resultSet.getString("incorrect_answer1"),
                        resultSet.getString("incorrect_answer2"),
                        resultSet.getString("incorrect_answer3"),
                        quiz);
                question.setQuestionId(resultSet.getInt("idQuestion"));
                questionList.add(question);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving questions: " + e.getMessage());
        }
        return questionList;
    }


    /**
     * Counts the number of questions in a quiz using SQL COUNT query.
     * Efficient as it lets the database handle the counting.
     *
     * @param quiz The Quiz object
     * @return Number of questions in the quiz
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
        } catch (SQLException sqlfout) {
            System.out.println("SQL fout!");
        }
        return number_of_questions;
    } // countNumberOfQuestions


    /**
     * Returns the number of questions in a quiz
     * (database returns the whole list and we do the selection in Java)
     */
    public int countNumberOfQuestionsAlternative(Quiz quiz) {
        int number_of_questions = 0;
        String sql = "SELECT * FROM Question";
        try {
            setupPreparedStatement(sql);
            ResultSet resultSet = executeSelectStatement();
            String quizName = quiz.getQuizName();
            while (resultSet.next()) { // resultaten ophalen en in lijst zetten
                if (resultSet.getString(7).equals(quizName)) {
                    number_of_questions++;
                }
            }
        } catch (SQLException foutmelding) {
            System.out.println(foutmelding.getMessage());
        }
        return number_of_questions;
    }  // countNumberOfQuestionsAlternative
}
