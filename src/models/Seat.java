package models;

public class Seat {

    private int id;
    private int airplaneId;
    private int totalSeats;
    private int firstClass;
    private int businessClass;
    private int economyClass;
    private int availableFirstClass;
    private int availableBusinessClass;
    private int availableEconomyClass;

    // Full constructor (with id and available seats)
    public Seat(int id, int airplaneId, int totalSeats,
            int firstClass, int businessClass, int economyClass,
            int availableFirstClass, int availableBusinessClass, int availableEconomyClass) {
        this.id = id;
        this.airplaneId = airplaneId;
        this.totalSeats = totalSeats;
        this.firstClass = firstClass;
        this.businessClass = businessClass;
        this.economyClass = economyClass;
        this.availableFirstClass = availableFirstClass;
        this.availableBusinessClass = availableBusinessClass;
        this.availableEconomyClass = availableEconomyClass;
    }

    public Seat(int airplaneId, int totalSeats, int firstClass, int businessClass, int economyClass) {
        this.airplaneId = airplaneId;
        this.totalSeats = totalSeats;
        this.firstClass = firstClass;
        this.businessClass = businessClass;
        this.economyClass = economyClass;
    }

    // Constructor without id (for creating new Seat entries)
    public Seat(int airplaneId, int totalSeats,
            int firstClass, int businessClass, int economyClass,
            int availableFirstClass, int availableBusinessClass, int availableEconomyClass) {
        this.airplaneId = airplaneId;
        this.totalSeats = totalSeats;
        this.firstClass = firstClass;
        this.businessClass = businessClass;
        this.economyClass = economyClass;
        this.availableFirstClass = availableFirstClass;
        this.availableBusinessClass = availableBusinessClass;
        this.availableEconomyClass = availableEconomyClass;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAirplaneId() {
        return airplaneId;
    }

    public void setAirplaneId(int airplaneId) {
        this.airplaneId = airplaneId;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public int getFirstClass() {
        return firstClass;
    }

    public void setFirstClass(int firstClass) {
        this.firstClass = firstClass;
    }

    public int getBusinessClass() {
        return businessClass;
    }

    public void setBusinessClass(int businessClass) {
        this.businessClass = businessClass;
    }

    public int getEconomyClass() {
        return economyClass;
    }

    public void setEconomyClass(int economyClass) {
        this.economyClass = economyClass;
    }

    public int getAvailableFirstClass() {
        return availableFirstClass;
    }

    public void setAvailableFirstClass(int availableFirstClass) {
        this.availableFirstClass = availableFirstClass;
    }

    public int getAvailableBusinessClass() {
        return availableBusinessClass;
    }

    public void setAvailableBusinessClass(int availableBusinessClass) {
        this.availableBusinessClass = availableBusinessClass;
    }

    public int getAvailableEconomyClass() {
        return availableEconomyClass;
    }

    public void setAvailableEconomyClass(int availableEconomyClass) {
        this.availableEconomyClass = availableEconomyClass;
    }

    @Override
    public String toString() {
        return "Seat{"
                + "id=" + id
                + ", airplaneId=" + airplaneId
                + ", totalSeats=" + totalSeats
                + ", firstClass=" + firstClass
                + ", businessClass=" + businessClass
                + ", economyClass=" + economyClass
                + ", availableFirstClass=" + availableFirstClass
                + ", availableBusinessClass=" + availableBusinessClass
                + ", availableEconomyClass=" + availableEconomyClass
                + '}';
    }
}
