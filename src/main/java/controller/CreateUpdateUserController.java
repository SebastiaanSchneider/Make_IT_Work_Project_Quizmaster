package controller;

import database.mysql.DBAccess;
import database.mysql.UserDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Role;
import model.User;
import view.Main;

public class CreateUpdateUserController {

    @FXML
    private TextField firstNameField;
    @FXML
    private TextField infixField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private ComboBox<Role> roleComboBox;
    @FXML
    private Label roleCountLabel;
    @FXML
    private Label nameRoleLabel;

    private final DBAccess dbAccess = Main.getDBaccess();
    private final UserDAO userDAO = new UserDAO(dbAccess);
    private User user = null;

    /**
     * Initialiseert het scherm met gebruikersdata (indien bewerken) en stelt rollen in.
     */
    public void setup(User user) {
        this.user = user;
        roleComboBox.getItems().addAll(Role.values());
        User loggedInUser = WelcomeController.getUser();
        nameRoleLabel.setText(loggedInUser.getFullName() + "\nRol: " + loggedInUser.getRole().toString());

        if (user != null) {
            firstNameField.setText(user.getFirstname());
            infixField.setText(user.getInfix());
            lastNameField.setText(user.getLastname());
            usernameField.setText(user.getUsername());
            passwordField.setText(user.getPassword());
            roleComboBox.setValue(user.getRole());
            updateRoleCountLabel(user.getRole());
        }

        roleComboBox.setOnAction(e -> updateRoleCountLabel(roleComboBox.getValue()));
    }

    /**
     * Maakt een nieuwe gebruiker aan (of overschrijft bestaande) en slaat deze op in de database.
     */
    public void doCreateUpdateUser() {
        // Controle op lege velden
        if (firstNameField.getText().isEmpty()
                || lastNameField.getText().isEmpty()
                || usernameField.getText().isEmpty()
                || passwordField.getText().isEmpty()
                || roleComboBox.getValue() == null) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Foutmelding");
            alert.setHeaderText("Invoervelden niet volledig");
            alert.setContentText("Alle verplichte velden moeten worden ingevuld:\n- Voornaam\n- Achternaam\n- Gebruikersnaam\n- Wachtwoord\n- Rol");
            alert.showAndWait();
            return;
        }

        // Maak gebruiker aan en sla op
        User newUser = new User(
                usernameField.getText(),
                passwordField.getText(),
                firstNameField.getText(),
                infixField.getText(),
                lastNameField.getText(),
                roleComboBox.getValue()
        );
        userDAO.storeOne(newUser);
        showAlert("Gebruiker succesvol aangemaakt.");
    }


    /**
     * Gaat terug naar het hoofdmenu (welkomstscherm).
     */
    public void doMenu() {
        Main.getSceneManager().showWelcomeScene(WelcomeController.getUser());
    }

    /**
     * Gaat terug naar het gebruikersbeheer-scherm.
     */
    public void goBackUser() {
        Main.getSceneManager().showManageUserScene();
    }

    /**
     * Berekent en toont hoeveel gebruikers dezelfde rol hebben als geselecteerd in de combobox.
     */
    private void updateRoleCountLabel(Role role) {
        int count = userDAO.countUsersPerRole(role);
        roleCountLabel.setText("Aantal gebruikers met dezelfde rol: " + count);
    }

    /**
     * Toont een pop-upmelding met meegegeven bericht.
     */
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informatie");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
