package controller;

import database.mysql.DBAccess;
import database.mysql.UserDAO;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import model.User;
import view.Main;

/**
 * Gets the functionality for logging in.
 * <p>
 * Authenticates user.
 */
public class LoginController {

    @FXML
    private TextField nameTextField;
    @FXML
    private TextField passwordField;

    // access to database and DAO's
    DBAccess dbAccess = Main.getDBaccess();
    UserDAO userDAO = new UserDAO(dbAccess);

    /**
     * Authenticates user and goes to next scene.
     * <p>
     * Checks combination username and password in databas {@code UserDAO}
     * and goes to next scene (WelcomeScene).
     */
    public void doLogin() {
        String username = nameTextField.getText();
        String password = passwordField.getText();
        User user = userDAO.authenticate(username, password);
        if (user != null) {
            Main.getSceneManager().showWelcomeScene(user);
        } else {
            showError().showAndWait();
        }
    }

    /**
     * Creates error message for authentication.
     *
     * @return pop-up with error.
     */
    public Alert showError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Foutmelding");
        alert.setHeaderText("De combinatie van username en" + " wachtwoord is niet juist.");
        return alert;
    }

    /**
     * Stops program.
     */
    public void doQuit(ActionEvent event) {
        Platform.exit();
    }
}
