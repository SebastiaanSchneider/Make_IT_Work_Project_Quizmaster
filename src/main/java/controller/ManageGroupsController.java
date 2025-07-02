package controller;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import database.mysql.CourseDAO;
import database.mysql.DBAccess;
import database.mysql.GroupDAO;
import database.mysql.StudentGroupDAO;

import model.Course;
import model.Group;
import model.User;

import view.Main;

/**
 * Handles the functionality for the ManageGroups scene
 * @author Sebastiaan Schneider
 */
public class ManageGroupsController {
    @FXML
    private ListView<Group> groupList;
    @FXML
    private Label numberOfGroupsLabel;
    @FXML
    private Label nameRoleLabel;

    DBAccess dbAccess = Main.getDBaccess();
    GroupDAO groupDAO = new GroupDAO(dbAccess);
    CourseDAO courseDAO = new CourseDAO(dbAccess);
    StudentGroupDAO studentGroupDAO = new StudentGroupDAO(dbAccess);
    User user = WelcomeController.getUser();

    // creates the view with all available groups
    public void setup() {
        nameRoleLabel.setText(user.getFullName() + "\nRol: " + user.getRole().toString());
        List<Group> groupListFromDB = groupDAO.getAll();
        ObservableList<Group> groups =
                FXCollections.observableArrayList(groupListFromDB);
        groupList.setItems(groups);
        groupList.getSelectionModel().selectedItemProperty().addListener((
                observableValue, group, selectedGroup)
                -> {
            if (selectedGroup != null) {
                int count = groupDAO.countNumberOfGroups(selectedGroup);
                numberOfGroupsLabel.setText(String.valueOf(count));
            } else {
                numberOfGroupsLabel.setText("");
            }
        });
    }

    // delete group from database with confirmation pop-up
    public void doDeleteGroup() {
        Group group = groupList.getSelectionModel().getSelectedItem();
        if (group == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Fout");
            alert.setHeaderText("Selecteer een groep om te verwijderen.");
            alert.showAndWait();
        } else {
            Optional<ButtonType> confirm = showConfirmation(group).showAndWait();
            if (confirm.isPresent() && confirm.get() == ButtonType.OK) {
                groupDAO.deleteOne(group);
                groupList.getItems().remove(group);
            }
        }
    }

    // pop-up confirmation for doDeleteGroup
    public Alert showConfirmation(Group group) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Verwijderen");
        alert.setHeaderText("Groep " + group.getName() + " van cursus " +
                group.getCourse().getCourseName() +
                " wordt definitief verwijdert. Weet u het zeker?");
        return alert;
    }

    // move to main menu
    public void doMenu() {
        Main.getSceneManager().showWelcomeScene(WelcomeController.getUser());
    }

    // move to CreateUpdateGroup scene to create a new group
    public void doCreateGroup() {
        Main.getSceneManager().showCreateUpdateGroupScene(null);
    }

    // move to CreateUpdateGroup scene to modify an existing group
    public void doUpdateGroup() {
        Group group = groupList.getSelectionModel().getSelectedItem();
        if (group == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Fout");
            alert.setHeaderText("Selecteer een groep om te wijzigen.");
            alert.showAndWait();
        } else {
            Main.getSceneManager().showCreateUpdateGroupScene(group);
        }
    }

    // export to .txt file
    public void exportToFile() {
        List<Group> groupList;

        try (PrintWriter printWriter = new PrintWriter("Overzicht Groepen.txt")) {
            printWriter.println("Overzicht groepen\n");

            // iterate over every course
            for (Course course : courseDAO.getAll()) {
                printWriter.println("Cursus: " + course.getCourseName());
                groupList = groupDAO.getAllGroupsPerCourse(course);

                // test for if groupList = empty
                if (groupList.isEmpty()) {
                    printWriter.println("\tGeen groepen in cursus");
                } else {
                    // iterate over every group and add details
                    for (Group group : groupList) {
                        printWriter.println("\tGroepsnaam: " + group.getName() + "\n\tDocent: " +
                                group.getTeacher() + "\n\tMaximale capaciteit: " +
                                group.getCapacity() + "\n\tStudenten:");
                        // get every student for current group and add details
                        for (User student : studentGroupDAO.getAllUsersPerStudentGroupId(
                                group.getGroupId())) {
                            printWriter.println("\t\tNaam student: " + student.getFullName());
                        }
                        printWriter.println();
                    }
                }
                printWriter.println();
            }
        } catch (FileNotFoundException exception) {
            throw new RuntimeException(exception);
        }

        // create pop-up with succes confirmation
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText("Het overzicht is succesvol aangemaakt als \"Overzicht Groepen.txt\"");
        alert.showAndWait();
    }
}
