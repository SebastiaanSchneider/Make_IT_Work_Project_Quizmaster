package model;

public class StudentGroup {
    private int userId;
    private int groupId;

    // Constructor
    public StudentGroup(int userId, int groupId) {
        this.userId = userId;
        this.groupId = groupId;
    }

    // Getters and setters
    public int getGroupId() {
        return groupId;
    }

    public int getUserId() {
        return userId;
    }
}
