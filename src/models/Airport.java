package models;

public class Airport {
    private int id;
    private String name;
    private String location;

    public Airport(int id, String name, String location) {
        this.id = id;
        this.name = name;
        this.location = location;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return name + " (" + location + ")"; // Format to show name and location in ComboBox
    }
}
