package airline.gui.admin;

import dao.AirplaneDAO;
import dao.AirportDAO;
import dao.FlightDAO;
import models.Flight;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Map;

public class FlightSchedulerWindow extends JFrame {

    private JTextField fromDateTimeField, toDateTimeField;
    private JComboBox<String> airplaneComboBox, fromAirportComboBox, toAirportComboBox;
    private JButton addFlightBtn, filterBtn;
    private Map<String, Integer> airplaneMap;
    private Map<String, Integer> airportMap;
    private DefaultTableModel flightTableModel;

    public FlightSchedulerWindow() throws Exception {
        setTitle("Flight Scheduler");
        setSize(650, 560);
        setLayout(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JLabel fromTimeLabel = new JLabel("Departure (yyyy-MM-dd HH:mm):");
        fromTimeLabel.setBounds(30, 20, 200, 25);
        add(fromTimeLabel);

        fromDateTimeField = new JTextField("2025-05-26 08:00");
        fromDateTimeField.setBounds(240, 20, 180, 25);
        add(fromDateTimeField);

        JLabel toTimeLabel = new JLabel("Arrival (yyyy-MM-dd HH:mm):");
        toTimeLabel.setBounds(30, 60, 200, 25);
        add(toTimeLabel);

        toDateTimeField = new JTextField("2025-05-26 12:00");
        toDateTimeField.setBounds(240, 60, 180, 25);
        add(toDateTimeField);

        filterBtn = new JButton("Filter");
        filterBtn.setBounds(480, 20, 120, 25);
        add(filterBtn);

        filterBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    filterAvailableAirplanes();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(FlightSchedulerWindow.this, "Error filtering airplanes: " + ex.getMessage());
                }
            }
        });

        JLabel airplaneLabel = new JLabel("Airplane:");
        airplaneLabel.setBounds(30, 100, 100, 25);
        add(airplaneLabel);

        airplaneComboBox = new JComboBox<>();
        airplaneComboBox.setBounds(240, 100, 180, 25);
        add(airplaneComboBox);

        JLabel fromAirportLabel = new JLabel("From Airport:");
        fromAirportLabel.setBounds(30, 140, 100, 25);
        add(fromAirportLabel);

        fromAirportComboBox = new JComboBox<>();
        fromAirportComboBox.setBounds(240, 140, 180, 25);
        add(fromAirportComboBox);

        JLabel toAirportLabel = new JLabel("To Airport:");
        toAirportLabel.setBounds(30, 180, 100, 25);
        add(toAirportLabel);

        toAirportComboBox = new JComboBox<>();
        toAirportComboBox.setBounds(240, 180, 180, 25);
        add(toAirportComboBox);

        addFlightBtn = new JButton("Add Flight");
        addFlightBtn.setBounds(240, 220, 150, 30);
        add(addFlightBtn);

        String[] columnNames = {"Flight ID", "Airplane", "From", "To", "Departure", "Arrival", "Action"};
        flightTableModel = new DefaultTableModel(columnNames, 0);
        JTable flightTable = new JTable(flightTableModel) {
            public boolean isCellEditable(int row, int column) {
                return column == 6;
            }
        };

        flightTable.getColumn("Action").setCellRenderer(new ButtonRenderer());
        flightTable.getColumn("Action").setCellEditor(new ButtonEditor(new JCheckBox(), flightTableModel, this));

        JScrollPane scrollPane = new JScrollPane(flightTable);
        scrollPane.setBounds(30, 270, 580, 220);
        add(scrollPane);

        loadAllAirplanes();
        loadAllAirports();

        addFlightBtn.addActionListener(e -> {
            try {
                Flight flight = getFlightFromForm();
                if (FlightDAO.addFlight(flight)) {
                    JOptionPane.showMessageDialog(this, "Flight added successfully.");
                    loadFlightsIntoTable();
                    clearFields();
                    // Refresh airplanes combo with all airplanes (optional)
                    loadAllAirplanes();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add flight.");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        setLocationRelativeTo(null);
        setVisible(true);
        loadFlightsIntoTable();
    }

    private Flight getFlightFromForm() throws Exception {
        String fromStr = fromDateTimeField.getText().trim();
        String toStr = toDateTimeField.getText().trim();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        sdf.setLenient(false);

        Timestamp departure = new Timestamp(sdf.parse(fromStr).getTime());
        Timestamp arrival = new Timestamp(sdf.parse(toStr).getTime());

        if (!departure.before(arrival)) {
            throw new Exception("Departure time must be before arrival time.");
        }

        if (airplaneComboBox.getSelectedItem() == null || fromAirportComboBox.getSelectedItem() == null || toAirportComboBox.getSelectedItem() == null) {
            throw new Exception("Please select airplane and airports.");
        }

        int airplaneId = airplaneMap.get(airplaneComboBox.getSelectedItem().toString());
        int fromAirportId = airportMap.get(fromAirportComboBox.getSelectedItem().toString());
        int toAirportId = airportMap.get(toAirportComboBox.getSelectedItem().toString());

        if (fromAirportId == toAirportId) {
            throw new Exception("Departure and arrival airports must be different.");
        }

        String flightNumber = "FL" + System.currentTimeMillis() % 100000;
        String status = "Scheduled";

        return new Flight(airplaneId, fromAirportId, toAirportId, departure, arrival, flightNumber, status);
    }

    private void loadAllAirplanes() throws Exception {
        airplaneComboBox.removeAllItems();
        airplaneMap = AirplaneDAO.getAllAirplanes();
        for (String name : airplaneMap.keySet()) {
            airplaneComboBox.addItem(name);
        }
    }

    private void loadAllAirports() throws Exception {
        fromAirportComboBox.removeAllItems();
        toAirportComboBox.removeAllItems();
        airportMap = AirportDAO.getAirportIdNameMap();
        for (String name : airportMap.keySet()) {
            fromAirportComboBox.addItem(name);
            toAirportComboBox.addItem(name);
        }
    }

    private void loadFlightsIntoTable() {
        try {
            flightTableModel.setRowCount(0);
            for (Object[] f : FlightDAO.getAllFlightsForTable()) {
                flightTableModel.addRow(f);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading flights: " + ex.getMessage());
        }
    }

    private void clearFields() {
        fromDateTimeField.setText("2025-05-26 08:00");
        toDateTimeField.setText("2025-05-26 12:00");
    }

    // New method to filter airplanes by availability for given time period
    private void filterAvailableAirplanes() throws Exception {
        String fromStr = fromDateTimeField.getText().trim();
        String toStr = toDateTimeField.getText().trim();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        sdf.setLenient(false);

        Timestamp departure = new Timestamp(sdf.parse(fromStr).getTime());
        Timestamp arrival = new Timestamp(sdf.parse(toStr).getTime());

        if (!departure.before(arrival)) {
            throw new Exception("Departure time must be before arrival time.");
        }

        airplaneComboBox.removeAllItems();
        airplaneMap = AirplaneDAO.getAvailableAirplanes(departure, arrival);
        if (airplaneMap.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No airplanes available for the selected time period.");
        }

        for (String name : airplaneMap.keySet()) {
            airplaneComboBox.addItem(name);
        }
    }

    // Delete Button Renderer
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setText("Delete");
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            return this;
        }
    }

    // Delete Button Editor
    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private JTable table;
        private DefaultTableModel model;
        private FlightSchedulerWindow parentWindow;

        public ButtonEditor(JCheckBox checkBox, DefaultTableModel model, FlightSchedulerWindow parent) {
            super(checkBox);
            this.model = model;
            this.parentWindow = parent;
            button = new JButton("Delete");
            button.addActionListener(e -> {
                int row = table.getSelectedRow();
                if (row < 0) return;
                int flightId = Integer.parseInt(model.getValueAt(row, 0).toString());
                int confirm = JOptionPane.showConfirmDialog(button, "Delete Flight ID: " + flightId + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        if (FlightDAO.deleteFlight(flightId)) {
                            JOptionPane.showMessageDialog(button, "Flight deleted.");
                            model.removeRow(row);
                        } else {
                            JOptionPane.showMessageDialog(button, "Failed to delete flight.");
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(button, "Error: " + ex.getMessage());
                    }
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.table = table;
            return button;
        }

        public Object getCellEditorValue() {
            return "Delete";
        }

        public boolean stopCellEditing() {
            return super.stopCellEditing();
        }

        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
    }
}
