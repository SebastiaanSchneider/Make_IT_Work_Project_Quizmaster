package controller;

import database.mysql.DBAccess;
import javacouchdb.CouchDBAccess;
import javacouchdb.QuizResultCouchDBDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import model.Quiz;
import model.QuizResult;
import model.User;
import view.Main;

import java.util.List;

public class StudentFeedbackController {

    @FXML
    private Label feedbackLabel; // Label voor het tonen van de feedbacktitel

    @FXML
    private Label nameRoleLabel; // Label voor het tonen van de naam en rol van de gebruiker

    @FXML
    private FillOutQuizController fillOutQuizController; // Referentie naar de quizcontroller

    @FXML
    private ListView<QuizResult> feedbackListView; // ListView voor het tonen van quizresultaten

    // Databasetoegang tot MySQL en CouchDB
    DBAccess dbAccess = Main.getDBaccess();
    CouchDBAccess couchDBAccess = Main.getCouchDBAccess();
    QuizResultCouchDBDAO quizResultCouchDBDAO = new QuizResultCouchDBDAO(couchDBAccess);

    private User currentUser; // Huidige ingelogde gebruiker
    private Quiz currentQuiz;

    /**
     * Deze methode initialiseert het feedbackscherm na het invullen van een quiz.
     * Toont een alert of de student geslaagd is, en zet de feedbackgegevens in de lijst.
     */
    public void setup(Quiz selectedQuiz, boolean isPassed) {
        this.currentUser = WelcomeController.getUser();
        this.currentQuiz = selectedQuiz;
        // Toon de naam en rol van de gebruiker
        nameRoleLabel.setText(currentUser.getFullName() + "\nRol: " + currentUser.getRole().toString());
        // Toon geslaagd of niet geslaagd in een popup
        String message = isPassed ? "Je bent geslaagd!" : "Je bent niet geslaagd.";
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Resultaat");
        alert.setHeaderText(message);
        alert.showAndWait();
        // Zet de titel boven de feedback
        feedbackLabel.setText("Feedback voor quiz: " + selectedQuiz.getQuizName());
        // Haal quizresultaten op voor deze gebruiker en quiz
        List<QuizResult> results = quizResultCouchDBDAO.getQuizResultsByUserIdAndQuizId(
                currentUser.getUserId(), selectedQuiz.getQuizId());
        // Zet de resultaten in de ListView
        ObservableList<QuizResult> quizResults = FXCollections.observableArrayList(results);
        feedbackListView.setItems(quizResults);
    }

    /**
     * Zet een verwijzing naar de controller die de quiz afhandelt.
     */
    public void setQuizController(FillOutQuizController fillOutQuizController) {
        this.fillOutQuizController = fillOutQuizController;
    }

    /**
     * Wordt aangeroepen om te bepalen of de quiz geslaagd is.
     */
    public void showResult() {
        if (fillOutQuizController != null && currentQuiz != null) {
            boolean passed = fillOutQuizController.isQuizPassed();
            setup(currentQuiz, passed);
        } else {
            // Veiligheidscontrole: toon melding als iets ontbreekt
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Fout");
            alert.setHeaderText("Quizgegevens ontbreken");
            alert.setContentText("Kan resultaat niet tonen omdat de quiz of controller niet beschikbaar is.");
            alert.showAndWait();
        }
    }

    /**
     * Navigeert terug naar het welkomstscherm met de huidige gebruiker.
     */
    public void doMenu() {
        Main.getSceneManager().showWelcomeScene(currentUser);
    }
}
