package models;

import java.sql.Timestamp;

public class Booking {
    private int id;
    private int userId;
    private int flightId;
    private String seatClass;
    private String seatNumber;
    private Timestamp bookingTime;

    public Booking(int id, int userId, int flightId, String seatClass, String seatNumber, Timestamp bookingTime) {
        this.id = id;
        this.userId = userId;
        this.flightId = flightId;
        this.seatClass = seatClass;
        this.seatNumber = seatNumber;
        this.bookingTime = bookingTime;
    }

    public Booking(int userId, int flightId, String seatClass, String seatNumber, Timestamp bookingTime) {
        this.userId = userId;
        this.flightId = flightId;
        this.seatClass = seatClass;
        this.seatNumber = seatNumber;
        this.bookingTime = bookingTime;
    }

    // Getters
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public int getFlightId() { return flightId; }
    public String getSeatClass() { return seatClass; }
    public String getSeatNumber() { return seatNumber; }
    public Timestamp getBookingTime() { return bookingTime; }
}
