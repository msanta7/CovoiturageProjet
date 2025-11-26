package com.example.covoiturage;

public class UserSession {
    private static int userId;
    private static String userName;
    private static String userEmail;
    private static String userType;

    public static void setCurrentUser(int id, String name, String email, String type) {
        userId = id;
        userName = name;
        userEmail = email;
        userType = type;
    }

    public static int getUserId() {
        return userId;
    }

    public static String getUserName() {
        return userName;
    }

    public static String getUserEmail() {
        return userEmail;
    }

    public static String getUserType() {
        return userType;
    }

    public static void clearSession() {
        userId = 0;
        userName = null;
        userEmail = null;
        userType = null;
    }
}