package controller;

import java.util.List;
import java.util.Objects;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import database.mysql.CourseDAO;
import database.mysql.DBAccess;
import database.mysql.GroupDAO;
import database.mysql.UserDAO;

import model.Course;
import model.Group;
import model.Role;
import model.User;

import view.Main;

/**
 * Handles the functionality for the CreateUpdateGroup scene
 * @author Sebastiaan Schneider
 */
public class CreateUpdateGroupController {
    // aanmaak variabelen vanuit fxml
    @FXML
    private TextField groupName;
    @FXML
    private TextField groupCapacity;
    @FXML
    private ComboBox<Course> course;
    @FXML
    private ComboBox<User> teacher;
    @FXML
    private Label nameRoleLabel;

    // variabelen met verbinding met db
    DBAccess dbAccess = Main.getDBaccess();
    GroupDAO groupDAO = new GroupDAO(dbAccess);
    UserDAO userDAO = new UserDAO(dbAccess);
    CourseDAO courseDAO = new CourseDAO(dbAccess);
    int groupId;
    User user = WelcomeController.getUser();

    // setup die de velden invult
    public void setup(Group setupGroup) {
        nameRoleLabel.setText(user.getFullName() + "\nRol: " + user.getRole().toString());
        course.getItems().clear();
        teacher.getItems().clear();

        List<User> userList = userDAO.getUsersPerRole(Role.DOCENT);
        teacher.getItems().addAll(userList);
        List<Course> courseList = courseDAO.getAll();
        course.getItems().addAll(courseList);

        // vul de velden in met de gegevens van ingevoerde group
        if (setupGroup != null) {
            Group group = groupDAO.getGroupByName(setupGroup.getName());
            groupName.setText(group.getName());
            groupCapacity.setText(String.valueOf(group.getCapacity()));
            course.setValue(group.getCourse());
            teacher.setValue(group.getTeacher());
            this.groupId = group.getGroupId();
        }
    }

    // create or modify new group using storeOne
    public void doCreateUpdateGroup() {
        // try block to ensure only a succesful attempt gets forwarded
        try {
            String name = groupName.getText();
            User teacherValue = teacher.getValue();
            Course courseValue = course.getValue();

            // check for empty fields
            if (name.isEmpty() || teacherValue == null || courseValue == null) {
                showMessage("Lege velden", "Alle velden moeten worden ingevuld!").showAndWait();
                return;
            }

            // check for valid capacity
            int capacity = Integer.parseInt(groupCapacity.getText());
            if (capacity < 1) {
                showMessage("Onjuiste groepscapaciteit",
                        "De groepscapaciteit moet 1 of groter zijn!").showAndWait();
                return;
            }

            // set up Group Object
            Group group = new Group(groupId, name, capacity, teacherValue, courseValue);
            // check for duplicate name
            for (Group checkGroup: groupDAO.getAllGroupsPerCourse(group.getCourse())) {
                System.out.println(checkGroup.getName());
                System.out.println(group.getName());
                if (Objects.equals(checkGroup.getName(), group.getName())) {
                    showMessage("Ongeldige groepsnaam",
                            "Groepsnaam bestaat al binnen deze cursus.").showAndWait();
                    return;
                }
            }

            // no issues found, so store group
            groupDAO.storeOne(group);
            showMessage("Opgeslagen", "De groep is opgeslagen.").showAndWait();
            Main.getSceneManager().showManageGroupsScene();
        } catch (IllegalArgumentException exception) {
            System.out.println(exception.getMessage());
        }
    }

    // alert function
    public Alert showMessage(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(message);
        return alert;
    }

    // move to menu scene
    public void doMenu() {
        Main.getSceneManager().showWelcomeScene(WelcomeController.getUser());
    }

    // move to ManageGroups scene
    public void doBack() {
        Main.getSceneManager().showManageGroupsScene();
    }
}
