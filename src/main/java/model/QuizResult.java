package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class QuizResult {
    private int quizId;
    private int userId;
    private String dateTimeString;
    private int score;

    // Getters and setters
    public int getQuizId() {
        return quizId;
    }

    public void setQuizId(int quizId) {
        this.quizId = quizId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public LocalDateTime getDateTime() {
        return LocalDateTime.parse(dateTimeString);
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTimeString = dateTime.toString();
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    // Constructors
    public QuizResult(int quizId, int userId, LocalDateTime dateTime, int score) {
        this.quizId = quizId;
        this.userId = userId;
        this.dateTimeString = dateTime.toString();
        this.score = score;
    }

    // toString for QuizResults
    @Override
    public String toString() {
        return "Quiz resultaat: " +
                "\nDatum: " + this.getDateTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")) +
                "\nScore: " + score;
    }
}
