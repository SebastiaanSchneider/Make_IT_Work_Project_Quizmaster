package controller;

import database.mysql.UserDAO;
import database.mysql.DBAccess;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import model.User;
import view.Main;

import java.util.List;

public class ManageUsersController {

    @FXML
    private ListView<User> userList;
    @FXML
    private Label nameRoleLabel;

    DBAccess dbAccess = Main.getDBaccess();
    UserDAO userDAO = new UserDAO(dbAccess);
    User user = WelcomeController.getUser();

    /**
     * Initialiseert het scherm met gebruikersinformatie en toont alle gebruikers in de ListView.
     */
    public void setup() {
        nameRoleLabel.setText(user.getFullName() + "\nRol: " + user.getRole().toString());
        List<User> userListFromDB = userDAO.getAll();
        ObservableList<User> users = FXCollections.observableArrayList(userListFromDB);
        userList.setItems(users);

        // Format de weergave van gebruikers in de lijst
        userList.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                if (empty || user == null) {
                    setText(null);
                } else {
                    setText("Naam: " + user.getFullName());
                }
            }
        });
    }

    /**
     * Navigeert terug naar het welkomstmenu.
     */
    public void doMenu() {
        Main.getSceneManager().showWelcomeScene(WelcomeController.getUser());
    }

    /**
     * Opent het scherm voor het aanmaken van een nieuwe gebruiker.
     */
    public void doCreateUser() {
        Main.getSceneManager().showCreateUpdateUserScene(null);
    }

    /**
     * Opent het scherm om de geselecteerde gebruiker te bewerken.
     * Toont foutmelding als er geen gebruiker geselecteerd is.
     */
    public void doUpdateUser() {
        User user = userList.getSelectionModel().getSelectedItem();
        if (user == null) {
            showError().showAndWait();
        } else {
            Main.getSceneManager().showCreateUpdateUserScene(user);
        }
    }

    /**
     * Verwijdert de geselecteerde gebruiker uit de database en de lijst.
     */
    public void doDeleteUser() {
        User user = userList.getSelectionModel().getSelectedItem();
        userDAO.deleteOne(user);
        userList.getItems().remove(user);
    }

    /**
     * Toont een foutmelding wanneer geen gebruiker is geselecteerd.
     */
    public Alert showError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Foutmelding");
        alert.setHeaderText("Selecteer een gebruiker om te wijzigen.");
        return alert;
    }
}
