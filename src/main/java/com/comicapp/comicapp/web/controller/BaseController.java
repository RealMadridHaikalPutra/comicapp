package com.comicapp.comicapp.web.controller;

import com.comicapp.comicapp.model.User;
import com.comicapp.comicapp.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Base controller MVC: helper untuk membaca user yang sedang login dari session.
 */
public abstract class BaseController {

    @Autowired
    protected UserService userService;

    protected User currentUser(HttpSession session) {
        Object id = session.getAttribute("userId");
        if (id instanceof Long userId) {
            return userService.getUserById(userId).orElse(null);
        }
        return null;
    }

    protected boolean isAdmin(HttpSession session) {
        return "ADMIN".equalsIgnoreCase(String.valueOf(session.getAttribute("role")));
    }
}
