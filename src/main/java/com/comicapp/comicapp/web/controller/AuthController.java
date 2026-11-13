package com.comicapp.comicapp.web.controller;

import com.comicapp.comicapp.model.User;
import com.comicapp.comicapp.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller MVC untuk login, registrasi, dan logout (session-based).
 */
@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String loginForm(HttpSession session) {
        if (session.getAttribute("userId") != null) {
            return "redirect:/catalog";
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
        try {
            User user = userService.login(username, password);
            session.setAttribute("userId", user.getId());
            session.setAttribute("username", user.getUsername());
            session.setAttribute("role", user.getRole());
            return "redirect:/catalog";
        } catch (RuntimeException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("username", username);
            return "login";
        }
    }

    @GetMapping("/register")
    public String registerForm(HttpSession session) {
        if (session.getAttribute("userId") != null) {
            return "redirect:/catalog";
        }
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String password,
                           @RequestParam(defaultValue = "USER") String role,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        if (username == null || username.trim().isEmpty()
                || password == null || password.trim().isEmpty()) {
            model.addAttribute("error", "Username dan Password tidak boleh kosong!");
            model.addAttribute("username", username);
            return "register";
        }
        try {
            userService.register(username.trim(), password.trim(), role);
            redirectAttributes.addFlashAttribute("message", "Registrasi berhasil! Silakan login.");
            return "redirect:/login";
        } catch (RuntimeException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("username", username);
            return "register";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
