package airline.gui.admin;

import dao.UserDAO;
import models.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.List;

public class UserManagementWindow extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox;
    private JButton createUserBtn, updateUserBtn, deleteUserBtn, viewUsersBtn, activateUserBtn;
    private JTable usersTable;
    private DefaultTableModel tableModel;

    public UserManagementWindow() {
        setTitle("User Management");
        setSize(600, 500); // increased height to accommodate new button
        setLayout(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        addLabel("Username:", 30, 30);
        usernameField = addTextField(130, 30);

        addLabel("Password:", 30, 70);
        passwordField = new JPasswordField();
        passwordField.setBounds(130, 70, 150, 25);
        add(passwordField);

        addLabel("Role:", 30, 110);
        roleComboBox = new JComboBox<>(new String[]{"Customer", "Operator", "Admin"});
        roleComboBox.setBounds(130, 110, 150, 25);
        add(roleComboBox);

        createUserBtn = addButton("Create User", 30, 150);
        updateUserBtn = addButton("Update User", 160, 150);
        deleteUserBtn = addButton("Deactivate User", 290, 150);
        viewUsersBtn = addButton("View All Users", 420, 150);
        activateUserBtn = addButton("Activate User", 30, 360); // New button

        String[] columns = {"ID", "Username", "Role"};
        tableModel = new DefaultTableModel(columns, 0);
        usersTable = new JTable(tableModel);
        JScrollPane tableScroll = new JScrollPane(usersTable);
        tableScroll.setBounds(30, 200, 510, 150);
        add(tableScroll);

        createUserBtn.addActionListener((ActionEvent e) -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String role = (String) roleComboBox.getSelectedItem();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter both username and password.", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                boolean success = UserDAO.addUser(username, password, role);
                if (success) {
                    JOptionPane.showMessageDialog(this, "User added successfully.");
                    clearFields();
                    loadUsers();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add user.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        updateUserBtn.addActionListener((ActionEvent e) -> {
            int selectedRow = usersTable.getSelectedRow();
            if (selectedRow != -1) {
                int userId = (int) usersTable.getValueAt(selectedRow, 0);
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                String role = (String) roleComboBox.getSelectedItem();

                if (username.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Username cannot be empty.");
                    return;
                }

                try {
                    boolean success = UserDAO.updateUser(userId, username, password, role);
                    if (success) {
                        JOptionPane.showMessageDialog(this, "User updated successfully.");
                        clearFields();
                        loadUsers();
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to update user.");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a user to update.");
            }
        });

        deleteUserBtn.addActionListener((ActionEvent e) -> {
            int selectedRow = usersTable.getSelectedRow();
            if (selectedRow != -1) {
                int userId = (int) usersTable.getValueAt(selectedRow, 0);
                try {
                    if (UserDAO.deactivateUser(userId)) {
                        JOptionPane.showMessageDialog(this, "User deactivated successfully.");
                        loadUsers();
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to deactivate user.");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a user to deactivate.");
            }
        });

        activateUserBtn.addActionListener((ActionEvent e) -> {
            int selectedRow = usersTable.getSelectedRow();
            if (selectedRow != -1) {
                int userId = (int) usersTable.getValueAt(selectedRow, 0);
                try {
                    if (UserDAO.activateUser(userId)) {
                        JOptionPane.showMessageDialog(this, "User activated successfully.");
                        loadUsers();
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to activate user.");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a user to activate.");
            }
        });

        viewUsersBtn.addActionListener((ActionEvent e) -> loadUsers());

        usersTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = usersTable.getSelectedRow();
            if (selectedRow != -1) {
                usernameField.setText((String) usersTable.getValueAt(selectedRow, 1));
                roleComboBox.setSelectedItem(usersTable.getValueAt(selectedRow, 2));
            }
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void loadUsers() {
        tableModel.setRowCount(0);
        List<User> users = UserDAO.getAllUsers();
        for (User user : users) {
            tableModel.addRow(new Object[]{
                user.getId(),
                user.getUsername(),
                user.getRole()
            });
        }
    }

    private void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
        roleComboBox.setSelectedIndex(0);
    }

    private void addLabel(String text, int x, int y) {
        JLabel label = new JLabel(text);
        label.setBounds(x, y, 100, 25);
        add(label);
    }

    private JTextField addTextField(int x, int y) {
        JTextField field = new JTextField();
        field.setBounds(x, y, 150, 25);
        add(field);
        return field;
    }

    private JButton addButton(String label, int x, int y) {
        JButton button = new JButton(label);
        button.setBounds(x, y, 120, 30);
        add(button);
        return button;
    }

    public static void main(String[] args) {
        new UserManagementWindow();
    }
}
