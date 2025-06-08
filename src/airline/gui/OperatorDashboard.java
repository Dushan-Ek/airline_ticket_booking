package airline.gui;

import db.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class OperatorDashboard extends JFrame {
    private JTable flightTable;
    private JComboBox<String> fromComboBox, toComboBox, customerComboBox;

    public OperatorDashboard(String operatorUsername) {
        setTitle("Operator Dashboard - " + operatorUsername);
        setSize(800, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout());

        topPanel.add(new JLabel("Customer:"));
        customerComboBox = new JComboBox<>();
        topPanel.add(customerComboBox);

        topPanel.add(new JLabel("From:"));
        fromComboBox = new JComboBox<>();
        topPanel.add(fromComboBox);

        topPanel.add(new JLabel("To:"));
        toComboBox = new JComboBox<>();
        topPanel.add(toComboBox);

        JButton searchButton = new JButton("Search Flights");
        topPanel.add(searchButton);

        JButton reportButton = new JButton("Generate Report");
        topPanel.add(reportButton);

        add(topPanel, BorderLayout.NORTH);

        flightTable = new JTable();
        add(new JScrollPane(flightTable), BorderLayout.CENTER);

        JButton bookButton = new JButton("Book Flight");
        add(bookButton, BorderLayout.SOUTH);

        searchButton.addActionListener(e -> searchFlights());
        bookButton.addActionListener(e -> bookFlight());
        reportButton.addActionListener(e -> generateReport());

        loadCustomers();
        loadAirports();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void loadCustomers() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT username FROM users WHERE role = 'Customer'");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                customerComboBox.addItem(rs.getString("username"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load customers: " + e.getMessage());
        }
    }

    private void loadAirports() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT name FROM airports ORDER BY name ASC");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String airport = rs.getString("name");
                fromComboBox.addItem(airport);
                toComboBox.addItem(airport);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load airports: " + e.getMessage());
        }
    }

    private void searchFlights() {
        String from = (String) fromComboBox.getSelectedItem();
        String to = (String) toComboBox.getSelectedItem();

        if (from == null || to == null || from.equals(to)) {
            JOptionPane.showMessageDialog(this, "Please select valid From and To airports.");
            return;
        }

        String query = "SELECT f.id, f.flight_number, da.name AS from_airport, aa.name AS to_airport, " +
                "f.departure_time, f.arrival_time, s.available_first_class, s.available_business_class, s.available_economy_class " +
                "FROM flights f " +
                "JOIN airports da ON f.departure_airport_id = da.id " +
                "JOIN airports aa ON f.arrival_airport_id = aa.id " +
                "JOIN airplanes a ON f.airplane_id = a.id " +
                "JOIN seats s ON s.airplane_id = a.id " +
                "WHERE da.name = ? AND aa.name = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, from);
            stmt.setString(2, to);

            ResultSet rs = stmt.executeQuery();

            Vector<String> columns = new Vector<>();
            columns.add("Flight ID");
            columns.add("Flight No");
            columns.add("From");
            columns.add("To");
            columns.add("Departure");
            columns.add("Arrival");
            columns.add("First Class");
            columns.add("Business Class");
            columns.add("Economy Class");

            Vector<Vector<Object>> data = new Vector<>();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id"));
                row.add(rs.getString("flight_number"));
                row.add(rs.getString("from_airport"));
                row.add(rs.getString("to_airport"));
                row.add(rs.getTimestamp("departure_time"));
                row.add(rs.getTimestamp("arrival_time"));
                row.add(rs.getInt("available_first_class"));
                row.add(rs.getInt("available_business_class"));
                row.add(rs.getInt("available_economy_class"));
                data.add(row);
            }

            DefaultTableModel model = new DefaultTableModel(data, columns);
            flightTable.setModel(model);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error searching flights: " + e.getMessage());
        }
    }

    private void bookFlight() {
        int selectedRow = flightTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a flight.");
            return;
        }

        String selectedCustomer = (String) customerComboBox.getSelectedItem();
        if (selectedCustomer == null) {
            JOptionPane.showMessageDialog(this, "Please select a customer.");
            return;
        }

        int flightId = (int) flightTable.getValueAt(selectedRow, 0);

        String[] options = {"First", "Business", "Economy"};
        String selectedClass = (String) JOptionPane.showInputDialog(
                this,
                "Select class to book:",
                "Choose Class",
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                "Economy"
        );

        if (selectedClass == null) return;

        try (Connection conn = DBConnection.getConnection()) {
            // Get user_id
            PreparedStatement userStmt = conn.prepareStatement("SELECT id FROM users WHERE username = ?");
            userStmt.setString(1, selectedCustomer);
            ResultSet userRs = userStmt.executeQuery();
            if (!userRs.next()) {
                JOptionPane.showMessageDialog(this, "Customer not found.");
                return;
            }
            int userId = userRs.getInt("id");

            // Check seat availability
            String seatQuery = "SELECT s.id, s.available_first_class, s.available_business_class, s.available_economy_class " +
                    "FROM flights f " +
                    "JOIN airplanes a ON f.airplane_id = a.id " +
                    "JOIN seats s ON s.airplane_id = a.id " +
                    "WHERE f.id = ?";
            PreparedStatement seatStmt = conn.prepareStatement(seatQuery);
            seatStmt.setInt(1, flightId);
            ResultSet seatRs = seatStmt.executeQuery();

            if (seatRs.next()) {
                int seatId = seatRs.getInt("id");
                boolean canBook = false;

                String updateSeat = "";
                switch (selectedClass) {
                    case "First":
                        if (seatRs.getInt("available_first_class") > 0) {
                            updateSeat = "UPDATE seats SET available_first_class = available_first_class - 1 WHERE id = ?";
                            canBook = true;
                        }
                        break;
                    case "Business":
                        if (seatRs.getInt("available_business_class") > 0) {
                            updateSeat = "UPDATE seats SET available_business_class = available_business_class - 1 WHERE id = ?";
                            canBook = true;
                        }
                        break;
                    case "Economy":
                        if (seatRs.getInt("available_economy_class") > 0) {
                            updateSeat = "UPDATE seats SET available_economy_class = available_economy_class - 1 WHERE id = ?";
                            canBook = true;
                        }
                        break;
                }

                if (!canBook) {
                    JOptionPane.showMessageDialog(this, "No seats available in selected class.");
                    return;
                }

                // Book
                conn.setAutoCommit(false);

                PreparedStatement bookStmt = conn.prepareStatement(
                        "INSERT INTO bookings (user_id, flight_id, class) VALUES (?, ?, ?)");
                bookStmt.setInt(1, userId);
                bookStmt.setInt(2, flightId);
                bookStmt.setString(3, selectedClass);
                bookStmt.executeUpdate();

                PreparedStatement updateStmt = conn.prepareStatement(updateSeat);
                updateStmt.setInt(1, seatId);
                updateStmt.executeUpdate();

                conn.commit();
                JOptionPane.showMessageDialog(this, "Flight booked successfully for " + selectedCustomer);
                searchFlights();

            } else {
                JOptionPane.showMessageDialog(this, "Seats not found for the selected flight.");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error booking flight: " + e.getMessage());
        }
    }

    // generate a simple operational report dialog
    private void generateReport() {
        StringBuilder report = new StringBuilder();
        try (Connection conn = DBConnection.getConnection()) {
            // Total flights
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS total_flights FROM flights");
            rs.next();
            int totalFlights = rs.getInt("total_flights");
            report.append("Total Flights: ").append(totalFlights).append("\n");

            // Total bookings by class
            rs = stmt.executeQuery("SELECT class, COUNT(*) AS count FROM bookings GROUP BY class");
            report.append("Total Bookings by Class:\n");
            while (rs.next()) {
                String flightClass = rs.getString("class");
                int count = rs.getInt("count");
                report.append("  ").append(flightClass).append(": ").append(count).append("\n");
            }

            // Optionally, add more stats here (e.g., most popular routes, available seats, etc.)

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error generating report: " + e.getMessage());
            return;
        }

        // Show report in dialog
        JTextArea textArea = new JTextArea(report.toString());
        textArea.setEditable(false);
        JOptionPane.showMessageDialog(this, new JScrollPane(textArea), "Operational Report", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        new OperatorDashboard("operator_user");
    }
}
