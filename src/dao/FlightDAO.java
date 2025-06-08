package dao;

import db.DBConnection;
import models.Flight;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;

public class FlightDAO {

    public static boolean addFlight(Flight flight) throws SQLException {
        // Check for overlapping flight
        if (isAirplaneScheduled(flight.getAirplaneId(), flight.getDepartureTime(), flight.getArrivalTime())) {
            JOptionPane.showMessageDialog(null, "Scheduling conflict: airplane already assigned to another flight.");
            System.out.println("Scheduling conflict: airplane already assigned to another flight.");
            return false;
        }

        // ✅ Check airplane current location matches departure airport
        if (!isAirplaneAtCurrentLocation(flight.getAirplaneId(), flight.getDepartureAirportId())) {
            JOptionPane.showMessageDialog(null, "Airplane is not currently at the selected departure airport.");
            System.out.println("Airplane is not currently at the selected departure airport.");
            return false;
        }

        String sql = "INSERT INTO flights (airplane_id, departure_airport_id, arrival_airport_id, departure_time, arrival_time, flight_number, status) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        Connection conn = DBConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, flight.getAirplaneId());
            ps.setInt(2, flight.getDepartureAirportId());
            ps.setInt(3, flight.getArrivalAirportId());
            ps.setTimestamp(4, flight.getDepartureTime());
            ps.setTimestamp(5, flight.getArrivalTime());
            ps.setString(6, flight.getFlightNumber());
            ps.setString(7, flight.getStatus());

            int result = ps.executeUpdate();

            // ✅ Update airplane's current location to new arrival airport
            if (result > 0) {
                updateAirplaneLocation(flight.getAirplaneId(), flight.getArrivalAirportId());
                return true;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error adding flight: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

//    public static boolean addFlight(Flight flight) throws SQLException {
//        String sql = "INSERT INTO flights (airplane_id, departure_airport_id, arrival_airport_id, departure_time, arrival_time) VALUES (?, ?, ?, ?, ?)";
//        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
//        ps.setInt(1, flight.getAirplaneId());
//        ps.setInt(2, flight.getDepartureAirportId());
//        ps.setInt(3, flight.getArrivalAirportId());
//        ps.setTimestamp(4, flight.getDepartureTime());
//        ps.setTimestamp(5, flight.getArrivalTime());
//        return ps.executeUpdate() > 0;
//    }
//    public static boolean addFlight(Flight flight) throws SQLException {
//        String sql = "INSERT INTO flights (airplane_id, departure_airport_id, arrival_airport_id, departure_time, arrival_time, flight_number, status) "
//                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
//
//        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
//        ps.setInt(1, flight.getAirplaneId());
//        ps.setInt(2, flight.getDepartureAirportId());
//        ps.setInt(3, flight.getArrivalAirportId());
//        ps.setTimestamp(4, flight.getDepartureTime());
//        ps.setTimestamp(5, flight.getArrivalTime());
//        ps.setString(6, flight.getFlightNumber());
//        ps.setString(7, flight.getStatus());
//
//        return ps.executeUpdate() > 0;
//    }
    public static boolean isAirplaneAvailable(int airplaneId, Timestamp departure, Timestamp arrival) throws SQLException {
        String sql = "SELECT COUNT(*) > 0 AS is_conflict FROM flights WHERE airplane_id = ? AND ("
                + "(departure_time BETWEEN ? AND ?) OR "
                + "(arrival_time BETWEEN ? AND ?) OR "
                + "(? BETWEEN departure_time AND arrival_time) OR "
                + "(? BETWEEN departure_time AND arrival_time))";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, airplaneId);
            ps.setTimestamp(2, departure);
            ps.setTimestamp(3, arrival);
            ps.setTimestamp(4, departure);
            ps.setTimestamp(5, arrival);
            ps.setTimestamp(6, departure);
            ps.setTimestamp(7, arrival);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean("is_conflict");
                }
                return false;
            }
        }
    }

//    public static Map<String, Integer> getFlightsByAirplaneId(int airplaneId) throws SQLException {
//        String sql = "SELECT flight_id FROM flights WHERE airplane_id = ? ORDER BY flight_id";
//        Map<String, Integer> result = new LinkedHashMap<>();
//        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
//            ps.setInt(1, airplaneId);
//            try (ResultSet rs = ps.executeQuery()) {
//                while (rs.next()) {
//                    int id = rs.getInt("flight_id");
//                    result.put(String.valueOf(id), id);
//                }
//            }
//        }
//        return result;
//    }
    public static boolean updateFlight(Flight flight) throws SQLException {
        String sql = """
            UPDATE flights
            SET airplane_id = ?, from_airport_id = ?, to_airport_id = ?, departure_time = ?, arrival = ?
            WHERE flight_id = ?
            """;
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, flight.getAirplaneId());
            ps.setInt(2, flight.getDepartureAirportId());
            ps.setInt(3, flight.getArrivalAirportId());
//            ps.setTimestamp(4, flight.getArrivalAirportId());
//            ps.setTimestamp(5, flight.getArrival());
//            ps.setInt(6, flight.getFlightId());

            return ps.executeUpdate() == 1;
        }
    }

//    public static boolean updateFlight(Flight flight) throws SQLException {
//        String sql = "UPDATE flights SET airplane_id=?, departure_airport_id=?, arrival_airport_id=?, departure_time=?, arrival_time=? WHERE id=?";
//        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
//        ps.setInt(1, flight.getAirplaneId());
//        ps.setInt(2, flight.getDepartureAirportId());
//        ps.setInt(3, flight.getArrivalAirportId());
//        ps.setTimestamp(4, flight.getDepartureTime());
//        ps.setTimestamp(5, flight.getArrivalTime());
//        ps.setInt(6, flight.getId());
//        return ps.executeUpdate() > 0;
//    }
    public static boolean deleteFlight(int flightId) throws SQLException {
        String sql = "DELETE FROM flights WHERE id = ?";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, flightId);
        return ps.executeUpdate() > 0;
    }

//    public static List<Flight> getAllFlights() {
//        List<Flight> flights = new ArrayList<>();
//
//        String sql = "SELECT f.id, f.airplane_id, f.departure_airport_id, f.arrival_airport_id, f.departure_time, f.arrival_time, "
//                + "dep.name AS departure_name, arr.name AS arrival_name "
//                + "FROM flights f "
//                + "JOIN airports dep ON f.departure_airport_id = dep.id "
//                + "JOIN airports arr ON f.arrival_airport_id = arr.id "
//                + "ORDER BY f.departure_time";
//
//        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
//
//            while (rs.next()) {
//                Flight flight = new Flight(
//                        rs.getInt("id"),
//                        rs.getInt("airplane_id"),
//                        rs.getInt("departure_airport_id"),
//                        rs.getInt("arrival_airport_id"),
//                        rs.getTimestamp("departure_time"),
//                        rs.getTimestamp("arrival_time"),
//                        rs.getString("departure_name"),
//                        rs.getString("arrival_name")
//                );
//                flights.add(flight);
//            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        return flights;
//    }
    public static List<Flight> getAllFlights() {
        List<Flight> flights = new ArrayList<>();

        String sql = "SELECT f.id, f.airplane_id, f.departure_airport_id, f.arrival_airport_id, "
                + "f.departure_time, f.arrival_time, f.flight_number, f.status, "
                + "dep.name AS departure_name, arr.name AS arrival_name "
                + "FROM flights f "
                + "JOIN airports dep ON f.departure_airport_id = dep.id "
                + "JOIN airports arr ON f.arrival_airport_id = arr.id "
                + "ORDER BY f.departure_time";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Flight flight = new Flight(
                        rs.getInt("id"),
                        rs.getInt("airplane_id"),
                        rs.getInt("departure_airport_id"),
                        rs.getInt("arrival_airport_id"),
                        rs.getTimestamp("departure_time"),
                        rs.getTimestamp("arrival_time"),
                        rs.getString("flight_number"),
                        rs.getString("status"),
                        rs.getString("departure_name"),
                        rs.getString("arrival_name")
                );
                flights.add(flight);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return flights;
    }

    public static List<Object[]> getAllFlightsForTable() throws SQLException {
        List<Object[]> flights = new ArrayList<>();

        String sql = "SELECT f.id, a.code as airplane_code, "
                + "ap_from.name as from_airport_name, ap_to.name as to_airport_name, "
                + "f.departure_time, f.arrival_time "
                + "FROM flights f "
                + "JOIN airplanes a ON f.airplane_id = a.id "
                + "JOIN airports ap_from ON f.departure_airport_id = ap_from.id "
                + "JOIN airports ap_to ON f.arrival_airport_id = ap_to.id "
                + "ORDER BY f.departure_time";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Object[] row = new Object[7];
                row[0] = rs.getInt("id");                            // flight id
                row[1] = rs.getString("airplane_code");             // airplane code
                row[2] = rs.getString("from_airport_name");         // from airport
                row[3] = rs.getString("to_airport_name");           // to airport
                row[4] = rs.getTimestamp("departure_time").toString();
                row[5] = rs.getTimestamp("arrival_time").toString();
                row[6] = "Delete";                                   // action column label

                flights.add(row);
            }
        }
        return flights;
    }

    public static boolean isAirplaneAtCurrentLocation(int airplaneId, int departureAirportId) throws SQLException {
        String sql = "SELECT current_airport_id FROM airplanes WHERE id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, airplaneId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("current_airport_id") == departureAirportId;
                }
            }
        }
        return false;
    }

    public static void updateAirplaneLocation(int airplaneId, int newAirportId) throws SQLException {
        String sql = "UPDATE airplanes SET current_airport_id = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newAirportId);
            ps.setInt(2, airplaneId);
            ps.executeUpdate();
        }
    }

    public static boolean isAirplaneScheduled(int airplaneId, Timestamp departure, Timestamp arrival) throws SQLException {
        String sql = "SELECT COUNT(*) > 0 AS conflict FROM flights WHERE airplane_id = ? AND ("
                + "(departure_time < ? AND arrival_time > ?) OR "
                + "(departure_time BETWEEN ? AND ?) OR "
                + "(arrival_time BETWEEN ? AND ?))";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, airplaneId);
            ps.setTimestamp(2, arrival);
            ps.setTimestamp(3, departure);
            ps.setTimestamp(4, departure);
            ps.setTimestamp(5, arrival);
            ps.setTimestamp(6, departure);
            ps.setTimestamp(7, arrival);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getBoolean("conflict");
            }
        }
    }

}

//    public static Flight getFlightById(int id) throws SQLException {
//        String sql = "SELECT * FROM flights WHERE id = ?";
//        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
//        ps.setInt(1, id);
//        ResultSet rs = ps.executeQuery();
//        if (rs.next()) {
//            return new Flight(
//                    rs.getInt("id"),
//                    rs.getInt("airplane_id"),
//                    rs.getInt("departure_airport_id"),
//                    rs.getInt("arrival_airport_id"),
//                    rs.getTimestamp("departure_time"),
//                    rs.getTimestamp("arrival_time")
//            );
//        }
//        return null;
//    }
//}
