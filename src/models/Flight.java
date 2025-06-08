package models;

import java.sql.Timestamp;

public class Flight {

    private int id;
    private int airplaneId;
    private int departureAirportId;
    private int arrivalAirportId;
    private Timestamp departureTime;
    private Timestamp arrivalTime;
    private String flightNumber;
    private String status;

    private String departureAirportName;  // Optional: for display
    private String arrivalAirportName;

    // Constructor for inserting a new flight
    public Flight(int airplaneId, int departureAirportId, int arrivalAirportId,
            Timestamp departureTime, Timestamp arrivalTime,
            String flightNumber, String status) {
        this.airplaneId = airplaneId;
        this.departureAirportId = departureAirportId;
        this.arrivalAirportId = arrivalAirportId;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.flightNumber = flightNumber;
        this.status = status;
    }

    // Constructor for retrieving full flight details from DB
    public Flight(int id, int airplaneId, int departureAirportId, int arrivalAirportId,
            Timestamp departureTime, Timestamp arrivalTime,
            String flightNumber, String status,
            String departureAirportName, String arrivalAirportName) {
        this.id = id;
        this.airplaneId = airplaneId;
        this.departureAirportId = departureAirportId;
        this.arrivalAirportId = arrivalAirportId;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.flightNumber = flightNumber;
        this.status = status;
        this.departureAirportName = departureAirportName;
        this.arrivalAirportName = arrivalAirportName;
    }

    public Flight(int id, String flightNumber, int airplaneId, int departureAirportId, int arrivalAirportId,
            Timestamp departureTime, Timestamp arrivalTime, String status) {
        this.id = id;
        this.flightNumber = flightNumber;
        this.airplaneId = airplaneId;
        this.departureAirportId = departureAirportId;
        this.arrivalAirportId = arrivalAirportId;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.status = status;
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getAirplaneId() {
        return airplaneId;
    }

    public int getDepartureAirportId() {
        return departureAirportId;
    }

    public int getArrivalAirportId() {
        return arrivalAirportId;
    }

    public Timestamp getDepartureTime() {
        return departureTime;
    }

    public Timestamp getArrivalTime() {
        return arrivalTime;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public String getStatus() {
        return status;
    }

    public String getDepartureAirportName() {
        return departureAirportName;
    }

    public String getArrivalAirportName() {
        return arrivalAirportName;
    }

    @Override
    public String toString() {
        return flightNumber + ": " + departureAirportName + " → " + arrivalAirportName;
    }
}
