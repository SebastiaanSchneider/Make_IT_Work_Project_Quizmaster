package model;

public class Course {
    private int courseId;
    private Level courseLevel;
    private String courseName;
    private User coordinator;

    // Getters and setters
    public User getCoordinator() {
        return coordinator;
    }

    public void setCoordinator(User coordinator) {
        this.coordinator = coordinator;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setcourseId(int courseId) {
        this.courseId = courseId;
    }

    public Level getCourseLevel() {
        return courseLevel;
    }

    public void setCourseLevel(String courseLevel) {
        this.courseLevel = Level.valueOf(courseLevel);
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    // Constructors
    public Course(Level courseLevel, String courseName, User coordinator) {
        setCourseLevel(String.valueOf(courseLevel));
        setCourseName(courseName);
        setCoordinator(coordinator);
    }

    public Course(int courseId, Level courseLevel, String courseName,
                  User coordinator) {
        setcourseId(courseId);
        setCourseLevel(String.valueOf(courseLevel));
        setCourseName(courseName);
        setCoordinator(coordinator);
    }

    // toString for Course
    @Override
    public String toString() {
        return "Cursus: " + courseName +
                "\nNiveau: " + courseLevel +
                "\nCoordinator: " + coordinator;
    }

    /**
     * To check if two objects are the same, based on id.
     *
     * @param object to check
     * @return if objects are the same
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) return true; //same object?
        if (!(object instanceof Course)) return false; // is it a course?
        Course course = (Course) object; // cast to course
        return courseId == course.courseId; // compare primary key
    }

    /**
     * Makes a hashcode from courseId to search faster.
     *
     * @return hashcode
     */
    @Override
    public int hashCode() {
        return Integer.hashCode(courseId);
    }
}
