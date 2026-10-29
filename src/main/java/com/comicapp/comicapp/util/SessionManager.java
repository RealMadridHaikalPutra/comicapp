package com.comicapp.comicapp.util;

import com.comicapp.comicapp.model.User;

public class SessionManager {
    private static User loggedInUser;

    public static void setLoggedInUser(User user) {
        loggedInUser = user;
    }

    public static User getLoggedInUser() {
        return loggedInUser;
    }

    public static void logout() {
        loggedInUser = null;
    }
}