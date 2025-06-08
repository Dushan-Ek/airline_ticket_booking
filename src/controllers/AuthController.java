package controllers;

import dao.UserDAO;
import models.User;

public class AuthController {
    public static User login(String username, String password) {
        try {
            return UserDAO.authenticate(username, password);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}