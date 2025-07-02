/**
 * Controller for the Manage Quizzes view
 *
 * @author Michiel van Haren
 */


package controller;

import database.mysql.QuizDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Quiz;
import model.User;
import util.MichielFileHandler;
import view.Main;

import java.util.List;
import java.util.Optional;


public class ManageQuizzesController {
    // Attributes ----------------------------------------------------------------------------------
    private final QuizDAO quizDAO;
    private final MichielFileHandler fileHandler;

    @FXML
    private ListView<Quiz> quizList;
    @FXML
    private Label nameRoleLabel;


    // Constructors --------------------------------------------------------------------------------
    public ManageQuizzesController() {
        this.quizDAO = new QuizDAO(Main.getDBaccess());
        this.fileHandler = new MichielFileHandler();
    }

    // Methods -------------------------------------------------------------------------------------

    /**
     * Initial setup of the view and its controls
     */
    public void setup() {
        // Retrieve currently logged in user
        User coordinator = WelcomeController.getUser();
        nameRoleLabel.setText(coordinator.getFullName() + "\nRol: " + coordinator.getRole().toString());

        // Retrieve a list of all quizzes in the database belonging to courses
        // coordinated by the currently logged in user
        List<Quiz> quizListFromDB = quizDAO.getAllByCoordinator(coordinator);
        ObservableList<Quiz> quizzes = FXCollections.observableArrayList(quizListFromDB);
        // Show all quizzes in the quizListView
        quizList.setItems(quizzes);

        if (quizList.getItems().isEmpty()) {
            showError("Verbind je eerst als coördinator aan een cursus" +
                    "om de quizzen te kunnen beheren").showAndWait();
        }

        // Custom string format for showing the quizzes in the quizListView
        quizList.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Quiz quiz, boolean empty) {
                super.updateItem(quiz, empty);
                if (empty || quiz == null) {
                    setText(null);
                } else {
                    setText(quiz.getQuizName() + "\n(Cursus: " + quiz.getCourse().getCourseName() + ")"
                            // Show number of questions for each quiz
                            + "\nAantal vragen: " + quiz.countNumberOfQuestionsAlternative());
                }
            }
        });
    } // setup


    // Menu button functionality
    public void doMenu() {
        Main.getSceneManager().showWelcomeScene(WelcomeController.getUser());
    }


    /* Here is where we choose whether to show the update or create new screen.
    When we want to create a new quiz, we pass null to the showCreateUpdateQuizScene method
    This will trigger the if statement in the setup method in the CreateUpdateQuizController */

    /**
     * functionality for the create new quiz button
     */
    public void doCreateQuiz() {
        Main.getSceneManager().showCreateUpdateQuizScene(null);
        CreateUpdateQuizController.previousView = "Manager";
    }


    /* When we want to update an existing quiz, we pass on the selected quiz from the quizList
    to the showCreateUpdateQuizScene method, meaning we will edit an existing quiz
    and the showCreateUpdateQuizScene method will pass on the selected quiz
    to the setup method of the showCreateUpdateQuiz view. */

    /**
     * functionality for the update quiz button
     */
    public void doUpdateQuiz() {
        Quiz quiz = quizList.getSelectionModel().getSelectedItem();
        // if no quiz is selected when the user presses the "wijzigen" button, an error message is shown
        if (quiz == null) {
            showError("Selecteer een quiz om te wijzigen.").showAndWait();
        } else {
            Main.getSceneManager().showCreateUpdateQuizScene(quiz);
            CreateUpdateQuizController.previousView = "Manager";
        }
    }

    /**
     * functionality for the export button
     */
    public void doExport() {
        Optional<ButtonType> confirm = showExportConfirmation().showAndWait();
        if (confirm.isPresent() && confirm.get() == ButtonType.OK) {
            // checkt of er een button is geklikt & of de OK button is geklikt
            fileHandler.exportQuizzesFile();
            showExportInfo().showAndWait();
        }
    }

    /**
     * functionality for the delete quiz button
     */
    public void doDeleteQuiz() {
        // make a new quiz object of the quiz currently selected in the quizList
        Quiz quiz = quizList.getSelectionModel().getSelectedItem();
        // if no quiz is selected when the user presses the "wijzigen" button, an error message is shown
        if (quiz == null) {
            showError("Selecteer een quiz om te verwijderen.").showAndWait();
        } else {
            Optional<ButtonType> confirm = showDeleteConfirmation(quiz).showAndWait();
            if (confirm.isPresent() && confirm.get() == ButtonType.OK) {
                // checkt of er een button is geklikt & of de OK button is geklikt
                // delete this quiz from the database
                quizDAO.deleteOne(quiz);
                // delete this quiz from the quizList
                quizList.getItems().remove(quiz);
            }
        }
    }


    /**
     * Shows an popup with the specified error message
     *
     * @param errorMessage the error message to be shown
     */
    public Alert showError(String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Foutmelding");
        alert.setHeaderText(errorMessage);
        return alert;
    }


    /**
     * Shows a confirmation dialog when attempting to delete a quiz
     * @param quiz the quiz which name will be shown in the dialog
     */
    public Alert showDeleteConfirmation(Quiz quiz) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Bevestiging");
        alert.setHeaderText("De quiz " + quiz.getQuizName() +
                " wordt definitief verwijderd. Weet u het zeker?");
        return alert;
    }


    /**
     * Shows a confirmation dialog before exporting quizzes to a file
     */
    public Alert showExportConfirmation() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Bevestiging");
        alert.setHeaderText("Weet u zeker dat u alle quizzen wilt exporteren?");
        return alert;
    }

    /**
     * Shows information after succesfully exporting quizzes to a textfile
     *
     * @return an alert to be shown when quizzes are exported.
     */
    public Alert showExportInfo() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        User coordinator = WelcomeController.getUser();
        alert.setTitle("Geëxporteerd");
        alert.setHeaderText("De quizzen zijn geëxporteerd." +
                "\n/export/" + coordinator.getUsername() + "-quizzen.txt");
        return alert;
    }


}
