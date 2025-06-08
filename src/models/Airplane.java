package models;
public class Airplane {

    private int id;
    private String code;
    private String type; // e.g., "Small", "Medium", "Large"
    private int currentAirportId;
    private boolean available;

    // Constructor without ID (ID can be set later, e.g., by database)
    public Airplane(String code, String type, int currentAirportId, boolean available) {
        this.code = code;
        this.type = type;
        this.currentAirportId = currentAirportId;
        this.available = available;
    }

    // Constructor with ID
    public Airplane(int id, String code, String type, int currentAirportId, boolean available) {
        this.id = id;
        this.code = code;
        this.type = type;
        this.currentAirportId = currentAirportId;
        this.available = available;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getType() {
        return type;
    }

    public int getCurrentAirportId() {
        return currentAirportId;
    }

    public boolean isAvailable() {
        return available;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setCurrentAirportId(int currentAirportId) {
        this.currentAirportId = currentAirportId;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    // Optional: toString() method for debugging
    @Override
    public String toString() {
        return "Airplane{"
                + "id=" + id
                + ", code='" + code + '\''
                + ", type='" + type + '\''
                + ", currentAirportId=" + currentAirportId
                + ", available=" + available
                + '}';
    }
}
