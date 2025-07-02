package controller;


import database.mysql.*;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import model.Course;
import model.Group;
import model.Level;
import model.User;
import view.Main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Optional;

/**
 * Functionality for the ManageCourses scene.
 * <p>
 * Fills table with all courses.
 */
public class ManageCoursesController {

    @FXML
    private TableView<Course> courseList;
    @FXML
    private TableColumn<Course, String> courseNameCol;
    @FXML
    private TableColumn<Course, Level> levelCol;
    @FXML
    private TableColumn<Course, User> coordinatorCol;
    @FXML
    public TableColumn<Course, Integer> numberOfStudents;
    @FXML
    private Label selectionError;
    @FXML
    private Label nameRoleLabel;

    // access to databases and DAO's
    DBAccess dbAccess = Main.getDBaccess();
    CourseDAO courseDAO = new CourseDAO(dbAccess);
    RegistrationDAO registrationDAO = new RegistrationDAO(dbAccess);
    GroupDAO groupDAO = new GroupDAO(dbAccess);
    StudentGroupDAO studentGroupDAO = new StudentGroupDAO(dbAccess);
    User user = WelcomeController.getUser();

    /**
     * Initializes courseList for GUI.
     * <p>
     * Gets all courses from {@code courseDAO} and fills the list. Shows number of students.
     */
    public void setup() {
        nameRoleLabel.setText(user.getFullName() + "\nRol: " + user.getRole().toString());
        ObservableList<Course> courses = FXCollections.observableArrayList(courseDAO.getAll());
        courseList.setItems(courses);
        courseNameCol.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        levelCol.setCellValueFactory(new PropertyValueFactory<>("courseLevel"));
        coordinatorCol.setCellValueFactory(new PropertyValueFactory<>("coordinator"));
        numberOfStudents.setCellValueFactory(cellData -> {
            Course course = cellData.getValue();
            int count = registrationDAO.getAllUsersPerCourse(course).size();
            return new ReadOnlyObjectWrapper<>(count);
        });
    }

    /**
     * Navigates back to menu (WelcomeController) for current user.
     */
    public void doMenu() {
        Main.getSceneManager().showWelcomeScene(WelcomeController.getUser());
    }

    /**
     * Navigates to scene (CreateUpdateCourse) for filling in a new course.
     */
    public void doCreateCourse() {
        Main.getSceneManager().showCreateUpdateCourseScene(null);
    }

    /**
     * Navigates  to scene (CreateUpdateCourse) for updating selected course.
     */
    public void doUpdateCourse() {
        Course course = courseList.getSelectionModel().getSelectedItem();
        if (course == null) {
            selectionError.setText("Selecteer eerst een cursus.");
        } else {
            Main.getSceneManager().showCreateUpdateCourseScene(course);
        }
    }

    /**
     * Deletes selected course.
     * <p>
     * Checks for confirmation before deleting by {@code courseDAO}
     */
    public void doDeleteCourse() {
        Course course = courseList.getSelectionModel().getSelectedItem();
        if (course == null) {
            selectionError.setText("Selecteer eerst een cursus.");
        } else {
            Optional<ButtonType> confirm = showConfirmation(course).showAndWait();
            if (confirm.isPresent() && confirm.get() == ButtonType.OK) {
                courseDAO.deleteOne(course);
                courseList.getItems().remove(course);
            }
        }
    }

    /**
     * Asks for confirmation before deleting selected course.
     *
     * @return pop-up that asks for confirmation.
     */
    public Alert showConfirmation(Course course) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Verwijderen");
        alert.setHeaderText(course.getCourseName() +
                " en bijbehorende quizzen worden definitief verwijderd. Weet u het zeker?");
        return alert;
    }

    /**
     * Create a document from all courses, groups and students.
     * <p>
     * Gets all courses, groups and students by
     * {@code courseDAO, registrationDAO, groupDAO, studentGroupDAO}.
     *
     * @param file all courses, groups and students.
     */
    public void exportToFile(File file) {
        try (PrintWriter pw = new PrintWriter(file)) {
            for (Course course : courseDAO.getAll()) {
                pw.printf("Cursus: %s (Niveau: %s)" + "\nAantal ingeschreven studenten: %d\n",
                        course.getCourseName(), course.getCourseLevel(),
                        registrationDAO.getAllUsersPerCourse(course).size());
                for (Group group : groupDAO.getAllGroupsPerCourse(course)) {
                    pw.printf("\tGroep: %s (Docent %s)\n", group.getName(), group.getTeacher().getFullName());
                    int number = studentGroupDAO.getAllUsersPerStudentGroupId(group.getGroupId()).size();
                    pw.println("\t\tAantal studenten: " + number);
                    pw.println();
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
        }
    }

    /**
     * Setup to export to chosen location.
     */
    public void exportWithFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exporteer cursussen");
        fileChooser.setInitialFileName("Cursussen.txt");
        Window window = courseList.getScene().getWindow();
        File file = fileChooser.showSaveDialog(window);
        if (file != null) {
            exportToFile(file);
        }
    }
}

