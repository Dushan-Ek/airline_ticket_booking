package airline.gui;

import db.DBConnection;
import db.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class CustomerDashboard extends JFrame {
    private JComboBox<String> fromComboBox, toComboBox, classComboBox;
    private JTable flightTable;
    private DefaultTableModel flightTableModel;
    private String username;

    public CustomerDashboard(String username) {
        this.username = username;

        setTitle("Customer Dashboard - " + username);
        setSize(950, 550);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top Panel
        JPanel topPanel = new JPanel(new FlowLayout());

        topPanel.add(new JLabel("From:"));
        fromComboBox = new JComboBox<>();
        topPanel.add(fromComboBox);

        topPanel.add(new JLabel("To:"));
        toComboBox = new JComboBox<>();
        topPanel.add(toComboBox);

        topPanel.add(new JLabel("Class:"));
        classComboBox = new JComboBox<>(new String[]{"Economy", "Business", "First"});
        topPanel.add(classComboBox);

        JButton searchBtn = new JButton("Search Flights");
        topPanel.add(searchBtn);

        JButton myBookingsBtn = new JButton("My Bookings"); // 🔁
        topPanel.add(myBookingsBtn); // 🔁

        add(topPanel, BorderLayout.NORTH);

        // Flight Table
        flightTableModel = new DefaultTableModel(new String[]{
                "Flight ID", "Flight No", "From", "To", "Departure", "Arrival", "Class", "Available Seats"
        }, 0);
        flightTable = new JTable(flightTableModel);
        add(new JScrollPane(flightTable), BorderLayout.CENTER);

        // Bottom Panel
        JButton bookBtn = new JButton("Book Selected Flight");
        add(bookBtn, BorderLayout.SOUTH);

        // Load airport names
        loadAirports();

        searchBtn.addActionListener(e -> searchFlights());
        bookBtn.addActionListener(e -> bookFlight());
        myBookingsBtn.addActionListener(e -> showMyBookings()); // 🔁

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void loadAirports() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT name FROM airports")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String airport = rs.getString("name");
                fromComboBox.addItem(airport);
                toComboBox.addItem(airport);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading airports: " + e.getMessage());
        }
    }

    private void searchFlights() {
        flightTableModel.setRowCount(0);
        String from = (String) fromComboBox.getSelectedItem();
        String to = (String) toComboBox.getSelectedItem();
        String seatClass = (String) classComboBox.getSelectedItem();

        if (from.equals(to)) {
            JOptionPane.showMessageDialog(this, "Departure and arrival airports must differ.");
            return;
        }

        String query = """
            SELECT f.id AS flight_id, f.flight_number, a1.name AS from_airport, a2.name AS to_airport,
                   f.departure_time, f.arrival_time, f.airplane_id,
                   s.available_first_class, s.available_business_class, s.available_economy_class
            FROM flights f
            JOIN airports a1 ON f.departure_airport_id = a1.id
            JOIN airports a2 ON f.arrival_airport_id = a2.id
            JOIN airplanes ap ON f.airplane_id = ap.id
            JOIN seats s ON ap.id = s.airplane_id
            WHERE a1.name = ? AND a2.name = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, from);
            stmt.setString(2, to);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int availableSeats = switch (seatClass) {
                    case "First" -> rs.getInt("available_first_class");
                    case "Business" -> rs.getInt("available_business_class");
                    default -> rs.getInt("available_economy_class");
                };

                flightTableModel.addRow(new Object[]{
                        rs.getInt("flight_id"),
                        rs.getString("flight_number"),
                        rs.getString("from_airport"),
                        rs.getString("to_airport"),
                        rs.getTimestamp("departure_time"),
                        rs.getTimestamp("arrival_time"),
                        seatClass,
                        availableSeats
                });
            }

            if (flightTableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No flights found.");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error searching flights: " + e.getMessage());
        }
    }

    private void bookFlight() {
        int row = flightTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a flight first.");
            return;
        }

        int flightId = (int) flightTableModel.getValueAt(row, 0);
        String seatClass = (String) flightTableModel.getValueAt(row, 6);
        int availableSeats = (int) flightTableModel.getValueAt(row, 7);

        if (availableSeats <= 0) {
            JOptionPane.showMessageDialog(this, "No available seats for the selected class.");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            int userId;
            try (PreparedStatement stmt = conn.prepareStatement("SELECT id FROM users WHERE username = ?")) {
                stmt.setString(1, username);
                ResultSet rs = stmt.executeQuery();
                if (!rs.next()) {
                    JOptionPane.showMessageDialog(this, "User not found.");
                    return;
                }
                userId = rs.getInt("id");
            }

            try (PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO bookings (user_id, flight_id, class) VALUES (?, ?, ?)")) {
                stmt.setInt(1, userId);
                stmt.setInt(2, flightId);
                stmt.setString(3, seatClass);
                stmt.executeUpdate();
            }

            String updateSeatsSQL = switch (seatClass) {
                case "First" -> "UPDATE seats s JOIN flights f ON s.airplane_id = f.airplane_id SET s.available_first_class = s.available_first_class - 1 WHERE f.id = ?";
                case "Business" -> "UPDATE seats s JOIN flights f ON s.airplane_id = f.airplane_id SET s.available_business_class = s.available_business_class - 1 WHERE f.id = ?";
                default -> "UPDATE seats s JOIN flights f ON s.airplane_id = f.airplane_id SET s.available_economy_class = s.available_economy_class - 1 WHERE f.id = ?";
            };

            try (PreparedStatement stmt = conn.prepareStatement(updateSeatsSQL)) {
                stmt.setInt(1, flightId);
                stmt.executeUpdate();
            }

            conn.commit();
            JOptionPane.showMessageDialog(this, "Flight booked successfully!");
            searchFlights(); // refresh the table

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Booking failed: " + e.getMessage());
        }
    }

    // 🔁 New method to view bookings
    private void showMyBookings() {
        JDialog dialog = new JDialog(this, "My Bookings", true);
        dialog.setSize(900, 400);
        dialog.setLayout(new BorderLayout());

        DefaultTableModel bookingsModel = new DefaultTableModel(new String[]{
                "Booking ID", "Flight No", "From", "To", "Departure", "Arrival", "Class"
        }, 0);
        JTable bookingsTable = new JTable(bookingsModel);
        dialog.add(new JScrollPane(bookingsTable), BorderLayout.CENTER);

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("""
                 SELECT b.id AS booking_id, f.flight_number, a1.name AS from_airport, a2.name AS to_airport,
                        f.departure_time, f.arrival_time, b.class
                 FROM bookings b
                 JOIN users u ON b.user_id = u.id
                 JOIN flights f ON b.flight_id = f.id
                 JOIN airports a1 ON f.departure_airport_id = a1.id
                 JOIN airports a2 ON f.arrival_airport_id = a2.id
                 WHERE u.username = ?
             """)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                bookingsModel.addRow(new Object[]{
                        rs.getInt("booking_id"),
                        rs.getString("flight_number"),
                        rs.getString("from_airport"),
                        rs.getString("to_airport"),
                        rs.getTimestamp("departure_time"),
                        rs.getTimestamp("arrival_time"),
                        rs.getString("class")
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load bookings: " + e.getMessage());
        }

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CustomerDashboard("john_doe"));
    }
}
