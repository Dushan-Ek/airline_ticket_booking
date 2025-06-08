package dao;

import db.DBConnection;
import models.Booking;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {

    public static boolean addBooking(Booking booking) throws SQLException {
        String sql = "INSERT INTO bookings (user_id, flight_id, class, seat_number, booking_time) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, booking.getUserId());
        ps.setInt(2, booking.getFlightId());
        ps.setString(3, booking.getSeatClass());
        ps.setString(4, booking.getSeatNumber());
        ps.setTimestamp(5, booking.getBookingTime());
        return ps.executeUpdate() > 0;
    }

    public static List<Booking> getBookingsByUser(int userId) throws SQLException {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE user_id = ?";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            list.add(new Booking(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getInt("flight_id"),
                    rs.getString("class"),
                    rs.getString("seat_number"),
                    rs.getTimestamp("booking_time")
            ));
        }
        return list;
    }

     public static boolean bookFlight(int userId, int flightId, String seatClass) {
        String checkQuery = """
        SELECT s.id, s.available_first_class, s.available_business_class, s.available_economy_class
        FROM seats s
        JOIN airplanes a ON s.airplane_id = a.id
        JOIN flights f ON f.airplane_id = a.id
        WHERE f.id = ?
        """;
        Connection conn = null;
        PreparedStatement checkStmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);  // Start transaction

            checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setInt(1, flightId);
            rs = checkStmt.executeQuery();
                System.out.println(rs);

            if (rs.next()) {
                boolean hasSeat = switch (seatClass.toLowerCase()) {
                    case "first" ->
                        rs.getInt("available_first_class") > 0;
                    case "business" ->
                        rs.getInt("available_business_class") > 0;
                    case "economy" ->
                        rs.getInt("available_economy_class") > 0;
                    default ->
                        false;
                };


                if (!hasSeat) {
                                    System.out.println("here me");
                    return false; // No seat available
                }

                // Book flight (insert only, triggers handle availability update)
                String insertBooking = "INSERT INTO bookings (user_id, flight_id, class) VALUES (?, ?, ?)";
                try (PreparedStatement bookStmt = conn.prepareStatement(insertBooking)) {
                    bookStmt.setInt(1, userId);
                    bookStmt.setInt(2, flightId);
                    bookStmt.setString(3, seatClass);
                    bookStmt.executeUpdate();
                }

                conn.commit();
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ignored) {
            }
            try {
                if (checkStmt != null) {
                    checkStmt.close();
                }
            } catch (SQLException ignored) {
            }
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                }
                conn.close();
            } catch (SQLException ignored) {
            }
        }

        return false;
    }

    public static Booking getBookingById(int id) throws SQLException {
        String sql = "SELECT * FROM bookings WHERE id = ?";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return new Booking(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getInt("flight_id"),
                    rs.getString("class"),
                    rs.getString("seat_number"),
                    rs.getTimestamp("booking_time")
            );
        }
        return null;
    }
}
