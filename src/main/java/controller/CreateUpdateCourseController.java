package controller;

import database.mysql.CourseDAO;
import database.mysql.DBAccess;
import database.mysql.UserDAO;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;
import model.Course;
import model.Level;
import model.Role;
import model.User;
import view.Main;

import java.util.List;

/**
 * Functionality for scene for updating or adding courses.
 */
public class CreateUpdateCourseController {

    @FXML
    private TextField courseName;
    @FXML
    private ComboBox<Level> courseLevel;
    @FXML
    private ComboBox<User> coordinator;
    @FXML
    private Label courseNameError;
    @FXML
    private Label courseLevelError;
    @FXML
    private Label coordinatorError;
    @FXML
    private Label nameRoleLabel;

    // access to databases and DAO's
    DBAccess dbAccess = Main.getDBaccess();
    UserDAO userdao = new UserDAO(dbAccess);
    CourseDAO coursedao = new CourseDAO(dbAccess);
    int courseId = 0;
    User user = WelcomeController.getUser();

    /**
     * Fills fields coordinator, level and name for GUI.
     * <p>
     * Gets all users with role coordinator from {@code userDAO} and fills the list.
     *
     * @param filledcourse the selected course from previous scene (ManageCourses)
     */
    public void setup(Course filledcourse) {
        nameRoleLabel.setText(user.getFullName() + "\nRol: " + user.getRole().toString());
        coordinator.getItems().clear();
        courseLevel.getItems().clear();
        List<User> userList = userdao.getUsersPerRole(Role.COORDINATOR);
        coordinator.getItems().addAll(userList);
        courseLevel.getItems().addAll(Level.values());
        if (filledcourse != null) {
            courseName.setText(filledcourse.getCourseName());
            courseLevel.setValue(filledcourse.getCourseLevel());
            coordinator.setValue(filledcourse.getCoordinator());
            this.courseId = filledcourse.getCourseId();
        }
    }

    /**
     * Navigates back to menu (WelcomeController) for current user.
     */
    public void doMenu() {
        Main.getSceneManager().showWelcomeScene(WelcomeController.getUser());
    }

    /**
     * To update or create a course in the database.
     * <p>
     * Check if all fields are filled, and if course name is unique.
     * Then stores in database bu {@code courseDAO}
     */
    public void doCreateUpdateCourse() {
        if (validate()) {
            Course course = new Course(courseId, courseLevel.getValue(), courseName.getText().trim(),
                    coordinator.getValue());
            if (coursedao.courseNameExists(course.getCourseName(), courseId)) {
                courseNameError.setText("Al in gebruik. Kies een andere naam.");
                return;
            }
            coursedao.storeOne(course);
            showInfo();
            Main.getSceneManager().showManageCoursesScene();
        }
    }

    /**
     * Checks if all required fields are filled.
     *
     * @return if all fields are filled
     */
    public boolean validate() {
        return validateCourseName() && validateCourseLevel() && validateCoordinator();
    }

    /**
     * Checks if courseName is valid
     *
     * @return if valid
     */
    public boolean validateCourseName() {
        if (courseName.getText().trim().isEmpty()) {
            courseName.setStyle("-fx-border-color: red; -fx-border-width: 2;");
            courseNameError.setText("Vul een cursusnaam in.");
            return false;
        } else {
            courseName.setStyle("");
            courseNameError.setText("");
            return true;
        }
    }

    /**
     * Checks if courselevel is valid
     *
     * @return if valid
     */
    public boolean validateCourseLevel() {
        if (courseLevel.getValue() == null) {
            courseLevel.setStyle("-fx-border-color: red;");
            courseLevelError.setText("Vul een niveau in.");
            return false;
        } else {
            courseLevel.setStyle("");
            courseLevelError.setText("");
            return true;
        }
    }

    /**
     * Checks if coordinator is valid.
     *
     * @return if valid.
     */
    public boolean validateCoordinator() {
        if (coordinator.getValue() == null) {
            coordinator.setStyle("-fx-border-color: red;");
            coordinatorError.setText("Vul een coördinator in.");
            return false;
        } else {
            coordinator.setStyle("");
            coordinatorError.setText("");
            return true;
        }
    }

    /**
     * Navigates back to previous screen (ManageCourses).
     */
    public void goback() {
        Main.getSceneManager().showManageCoursesScene();
    }

    /**
     * Creates pop-up when course is saved.
     */
    public void showInfo() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Opgeslagen");
        alert.setHeaderText("De cursus is opgeslagen.");
        alert.getButtonTypes().setAll(ButtonType.CLOSE);
        alert.show();
        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(e -> alert.close());
        delay.play();
    }
}

