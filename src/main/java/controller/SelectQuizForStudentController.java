package controller;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import javacouchdb.CouchDBAccess;
import javacouchdb.QuizResultCouchDBDAO;

import database.mysql.DBAccess;
import database.mysql.QuestionDAO;
import database.mysql.QuizDAO;
import database.mysql.RegistrationDAO;

import model.Course;
import model.Quiz;
import model.QuizResult;
import model.User;

import view.Main;

/**
 * Presents user with possible quizzes to take and show the result of a previous attempt,
 * if available.
 * @author Sebastiaan Schneider
 */
public class SelectQuizForStudentController {
    // setup variables
    public static final String PASSED = "Voldoende";
    public static final String FAILED = "Onvoldoende";

    @FXML
    private Label nameRoleLabel;
    @FXML
    ListView<Quiz> quizList;
    public Label dateResultLabel;

    // connect with databases and DAOs
    DBAccess dbAccess = Main.getDBaccess();
    CouchDBAccess couchDbAccess = Main.getCouchDBAccess();
    QuizResultCouchDBDAO quizResultCouchDBDAO = new QuizResultCouchDBDAO(couchDbAccess);
    QuizDAO quizDAO = new QuizDAO(dbAccess);
    RegistrationDAO registrationDAO = new RegistrationDAO(dbAccess);
    QuestionDAO questionDAO = new QuestionDAO(dbAccess);

    // setup, present available quizzes and possible previous result
    public void setup() {
        List<Quiz> checkedQuizList = new ArrayList<>();
        User currentUser = WelcomeController.getUser();
        nameRoleLabel.setText(currentUser.getFullName() + "\nRol: " +
                currentUser.getRole().toString());

        // fill listView with relevant quizzes
        for (Quiz quiz: quizDAO.getAll()) {
            for (Course course: registrationDAO.getAllCoursesPerUser(currentUser)) {
                if (Objects.equals(quiz.getCourse().getCourseName(), course.getCourseName())) {
                    checkedQuizList.add(quiz);
                }
            }
        }
        ObservableList<Quiz> quizzes = FXCollections.observableArrayList(checkedQuizList);
        quizList.setItems(quizzes);

        // listen for select event, in which case show possible previous quiz result
        quizList.getSelectionModel().selectedItemProperty().addListener((
                observableValue, quiz, selectedQuiz) -> {
            if (selectedQuiz != null) {
                 List<QuizResult> resultList =quizResultCouchDBDAO.getQuizResultsByUserId(
                         currentUser.getUserId());
                 if (!resultList.isEmpty()) {
                     QuizResult lastQuizResult = resultList.get(resultList.size() - 1);
                     String outcome;
                     if (lastQuizResult.getScore() >= selectedQuiz.getSuccessDefinition()) {
                         outcome = PASSED;
                     } else {
                         outcome = FAILED;
                     }
                     dateResultLabel.setText("Score: " + lastQuizResult.getScore()
                             + "/" + questionDAO.countNumberOfQuestions(selectedQuiz) +
                             "\nResultaat: " + outcome + "\nDatum: " +
                             lastQuizResult.getDateTime().format(
                                     DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
                 } else {
                    dateResultLabel.setText("NA");
                 }
            } else {
                dateResultLabel.setText("NA");
            }
        });
    }

    // navigate to menu view
    public void doMenu() {
        Main.getSceneManager().showWelcomeScene(WelcomeController.getUser());
    }

    // navigate to quiz view
    public void doQuiz() {
        Quiz quiz= quizList.getSelectionModel().getSelectedItem();
        if (quiz == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Fout");
            alert.setHeaderText("Selecteer eerst een quiz.");
            alert.showAndWait();
        } else {
            Main.getSceneManager().showFillOutQuiz(quiz);
        }
    }
}
