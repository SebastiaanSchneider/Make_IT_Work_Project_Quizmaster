package model;

public enum Role {
    STUDENT("Student"),
    DOCENT("Docent"),
    COORDINATOR("Coördinator"),
    ADMINISTRATOR("Administrator"),
    FUNCTIONEEL_BEHEERDER("Functioneel Beheerder");

    private final String displayName;

    /**
     * Changes a string to a Role.
     *
     * @param roleValue String to change.
     * @return Role from String.
     */
    public static Role getValue(String roleValue) {
        String cleaned = roleValue.replace(" ", "_").replace("ö", "o").replace("Ö", "O").toUpperCase();
        return Role.valueOf(cleaned);
    }

    // Name for GUI.
    Role(String displayName) {
        this.displayName = displayName;
    }

    //To get displayname.
    @Override
    public String toString() {
        return displayName;
    }

}