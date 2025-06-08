package airline.gui;

import controllers.AuthController;
import javax.swing.*;
import models.User;

public class LoginWindow extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginWindow() {
        setTitle("Airline Login");
        setSize(300, 180);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(30, 20, 80, 25);
        add(userLabel);

        usernameField = new JTextField();
        usernameField.setBounds(120, 20, 130, 25);
        add(usernameField);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(30, 60, 80, 25);
        add(passLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(120, 60, 130, 25);
        add(passwordField);

        JButton loginButton = new JButton("Login");
        loginButton.setBounds(90, 100, 100, 30);
        add(loginButton);

        loginButton.addActionListener(e -> {
            try {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                // Basic input validation
                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter both username and password.", "Input Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                User user = AuthController.login(username, password);

                if (user == null) {
                    JOptionPane.showMessageDialog(this, "Invalid username or password!", "Login Failed", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String role = user.getRole();

                JOptionPane.showMessageDialog(this, "Login successful! Role: " + role, "Welcome", JOptionPane.INFORMATION_MESSAGE);

                // Open appropriate dashboard based on user role
                switch (role) {
                    case "Customer":
                        new CustomerDashboard(username);  // Update if CustomerDashboard also needs a back reference
                        break;
                    case "Operator":
                        new OperatorDashboard(username);  // Update if OperatorDashboard also needs a back reference
                        break;
                    case "Admin":
                        new AdminDashboard(username, this); // <-- Corrected line
                        this.setVisible(false);             // Hide login window while dashboard is open
                        break;
                    default:
                        JOptionPane.showMessageDialog(this, "Unknown role: " + role, "Error", JOptionPane.ERROR_MESSAGE);
                        break;
                }

                // Note: Don't dispose here if using "back" navigation
                // dispose();

            } catch (Exception ex) {
                ex.printStackTrace(); // for developer debugging
                JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Main method to run login window
    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginWindow::new);
    }
}
