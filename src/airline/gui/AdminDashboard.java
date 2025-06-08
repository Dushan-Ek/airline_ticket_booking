package airline.gui;

import airline.gui.admin.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

public class AdminDashboard extends JFrame {
    private JFrame previousWindow;

    public AdminDashboard(String username, JFrame previousWindow) {
        this.previousWindow = previousWindow;

        setTitle("Admin Dashboard - " + username);
        setSize(500, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        JButton manageUsersBtn = new JButton("Manage Users");
        manageUsersBtn.setBounds(50, 50, 180, 30);
        add(manageUsersBtn);

        JButton scheduleFlightsBtn = new JButton("Schedule Flights");
        scheduleFlightsBtn.setBounds(50, 100, 180, 30);
        add(scheduleFlightsBtn);

        JButton viewReportsBtn = new JButton("View Reports");
        viewReportsBtn.setBounds(50, 150, 180, 30);
        add(viewReportsBtn);

        JButton manageAirplanesBtn = new JButton("Manage Airplanes");
        manageAirplanesBtn.setBounds(50, 200, 180, 30);
        add(manageAirplanesBtn);

        // Replacing "Manage Seats" with "Add Airport"
        JButton addAirportBtn = new JButton("Add Airport");
        addAirportBtn.setBounds(50, 250, 180, 30);
        add(addAirportBtn);

        // Back Button
        JButton backButton = new JButton("Back");
        backButton.setBounds(50, 300, 180, 30);
        add(backButton);

        // Action Listeners
        manageUsersBtn.addActionListener(e -> new UserManagementWindow());
        scheduleFlightsBtn.addActionListener(e -> {
            try {
                new FlightSchedulerWindow();
            } catch (Exception ex) {
                Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        viewReportsBtn.addActionListener(e -> new ReportViewerWindow());
        manageAirplanesBtn.addActionListener(e -> new AirplaneManagementWindow());
        addAirportBtn.addActionListener(e -> new AddAirportWindow());

        // Back button listener
        backButton.addActionListener(e -> {
            if (previousWindow != null) {
                previousWindow.setVisible(true);
            }
            dispose(); // Close current window
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Entry point for testing only
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminDashboard("admin", null));
    }
}
