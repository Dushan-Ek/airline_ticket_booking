package main;

import airline.gui.LoginWindow;
import dao.UserDAO;
import java.util.List;
import models.User;

public class Main {

    public static void main(String[] args) {
        LoginWindow loginWindow = new LoginWindow(); // Launches the login GUI
        System.out.println("App started!");

//        List<User> users = UserDAO.getAllUsers();
//
//        // Print out the details of each user
//        if (users != null && !users.isEmpty()) {
//            for (User user : users) {
//                System.out.println("ID: " + user.getId());
//                System.out.println("Username: " + user.getUsername());
//                System.out.println("Role: " + user.getRole());
//                System.out.println("Status: " + (user.isStatus() ? "Active" : "Inactive"));
//                System.out.println("------");
//            }
//        } else {
//            System.out.println("No users found.");
//        }
    }
}
