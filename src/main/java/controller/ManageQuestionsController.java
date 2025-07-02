package controller;

import database.mysql.CourseDAO;
import database.mysql.QuestionDAO;
import database.mysql.QuizDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import model.*;
import view.Main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class ManageQuestionsController {

    @FXML
    private ComboBox<Quiz> quizComboBox;

    @FXML
    private ListView<Question> questionList;

    @FXML
    private Label quizInfoLabel;

    @FXML
    private Label nameRoleLabel;


    private QuestionDAO questionDao = new QuestionDAO(Main.getDBaccess());

    CourseDAO courseDAO = new CourseDAO(Main.getDBaccess());


    private QuizDAO quizDao = new QuizDAO(Main.getDBaccess());

    private List<Question> allQuestions = new ArrayList<>();

    private User user = WelcomeController.getUser();


    /**
     * Sets up all the necessary listeners and UI configurations
     * for the ComboBox (quizComboBox) and ListView (questionList).
     */
    private void setupListeners() {
        //for more info by using combobox.cellfactory go to: https://docs.oracle.com/javafx/2/api/javafx/scene/control/ComboBox.html
        // Customize how each item (Quiz) in the ComboBox dropdown is displayed
        nameRoleLabel.setText(user.getFullName() + "\nRol: " + user.getRole().toString());
        quizComboBox.setCellFactory(listView -> new ListCell<>() {

            protected void updateItem(Quiz quiz, boolean empty) {
                super.updateItem(quiz, empty);
                if (empty || quiz == null) {
                    setText("<leeg>");
                } else {
                    int aantal = questionDao.countNumberOfQuestions(quiz);
                    setText(quiz.getQuizName() + " (" + aantal + " vragen)");                }
            }
        });

        // The button cell is used to render what is shown in the ComboBox 'button' area.
        // Customize the selected item (shown on the button of the ComboBox)
        quizComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Quiz quiz, boolean empty) {
                super.updateItem(quiz, empty);
                if (empty || quiz == null) {
                    setText("<leeg2>");
                } else {
                    setText(quiz.getQuizName());
                }
            }
        });


        // Add a listener that reacts when the user selects a different quiz
        // It loads and displays the questions related to the selected quiz.
        quizComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldQuiz, newQuiz) -> {
            if (newQuiz != null) {
                updateQuestionList(newQuiz);

                int aantalVragen = questionDao.countNumberOfQuestions(newQuiz);
                quizInfoLabel.setText("Aantal Vragen " + aantalVragen);
            }
        });

        // Customize how each question is displayed in the ListView (questionList)
        // Enables line wrapping and prevents cutoff of long question texts.
        questionList.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Question question, boolean empty) {
                super.updateItem(question, empty);
                setWrapText(true); // Enable line wrapping for long text
                setPrefWidth(0); // Adjust width to avoid cutoff
                setText((empty || question == null) ? null : question.getQuestionText());
            }
        });

    }

    private void updateQuestionList(Quiz newQuiz) {
        questionList.getItems().clear();
        //// Loop through all the questions, Check if the question belongs to the selected quiz,
        //// Add only the questions that belong to the selected quiz
       for (Question question : allQuestions) {
            if (question.getQuiz().equals(newQuiz)) {
                questionList.getItems().add(question);
            }
        }

        //Second solution
//        List<Question> questions = questionDao.getQuestionsByQuizName(newQuiz.getQuizName());
//        questionList.getItems().addAll(questions);

    }

    public void setup() {
        quizComboBox.setPromptText("Kies een quiz...");
        quizInfoLabel.setText("Aantal Vragen: ");
        setupListeners();
        refresh();
    }

    public void refresh() {
        quizComboBox.getItems().clear();
        questionList.getItems().clear();
        quizComboBox.getItems().addAll(quizDao.getAll());
        allQuestions = questionDao.getAll();
        questionList.getItems().addAll(allQuestions);
    }


    public void doMenu() {
        Main.getSceneManager().showWelcomeScene(WelcomeController.getUser());
    }

    /**
     * Handles the creation of a new question.
     * Opens the create/update question screen with an empty question linked to the selected quiz.
     */
    @FXML
    public void doCreateQuestion() {
        Quiz selectedQuiz = quizComboBox.getSelectionModel().getSelectedItem();

        if (selectedQuiz == null) {
            showError("Selecteer een quiz om een vraag aan toe te voegen").showAndWait();
        } else {
            // Navigate to question creation screen with a new empty question
            Main.getSceneManager().showCreateUpdateQuestionScene(new Question("", "", "", "", "", selectedQuiz));
            CreateUpdateQuestionController.previousView = "Manager";
        }
    }


    /**
     * Handles the update of an existing question.
     * Opens the create/update question screen pre-filled with the selected question.
     */
    @FXML
    public void doUpdateQuestion() {
        Question question = questionList.getSelectionModel().getSelectedItem();
        if (question == null){
            showError("Selecteer een vraag om te wijzigen").showAndWait();
        }else {
            // Navigate to question update screen with selected question
            Main.getSceneManager().showCreateUpdateQuestionScene(question);
            CreateUpdateQuestionController.previousView = "Manager";
        }
    }

    /**
     * Utility method to show an error popup with the given message.
     */
    public Alert showError(String errorMessage){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Foutmelding");
        alert.setHeaderText(errorMessage);
        return alert;
    }

    /**
     * Deletes the selected question from the database and the view.
     */
    @FXML
    public void doDeleteQuestion() {
        // Get the selected question from the ListView
        Question selectedQuestion = questionList.getSelectionModel().getSelectedItem();

        // Check if a question is actually selected
        if (selectedQuestion != null) {
            // Delete the selected question from the database
            questionDao.deleteOne(selectedQuestion);
            questionList.getItems().remove(selectedQuestion);
            // remove it from allQuestions list if it exists
            allQuestions.remove(selectedQuestion);
            System.out.println("Question deleted successfully.");
        } else {
            showError("Selecteer een vraag om te verwijderen").showAndWait();
        }
    }


    /**
     * Exports all questions organized by course and quiz to a given file.
     */
    public void exportToFile(File file) throws FileNotFoundException {
        try (PrintWriter printWriter = new PrintWriter(file)) {
            User coordinator = WelcomeController.getUser();

            for (Course course : courseDAO.getCoursesByUsername(coordinator)) {
                printWriter.println("CURSUS: " + course.getCourseName());
                printWriter.println();

                List<Quiz> quizzes = quizDao.getAllByCoordinator(coordinator);
                for (Quiz quiz : quizzes) {
                    printWriter.println("    QUIZ: " + quiz.getQuizName());
                    printWriter.println();

                    List<Question> questions = questionDao.getQuestionsByQuizName(quiz.getQuizName());
                    for (Question question : questions) {
                        printWriter.println("        VRAAG: " + question.getQuestionText());
                        printWriter.println("        1) Correct Answer: " + question.getCorrectAnswer());
                        printWriter.println("        2) Incorrect Answer: " + question.getIncorrectAnswer1());
                        printWriter.println("        3) Incorrect Answer: " + question.getIncorrectAnswer2());
                        printWriter.println("        4) Incorrect Answer: " + question.getIncorrectAnswer3());
                        printWriter.println();
                    }
                    printWriter.println();
                }
                printWriter.println();
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Bestand niet gevonden: " + e.getMessage());
        }
    }


    // locatie kunnen kiezen voor exportbestand
    public void exportWithFileChooser() throws FileNotFoundException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exporteer vragen");
        fileChooser.setInitialFileName("Vragen.txt");
        Window window = questionList.getScene().getWindow();
        File file = fileChooser.showSaveDialog(window);
        if (file != null){
            exportToFile(file);
        }
    }
}
