package airline.gui.admin;

import dao.AirportDAO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddAirportWindow extends JFrame {

    private JTextField nameField, locationField;

    public AddAirportWindow() {
        setTitle("Add New Airport");
        setSize(400, 250);
        setLayout(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JLabel nameLabel = new JLabel("Airport Name:");
        nameLabel.setBounds(30, 30, 100, 25);
        add(nameLabel);

        nameField = new JTextField();
        nameField.setBounds(140, 30, 200, 25);
        add(nameField);

        JLabel locationLabel = new JLabel("Location:");
        locationLabel.setBounds(30, 70, 100, 25);
        add(locationLabel);

        locationField = new JTextField();
        locationField.setBounds(140, 70, 200, 25);
        add(locationField);

        JButton addBtn = new JButton("Add Airport");
        addBtn.setBounds(140, 120, 120, 30);
        add(addBtn);

        // Add action listener for real DB insert using AirportDAO
        addBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText().trim();
                String location = locationField.getText().trim();

                if (name.isEmpty() || location.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please fill all fields.");
                    return;
                }

                try {
                    boolean success = AirportDAO.addAirport(name, location);

                    if (success) {
                        JOptionPane.showMessageDialog(null, "Airport added successfully.");
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(null, "Error adding airport.");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage());
                }
            }
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }
}
