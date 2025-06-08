package airline.gui.admin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ReportViewerWindow extends JFrame {
    private JTextField flightIDField, airportField, startDateField, endDateField;
    private JButton flightManifestBtn, airportReportBtn;

    public ReportViewerWindow() {
        setTitle("Reports");
        setSize(600, 400);
        setLayout(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Flight ID Field for Passenger Manifest Report
        JLabel flightIDLabel = new JLabel("Flight ID:");
        flightIDLabel.setBounds(30, 30, 100, 25);
        add(flightIDLabel);
        flightIDField = new JTextField();
        flightIDField.setBounds(150, 30, 150, 25);
        add(flightIDField);

        // Airport Field for Airport Report
        JLabel airportLabel = new JLabel("Airport:");
        airportLabel.setBounds(30, 70, 100, 25);
        add(airportLabel);
        airportField = new JTextField();
        airportField.setBounds(150, 70, 150, 25);
        add(airportField);

        // Start Date Field for Airport Report
        JLabel startDateLabel = new JLabel("Start Date:");
        startDateLabel.setBounds(30, 110, 100, 25);
        add(startDateLabel);
        startDateField = new JTextField();
        startDateField.setBounds(150, 110, 150, 25);
        add(startDateField);

        // End Date Field for Airport Report
        JLabel endDateLabel = new JLabel("End Date:");
        endDateLabel.setBounds(30, 150, 100, 25);
        add(endDateLabel);
        endDateField = new JTextField();
        endDateField.setBounds(150, 150, 150, 25);
        add(endDateField);

        // Flight Passenger Manifest Button
        flightManifestBtn = new JButton("Flight Passenger Manifest");
        flightManifestBtn.setBounds(30, 200, 250, 30);
        add(flightManifestBtn);

        // Airport Flight Report Button
        airportReportBtn = new JButton("Airport Flight Report");
        airportReportBtn.setBounds(30, 250, 250, 30);
        add(airportReportBtn);

        // Action Listener for Passenger Manifest Report Button
        flightManifestBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String flightID = flightIDField.getText();
                
                // TODO: Implement the database interaction to generate the passenger manifest for a specific flight.
                // Example: ReportDAO.generatePassengerManifest(flightID);

                // For now, show a placeholder message.
                JOptionPane.showMessageDialog(ReportViewerWindow.this, "Passenger Manifest Report for Flight ID: " + flightID + " (TODO)");
                clearFields();
            }
        });

        // Action Listener for Airport Report Button
        airportReportBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String airport = airportField.getText();
                String startDate = startDateField.getText();
                String endDate = endDateField.getText();
                
                // TODO: Implement the database interaction to generate the airport flight report.
                // Example: ReportDAO.generateAirportReport(airport, startDate, endDate);

                // For now, show a placeholder message.
                JOptionPane.showMessageDialog(ReportViewerWindow.this, "Airport Flight Report for Airport: " + airport +
                        "\nFrom: " + startDate + " To: " + endDate + " (TODO)");
                clearFields();
            }
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Method to clear the input fields after generating the report
    private void clearFields() {
        flightIDField.setText("");
        airportField.setText("");
        startDateField.setText("");
        endDateField.setText("");
    }

    public static void main(String[] args) {
        new ReportViewerWindow();
    }
}
