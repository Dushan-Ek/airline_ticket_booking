package airline.gui.admin;

import dao.AirplaneDAO;
import dao.SeatDAO;
import dao.AirportDAO;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import models.Airport;

import javax.swing.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.Airplane;
import models.Seat;

public class AirplaneManagementWindow extends JFrame {

    private JTextField codeField, firstClassField, businessClassField, economyClassField, totalSeatsField;
    private JComboBox<String> typeComboBox;
    private JComboBox<Airport> airportComboBox;
    private JCheckBox availableCheckBox;

    public AirplaneManagementWindow() {
        setTitle("Manage Airplanes");
        setSize(400, 500);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);

        JLabel typeLabel = new JLabel("Type:");
        typeLabel.setBounds(30, 30, 100, 25);
        add(typeLabel);
        typeComboBox = new JComboBox<>(new String[]{"small", "medium", "large"});
        typeComboBox.setBounds(140, 30, 200, 25);
        add(typeComboBox);

        JLabel codeLabel = new JLabel("Airplane Code:");
        codeLabel.setBounds(30, 70, 100, 25);
        add(codeLabel);
        codeField = new JTextField();
        codeField.setBounds(140, 70, 200, 25);
        add(codeField);

        JLabel airportLabel = new JLabel("Current Airport:");
        airportLabel.setBounds(30, 110, 100, 25);
        add(airportLabel);
        airportComboBox = new JComboBox<>();
        airportComboBox.setBounds(140, 110, 200, 25);
        add(airportComboBox);

        JLabel availableLabel = new JLabel("Available:");
        availableLabel.setBounds(30, 150, 100, 25);
        add(availableLabel);
        availableCheckBox = new JCheckBox();
        availableCheckBox.setSelected(true);
        availableCheckBox.setBounds(140, 150, 20, 25);
        add(availableCheckBox);

        JLabel totalLabel = new JLabel("Total Seats:");
        totalLabel.setBounds(30, 190, 100, 25);
        add(totalLabel);
        totalSeatsField = new JTextField();
        totalSeatsField.setBounds(140, 190, 200, 25);
        totalSeatsField.setEditable(false);
        add(totalSeatsField);

        JLabel firstLabel = new JLabel("First Class:");
        firstLabel.setBounds(30, 230, 100, 25);
        add(firstLabel);
        firstClassField = new JTextField();
        firstClassField.setBounds(140, 230, 200, 25);
        add(firstClassField);

        JLabel businessLabel = new JLabel("Business Class:");
        businessLabel.setBounds(30, 270, 100, 25);
        add(businessLabel);
        businessClassField = new JTextField();
        businessClassField.setBounds(140, 270, 200, 25);
        add(businessClassField);

        JLabel economyLabel = new JLabel("Economy Class:");
        economyLabel.setBounds(30, 310, 100, 25);
        add(economyLabel);
        economyClassField = new JTextField();
        economyClassField.setBounds(140, 310, 200, 25);
        add(economyClassField);

        JButton addButton = new JButton("Add Airplane");
        addButton.setBounds(120, 360, 150, 30);
        add(addButton);

        typeComboBox.addActionListener(e -> {
            String type = (String) typeComboBox.getSelectedItem();
            if (type.equals("small")) {
                totalSeatsField.setText("30");
            }
            if (type.equals("medium")) {
                totalSeatsField.setText("50");
            }
            if (type.equals("large")) {
                totalSeatsField.setText("100");
            }
        });

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Debug
                    System.out.println("I am here");

                    String type = (String) typeComboBox.getSelectedItem();
                    String code = codeField.getText().trim();
                    String firstText = firstClassField.getText().trim();
                    String businessText = businessClassField.getText().trim();
                    String economyText = economyClassField.getText().trim();

                    if (code.isEmpty()) {
                        throw new IllegalArgumentException("Airplane code cannot be empty.");
                    }

                    if (firstText.isEmpty() || businessText.isEmpty() || economyText.isEmpty()) {
                        throw new IllegalArgumentException("Seat fields cannot be empty.");
                    }

                    Airport selectedAirport = (Airport) airportComboBox.getSelectedItem();
                    if (selectedAirport == null) {
                        throw new IllegalArgumentException("No airport selected.");
                    }

                    int first = Integer.parseInt(firstText);
                    int business = Integer.parseInt(businessText);
                    int economy = Integer.parseInt(economyText);

                    if (first < 0 || business < 0 || economy < 0) {
                        throw new IllegalArgumentException("Seat numbers must be non-negative.");
                    }

                    int total = Integer.parseInt(totalSeatsField.getText());

                    if (first + business + economy != total) {
                        throw new IllegalArgumentException("Total seats must equal the sum of first, business, and economy class.");
                    }

                    int airportId = selectedAirport.getId();
                    boolean available = availableCheckBox.isSelected();

//                    boolean success = AirplaneDAO.addAirplane(code, type, airportId, available);
                    int id = AirplaneDAO.addAirplane(code, type, airportId, available);

                    if (id != -1) {
                        Seat seat = new Seat(id, total, first, business, economy);
                        SeatDAO.addSeat(seat);
                        JOptionPane.showMessageDialog(AirplaneManagementWindow.this, "Airplane & seats added.");
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(AirplaneManagementWindow.this, "Failed to add airplane.");
                    }

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(AirplaneManagementWindow.this, "Please enter valid numbers in all seat fields.");
                    Logger.getLogger(AirplaneManagementWindow.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(AirplaneManagementWindow.this, ex.getMessage());
                    Logger.getLogger(AirplaneManagementWindow.class.getName()).log(Level.WARNING, null, ex);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Logger.getLogger(AirplaneManagementWindow.class.getName()).log(Level.SEVERE, null, ex);
                    JOptionPane.showMessageDialog(AirplaneManagementWindow.this, "Unexpected error: " + ex.getMessage());
                }
            }
        });

        loadAirports();
        typeComboBox.setSelectedIndex(0);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void loadAirports() {
        try {
            List<Airport> airports = AirportDAO.getAllAirports();
            for (Airport airport : airports) {
                airportComboBox.addItem(airport);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
