package controller;

import database.mysql.QuestionDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import model.Question;
import model.User;
import view.Main;

public class CreateUpdateQuestionController {

    @FXML
    private javafx.scene.control.TextArea questionTextArea;
    @FXML
    private javafx.scene.control.TextField correctAnswerField;
    @FXML
    private javafx.scene.control.TextField incorrectAnswer1Field;
    @FXML
    private javafx.scene.control.TextField incorrectAnswer2Field;
    @FXML
    private javafx.scene.control.TextField incorrectAnswer3Field;
    @FXML
    private Label nameRoleLabel;


    private QuestionDAO questionDao = new QuestionDAO(Main.getDBaccess());

    private Question currentQuestion;

    protected static String previousView = ""; // needed for correct functioning of the back button
    User user = WelcomeController.getUser();

    public void setup(Question question) {
        currentQuestion = question;
        User coordinator = WelcomeController.getUser();
        questionTextArea.setText(question.getQuestionText());
        correctAnswerField.setText(question.getCorrectAnswer());
        incorrectAnswer1Field.setText(question.getIncorrectAnswer1());
        incorrectAnswer2Field.setText(question.getIncorrectAnswer2());
        incorrectAnswer3Field.setText(question.getIncorrectAnswer3());
        nameRoleLabel.setText(user.getFullName() + "\nRol: " + user.getRole().toString());
    }


    public void doMenu() {
        Main.getSceneManager().showWelcomeScene(WelcomeController.getUser());
    }

    public void goBackToPreviousPage() {
        if (previousView.equals("Dashboard")) {
            Main.getSceneManager().showCoordinatorDashboard();
        } else if (previousView.equals("Manager")){
            Main.getSceneManager().showManageQuestionsScene();
        }
    }

    public void showMessage(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(message);
        alert.showAndWait();
    }


    public void doCreateUpdateQuestion() {


        if (questionTextArea.getText().isBlank() ||
                correctAnswerField.getText().isBlank() || incorrectAnswer1Field.getText().isBlank() ||
                incorrectAnswer2Field.getText().isBlank() || incorrectAnswer3Field.getText().isBlank()) {
            showMessage("Fout", "Vul alle velden in.", Alert.AlertType.WARNING);
            return;
        }


        boolean isSame =
                questionTextArea.getText().equals(currentQuestion.getQuestionText()) &&
                        correctAnswerField.getText().equals(currentQuestion.getCorrectAnswer()) &&
                        incorrectAnswer1Field.getText().equals(currentQuestion.getIncorrectAnswer1()) &&
                        incorrectAnswer2Field.getText().equals(currentQuestion.getIncorrectAnswer2()) &&
                        incorrectAnswer3Field.getText().equals(currentQuestion.getIncorrectAnswer3());

        if (isSame) {
            showMessage("Geen wijzigingen", "U heeft geen wijzigingen aangebracht.", Alert.AlertType.INFORMATION);
            return;
        }

        // create/update a new question
        currentQuestion.setQuestionText(questionTextArea.getText());
        currentQuestion.setCorrectAnswer(correctAnswerField.getText());
        currentQuestion.setIncorrectAnswer1(incorrectAnswer1Field.getText());
        currentQuestion.setIncorrectAnswer2(incorrectAnswer2Field.getText());
        currentQuestion.setIncorrectAnswer3(incorrectAnswer3Field.getText());

        // save
        questionDao.storeOne(currentQuestion);

        // info msg
        showMessage("Opgeslagen", "De vraag is opgeslagen.", Alert.AlertType.INFORMATION);

        Main.getSceneManager().showManageQuestionsScene();

    }
}