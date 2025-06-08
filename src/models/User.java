package models;

public class User {

    private int id;
    private String username;
    private String role;
    private boolean active;

    // Constructor
    public User(int id, String username, String role, boolean active) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.active = active;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isStatus() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "User{"
                + "id=" + id
                + ", username='" + username + '\''
                + ", role='" + role + '\''
                + ", active=" + active
                + '}';
    }
}
