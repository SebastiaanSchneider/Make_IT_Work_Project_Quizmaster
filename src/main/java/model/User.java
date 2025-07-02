package model;

public class User {
    private int userId;
    private String username;
    private String password;
    private String firstname;
    private String infix;
    private String lastname;
    private Role role;

    /**
     * Constructor zonder userId, bedoeld voor nieuwe gebruikers die nog niet in de database staan.
     */
    public User(String username, String password, String firstname,
                String infix, String lastname, Role role) {
        this.username = username;
        this.password = password;
        this.firstname = firstname;
        this.infix = infix;
        this.lastname = lastname;
        this.role = role;
    }

    /**
     * Constructor met userId, bedoeld voor gebruikers die al in de database staan.
     */
    public User(int userId, String username, String password, String firstname, String infix, String lastname, Role role) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.firstname = firstname;
        this.infix = infix;
        this.lastname = lastname;
        this.role = role;
    }

    /**
     * Geeft de volledige naam van de gebruiker terug als string, met of zonder tussenvoegsel.
     */
    @Override
    public String toString() {
        if (infix.isEmpty()) {
            return getFirstname() + " " + getLastname();
        } else {
            return getFirstname() + " " + infix + " " + getLastname();
        }
    }

    // Getter voor userId
    public int getUserId() {
        return userId;
    }

    // Getter voor achternaam
    public String getLastname() {
        return lastname;
    }

    // Getter voor tussenvoegsel
    public String getInfix() {
        return infix;
    }

    // Getter voor voornaam
    public String getFirstname() {
        return firstname;
    }

    // Getter voor wachtwoord
    public String getPassword() {
        return password;
    }

    // Getter voor gebruikersnaam
    public String getUsername() {
        return username;
    }

    // Getter voor rol
    public Role getRole() {
        return role;
    }

    // Setter voor rol
    public void setRole(Role role) {
        this.role = role;
    }

    /**
     * Geeft de volledige naam van de gebruiker terug, met of zonder tussenvoegsel.
     */
    public String getFullName() {
        if (infix.isEmpty()) {
            return getFirstname() + " " + getLastname();
        } else {
            return getFirstname() + " " + infix + " " + getLastname();
        }
    }
}
