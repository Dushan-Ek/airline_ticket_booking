package dao;

import db.DBConnection;
import models.Seat;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SeatDAO {

    public static List<Seat> getSeatsByFlight(int flightId) throws SQLException {
        List<Seat> list = new ArrayList<>();
        String sql = "SELECT * FROM seats WHERE flight_id = ?";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, flightId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            list.add(new Seat(
                    rs.getInt("id"),
                    rs.getInt("flight_id"),
                    rs.getInt("seat_class"),
                    rs.getInt("total"),
                    rs.getInt("available")
            ));
        }
        return list;
    }

    public static boolean reduceAvailableSeat(int flightId, String seatClass) throws SQLException {
        String sql = "UPDATE seats SET available = available - 1 WHERE flight_id = ? AND seat_class = ? AND available > 0";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, flightId);
        ps.setString(2, seatClass);
        return ps.executeUpdate() > 0;
    }

    public static boolean addSeat(Seat seat) throws SQLException {
        String sql = "INSERT INTO seats (airplane_id, total_seats, first_class, business_class, economy_class) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, seat.getAirplaneId());
        ps.setInt(2, seat.getTotalSeats());
        ps.setInt(3, seat.getFirstClass());
        ps.setInt(4, seat.getBusinessClass());
        ps.setInt(5, seat.getEconomyClass());
        return ps.executeUpdate() > 0;
    }

    public static boolean addSeats(int airplaneId, String seatClass, int count) {
      System.out.print("hi");
      return true;
    }

}
