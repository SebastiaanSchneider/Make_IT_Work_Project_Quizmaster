package controller;

import database.mysql.CourseDAO;
import database.mysql.QuestionDAO;
import database.mysql.QuizDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Course;
import model.Question;
import model.Quiz;
import model.User;
import view.Main;

import java.util.List;

public class CoordinatorDashboardController {
    private final QuizDAO quizDAO;
    private final CourseDAO courseDAO;
    private final QuestionDAO questionDAO;

    @FXML
    private ListView<Course> courseList;
    @FXML
    private ListView<Quiz> quizList;
    @FXML
    private ListView<Question> questionList;
    @FXML
    private Label nameRoleLabel;

    public CoordinatorDashboardController() {
        this.quizDAO = new QuizDAO(Main.getDBaccess());
        this.courseDAO = new CourseDAO(Main.getDBaccess());
        this.questionDAO = new QuestionDAO(Main.getDBaccess());
    }


    public void setup() {
        // Retrieve currently logged in user
        User coordinator = WelcomeController.getUser();
        nameRoleLabel.setText(coordinator.getFullName() + "\nRol: " + coordinator.getRole().toString());

        // Show a list of all courses in the database, coordinated by the currently logged in user
        List<Course> courseListFromDB = courseDAO.getCoursesByUsername(coordinator);
        ObservableList<Course> courses = FXCollections.observableArrayList(courseListFromDB);
        courseList.setItems(courses);

        // Show a list of all quizzes belonging to the selected course
        courseList.getSelectionModel().selectedItemProperty().
                addListener((obs, oldSelection, newSelection) -> {
            String courseName = newSelection.getCourseName();
            List<Quiz> quizListFromDB = quizDAO.getQuizzesByCourseName(courseName);
            quizList.setItems(FXCollections.observableArrayList(quizListFromDB));
        });

        // Show a list of all questions belonging to the selected quiz
        quizList.getSelectionModel().selectedItemProperty().
                addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                String quizName = newSelection.getQuizName();
                List<Question> questionListFromDB = questionDAO.getQuestionsByQuizName(quizName);
                questionList.setItems(FXCollections.observableArrayList(questionListFromDB));
            } else {
                questionList.setItems(FXCollections.observableArrayList());
            }
        });

        // Custom string format for showing the course in the courseListView
        courseList.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Course course, boolean empty) {
                super.updateItem(course, empty);
                if (empty || course == null) {
                    setText(null);
                } else {
                    setText(course.getCourseName() + "\nNiveau: " + course.getCourseLevel());
                }
            }
        });

        // Custom string format for showing the quizzes in the quizListView
        quizList.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Quiz quiz, boolean empty) {
                super.updateItem(quiz, empty);
                if (empty || quiz == null) {
                    setText(null);
                } else {
                    setText(quiz.getQuizName() + "\nNiveau: " + quiz.getQuizLevel());
                }
            }
        });

        // Custom string format for showing the questions in the questionListView
        questionList.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Question question, boolean empty) {
                super.updateItem(question, empty);
                if (empty || question == null) {
                    setText(null);
                } else {
                    setText(question.getQuestionText());
                }
            }
        });
    } // setup


    public void doCreateQuiz() {
        Course selectedCourse = courseList.getSelectionModel().getSelectedItem();
        if (selectedCourse == null) {
            showCustomError("Selecteer eerst een cursus om een quiz aan te maken").showAndWait();
        } else {
            Main.getSceneManager().showCreateUpdateQuizScene(null);
            CreateUpdateQuizController.previousView = "Dashboard";
        }
    }


    public void doUpdateQuiz() {
        Quiz quiz = quizList.getSelectionModel().getSelectedItem();
        // if no quiz is selected when the user presses the "wijzigen" button, an error message is shown
        if (quiz == null) {
            showCustomError("Selecteer een quiz om te wijzigen").showAndWait();
        } else {
            Main.getSceneManager().showCreateUpdateQuizScene(quiz);
            CreateUpdateQuizController.previousView = "Dashboard";
        }
    }


    public void doCreateQuestion() {
        Quiz selectedQuiz = quizList.getSelectionModel().getSelectedItem();
        if (selectedQuiz == null) {
            showCustomError("Selecteer eerst een quiz om een vraag aan te maken").showAndWait();
        } else {
            Main.getSceneManager().showCreateUpdateQuestionScene
                    (new Question("", "", "", "", "", selectedQuiz));
            CreateUpdateQuestionController.previousView = "Dashboard";
        }
    }


    public void doUpdateQuestion() {
        Question question = questionList.getSelectionModel().getSelectedItem();
        // if no question is selected when the user presses the "wijzigen" button, an error message is shown
        if (question == null) {
            showCustomError("Selecteer een vraag om te wijzigen").showAndWait();
        } else {
            Main.getSceneManager().showCreateUpdateQuestionScene(question);
            CreateUpdateQuestionController.previousView = "Dashboard";
        }
    }

    public void doMenu() {
        Main.getSceneManager().showWelcomeScene(WelcomeController.getUser());
    }


    /**
     * Returns a custom error message based on the string passed as an argument
     */
    public Alert showCustomError(String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Foutmelding");
        alert.setHeaderText(errorMessage);
        return alert;
    }
}