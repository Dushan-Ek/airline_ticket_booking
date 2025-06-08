package airline.gui.operator;

import dao.BookingDAO;
import dao.UserDAO;
import dao.FlightDAO;
import models.Flight;
import models.User;

import javax.swing.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class OperatorBookingWindow extends JFrame {

    private JComboBox<String> customerComboBox;
    private JComboBox<String> flightComboBox;
    private JComboBox<String> classComboBox;
    private JButton bookButton;

    private Map<String, Integer> usernameToIdMap = new HashMap<>();
    private Map<String, Integer> flightMap = new HashMap<>();

    public OperatorBookingWindow() {
        setTitle("Book Flight for Customer");
        setSize(450, 280);
        setLayout(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Customer Label & ComboBox
        JLabel customerLabel = new JLabel("Customer:");
        customerLabel.setBounds(30, 30, 150, 25);
        add(customerLabel);

        customerComboBox = new JComboBox<>();
        customerComboBox.setMaximumRowCount(10);
        customerComboBox.setBounds(180, 30, 200, 25);
        add(customerComboBox);

        loadCustomerUsernames();

        // Flight Label & ComboBox
        JLabel flightLabel = new JLabel("Flight:");
        flightLabel.setBounds(30, 70, 150, 25);
        add(flightLabel);

        flightComboBox = new JComboBox<>();
        flightComboBox.setMaximumRowCount(10);
        flightComboBox.setBounds(180, 70, 200, 25);
        add(flightComboBox);

        loadFlights();

        // Class Label & ComboBox
        JLabel classLabel = new JLabel("Class:");
        classLabel.setBounds(30, 110, 150, 25);
        add(classLabel);

        classComboBox = new JComboBox<>(new String[]{"Economy", "Business", "First"});
        classComboBox.setBounds(180, 110, 200, 25);
        add(classComboBox);

        // Book Button
        bookButton = new JButton("Book Flight");
        bookButton.setBounds(140, 170, 150, 30);
        add(bookButton);

        // Booking Action
        bookButton.addActionListener(e -> {
            String username = (String) customerComboBox.getSelectedItem();
            String flightDisplay = (String) flightComboBox.getSelectedItem();
            String seatClass = (String) classComboBox.getSelectedItem();

            if (username == null || flightDisplay == null) {
                JOptionPane.showMessageDialog(this, "Please select both customer and flight.");
                return;
            }

            int userId = usernameToIdMap.get(username);
            int flightId = flightMap.get(flightDisplay);

            try {
                boolean success = BookingDAO.bookFlight(userId, flightId, seatClass);
                if (success) {
                    UserDAO.deactivateUser(userId);  // Set user status to false (inactive)
                    JOptionPane.showMessageDialog(this, "Flight booked successfully and user deactivated.");
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Booking failed. No seats available or error.");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "An error occurred: " + ex.getMessage());
            }
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void loadCustomerUsernames() {
        usernameToIdMap = UserDAO.getAllCustomerUserMap();
        for (String username : usernameToIdMap.keySet()) {
            customerComboBox.addItem(username);
        }
    }

    private void loadFlights() {
        List<Flight> flights = FlightDAO.getAllFlights();
        for (Flight flight : flights) {
            String label = flight.getId() + " - " + flight.getArrivalAirportName() + " to " + flight.getArrivalAirportName();
            flightComboBox.addItem(label);
            flightMap.put(label, flight.getId());
        }
    }
}
