package controller;

import database.mysql.CourseDAO;
import database.mysql.DBAccess;
import database.mysql.RegistrationDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import model.Course;
import model.Registration;
import model.User;
import view.Main;

import java.util.List;

public class StudentSignInOutController {

    @FXML
    private ListView<Course> signedOutCourseList;
    @FXML
    private ListView<Course> signedInCourseList;
    @FXML
    private Label nameRoleLabel;

    // access to databases
    private final static DBAccess dbAccess = Main.getDBaccess();
    private final static CourseDAO courseDAO = new CourseDAO(dbAccess);
    private final static RegistrationDAO registrationDAO = new RegistrationDAO(dbAccess);
    private final User student = WelcomeController.getUser();

    /**
     * Initializes lists with courses for signing in and out.
     */
    public void setup() {
        nameRoleLabel.setText(student.getFullName() + "\nRol: " + student.getRole().toString());
        setUpSignedIn();
        setUpSignedOut();
    }

    /**
     * Initializes list with signed in courses.
     * <p>
     * Gets all courses for current user from {@code registrationDAO} and fills list.
     */
    public void setUpSignedIn() {
        List<Course> registrationsfromDB = registrationDAO.getAllCoursesPerUser(student);
        ObservableList<Course> registrations = FXCollections.observableArrayList(registrationsfromDB);
        signedInCourseList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        signedInCourseList.setItems(registrations);
    }

    /**
     * Initializes list with signed out courses.
     * <p>
     * Gets all courses for current user from {@code registrationDAO} and removes registrations.
     */
    public void setUpSignedOut() {
        List<Course> registrationsfromDB = registrationDAO.getAllCoursesPerUser(student);
        List<Course> courseListFromDB = courseDAO.getAll();
        courseListFromDB.removeAll(registrationsfromDB);
        ObservableList<Course> courses = FXCollections.observableArrayList(courseListFromDB);
        signedOutCourseList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        signedOutCourseList.setItems(courses);
    }

    /**
     * Navigates back to menu (WelcomeController) for current user.
     */
    public void doMenu() {
        Main.getSceneManager().showWelcomeScene(WelcomeController.getUser());
    }

    /**
     * To sign in current user to course.
     */
    public void doSignIn() {
        ObservableList<Course> selectedCourses = signedOutCourseList.getSelectionModel().getSelectedItems();
        if (selectedCourses.isEmpty()) {
            showWarning().showAndWait();
            return;
        }
        for (Course course : selectedCourses) {
            registrationDAO.storeOne(new Registration(student, course));
        }
        setUpSignedIn();
        setUpSignedOut();
        showInfo().showAndWait();
    }

    /**
     * Navigates back to log-in (LoginScene)
     */
    public void doSignOut() {
        ObservableList<Course> selectedCourses = signedInCourseList.getSelectionModel().getSelectedItems();
        if (selectedCourses.isEmpty()) {
            showWarning().showAndWait();
            return;
        }
        for (Course course : selectedCourses) {
            registrationDAO.deleteOne(new Registration(student, course));
        }
        setUpSignedIn();
        setUpSignedOut();
        showInfo().showAndWait();
    }

    /**
     * Creates info pop-up after saving
     *
     * @return pop-up with info
     */
    public Alert showInfo() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Opgeslagen");
        alert.setHeaderText("De wijzigingen zijn opgeslagen.");
        return alert;
    }

    /**
     * Creates warning pop-up if no selected course
     *
     * @return pop-up with warning
     */
    public Alert showWarning() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Geen cursus geselecteerd");
        alert.setHeaderText("Selecteer een cursus.");
        return alert;
    }
}
