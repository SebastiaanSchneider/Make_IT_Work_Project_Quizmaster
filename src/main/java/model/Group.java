package model;

/**
 * Object model for Groups
 * @author Sebastiaa Schneider
 */
public class Group {
    // variables
    private int groupId;
    private String name;
    private int capacity;
    private User teacher;
    private Course course;


    // getters/setters
    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public User getTeacher() {
        return teacher;
    }

    public void setTeacher(User teacher) {
        this.teacher = teacher;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }


    // constructors
    public Group(int groupId, String name, int capacity, User teacher, Course course) {
        setGroupId(groupId);
        setName(name);
        setCapacity(capacity);
        setTeacher(teacher);
        setCourse(course);
    }

    public Group(String name, int capacity, User teacher, Course course) {
        setName(name);
        setCapacity(capacity);
        setTeacher(teacher);
        setCourse(course);
    }


    // methods
    @Override
    public String toString() {
        return "Naam: " + name +
                "\nMax groepsgrootte: " + capacity +
                "\nDocent: " + teacher +
                "\nCursus: " + course.getCourseName();
    }
}
