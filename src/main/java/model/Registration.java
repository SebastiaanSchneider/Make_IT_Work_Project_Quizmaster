package model;

public class Registration {
    private User student;
    private Course course;

    // Getters and setters
    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
        this.student = student;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    // Constructor
    public Registration(User student, Course course) {
        this.student = student;
        this.course = course;
    }
}
