package airline.gui.admin;

import dao.SeatDAO;

import javax.swing.*;

public class SeatManagementWindow extends JFrame {
    public SeatManagementWindow() {
        setTitle("Manage Seats");
        setSize(400, 250);
        setLayout(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JLabel flightIdLabel = new JLabel("Flight ID:");
        flightIdLabel.setBounds(30, 30, 100, 25);
        add(flightIdLabel);

        JTextField flightIdField = new JTextField();
        flightIdField.setBounds(140, 30, 200, 25);
        add(flightIdField);

        JLabel classLabel = new JLabel("Class:");
        classLabel.setBounds(30, 70, 100, 25);
        add(classLabel);

        JComboBox<String> classBox = new JComboBox<>(new String[]{"First", "Business", "Economy"});
        classBox.setBounds(140, 70, 200, 25);
        add(classBox);

        JLabel seatCountLabel = new JLabel("Seat Count:");
        seatCountLabel.setBounds(30, 110, 100, 25);
        add(seatCountLabel);

        JTextField seatCountField = new JTextField();
        seatCountField.setBounds(140, 110, 200, 25);
        add(seatCountField);

        JButton addBtn = new JButton("Add Seats");
        addBtn.setBounds(120, 160, 150, 30);
        add(addBtn);

        addBtn.addActionListener(e -> {
            try {
                int flightId = Integer.parseInt(flightIdField.getText().trim());
                String seatClass = (String) classBox.getSelectedItem();
                int count = Integer.parseInt(seatCountField.getText().trim());

                boolean success = SeatDAO.addSeats(flightId, seatClass, count);
                JOptionPane.showMessageDialog(this, success ? "Seats added!" : "Failed to add seats.");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Enter valid numbers.");
            }
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }
}
