package airline.gui.common;

import javax.swing.*;
import java.awt.*;

public class CustomerFlightSearchPanel extends JPanel {

    private JTable flightTable;
    private JTextField fromField, toField;
    private JComboBox<String> classComboBox;

    public CustomerFlightSearchPanel() {
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        fromField = new JTextField(10);
        toField = new JTextField(10);
        classComboBox = new JComboBox<>(new String[]{"Economy", "Business", "First"});

        JButton searchButton = new JButton("Search");

        topPanel.add(new JLabel("From:"));
        topPanel.add(fromField);
        topPanel.add(new JLabel("To:"));
        topPanel.add(toField);
        topPanel.add(new JLabel("Class:"));
        topPanel.add(classComboBox);
        topPanel.add(searchButton);

        add(topPanel, BorderLayout.NORTH);

        flightTable = new JTable();
        add(new JScrollPane(flightTable), BorderLayout.CENTER);

        searchButton.addActionListener(e -> {
            // TODO: Replace with actual DB search logic based on from/to/class
            String[][] mockData = {
                {"FL001", "NYC", "LAX", "08:00", "11:00", "Economy"},
                {"FL002", "NYC", "LAX", "12:00", "15:00", "Business"}
            };
            String[] cols = {"Flight ID", "From", "To", "Departure", "Arrival", "Class"};
            flightTable.setModel(new javax.swing.table.DefaultTableModel(mockData, cols));
        });
    }

    public JTable getFlightTable() {
        return flightTable;
    }

    public String getSelectedFlightId() {
        int row = flightTable.getSelectedRow();
        return (row != -1) ? (String) flightTable.getValueAt(row, 0) : null;
    }

    public String getSelectedClass() {
        return (String) classComboBox.getSelectedItem();
    }
}
