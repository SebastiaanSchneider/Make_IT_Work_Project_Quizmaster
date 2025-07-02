package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import model.User;
import view.Main;

import java.util.ArrayList;
import java.util.List;

/**
 * To get functionality for the scene Welcome.
 * <p>
 * Fills the tasks depending on the role.
 */
public class WelcomeController {
    private static User user;

    public static User getUser() {
        return user;
    }

    public void setUser(User user) {
        WelcomeController.user = user;
    }

    @FXML
    private Label welcomeLabel;
    @FXML
    private MenuButton taskMenuButton;
    @FXML
    private Label nameRoleLabel;

    /**
     * Initializes tasks for current user.
     * <p>
     * Gets all courses from {@code courseDAO} and fills the list. Shows number of students.
     *
     * @param user current logged-in user.
     */
    public void setup(User user) {
        setUser(user);
        taskMenuButton.getItems().clear();
        taskMenuButton.getItems().setAll(tasksperRole(user));
        welcomeLabel.setText("Welkom " + user);
        nameRoleLabel.setText(user.getFullName() + "\nRol: " + user.getRole().toString());
    }

    /**
     * Checks user role and fills list with tasks.
     *
     * @param user the current logged-in user.
     * @return list with tasks belonging to user.
     */
    public static List<MenuItem> tasksperRole(User user) {
        switch (user.getRole()) {
            case STUDENT:
                return tasksStudent();
            case COORDINATOR:
                return tasksCoordinator();
            case ADMINISTRATOR:
                return tasksAdministrator();
            case FUNCTIONEEL_BEHEERDER:
                return tasksFunctional();
            default:
                throw new IllegalArgumentException("Onbekende rol");
        }
    }

    /**
     * Fills list with tasks for student.
     * <p>
     * Adds menu-buttons with actions when clicked.
     *
     * @return list with tasks for student.
     */
    public static List<MenuItem> tasksStudent() {
        List<MenuItem> resultList = new ArrayList<>();
        MenuItem registration = new MenuItem("In- of uitschrijven cursus");
        registration.setOnAction(e -> Main.getSceneManager().showStudentSignInOutScene());
        MenuItem makeQuiz = new MenuItem("Maak een quiz");
        makeQuiz.setOnAction(e -> Main.getSceneManager().showSelectQuizForStudent());
        resultList.add(registration);
        resultList.add(makeQuiz);
        return resultList;
    }

    /**
     * Fills list with tasks for coordinator.
     * <p>
     * Adds menu-buttons with actions when clicked.
     *
     * @return list with tasks for coordinator.
     */
    public static List<MenuItem> tasksCoordinator() {
        List<MenuItem> resultList = new ArrayList<>();
        MenuItem dashboard = new MenuItem("Dashboard");
        dashboard.setOnAction(e -> Main.getSceneManager().showCoordinatorDashboard());
        MenuItem manageQuiz = new MenuItem("Beheer quizzen");
        manageQuiz.setOnAction(e -> Main.getSceneManager().showManageQuizScene());
        MenuItem manageQuestions = new MenuItem("Beheer vragen");
        manageQuestions.setOnAction(e -> Main.getSceneManager().showManageQuestionsScene());
        resultList.add(dashboard);
        resultList.add(manageQuiz);
        resultList.add(manageQuestions);
        return resultList;
    }

    /**
     * Fills list with tasks for administrator.
     * <p>
     * Adds menu-buttons with actions when clicked.
     *
     * @return list with tasks for administrator.
     */
    public static List<MenuItem> tasksAdministrator() {
        List<MenuItem> resultList = new ArrayList<>();
        MenuItem manageCourses = new MenuItem("Beheer cursussen");
        manageCourses.setOnAction(e -> Main.getSceneManager().showManageCoursesScene());
        MenuItem manageGroups = new MenuItem("Beheer groepen");
        manageGroups.setOnAction(e -> Main.getSceneManager().showManageGroupsScene());
        MenuItem manageStudents = new MenuItem("Deel groepen in");
        manageStudents.setOnAction(e -> Main.getSceneManager().showAssignStudentsToGroupScene());
        resultList.add(manageCourses);
        resultList.add(manageGroups);
        resultList.add(manageStudents);
        return resultList;
    }

    /**
     * Fills list with tasks for functional beheerder.
     * <p>
     * Adds menu-buttons with actions when clicked.
     *
     * @return list with tasks for functional beheerder.
     */
    public static List<MenuItem> tasksFunctional() {
        List<MenuItem> resultList = new ArrayList<>();
        MenuItem manageUsers = new MenuItem("Beheer gebruikers");
        manageUsers.setOnAction(e -> Main.getSceneManager().showManageUserScene());
        resultList.add(manageUsers);
        return resultList;
    }

    /**
     * Goes back to log-in (LoginScene)
     */
    public void doLogout() {
        Main.getSceneManager().showLoginScene();
    }
}

