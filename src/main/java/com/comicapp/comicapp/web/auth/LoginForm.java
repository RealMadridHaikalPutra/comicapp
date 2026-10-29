package com.comicapp.comicapp.web.auth;

import org.springframework.context.annotation.Lazy;
import com.comicapp.comicapp.model.User;
import com.comicapp.comicapp.service.UserService;
import com.comicapp.comicapp.util.SessionManager;
import com.comicapp.comicapp.web.user.MainDashboardFrame;   // <--- Perhatikan import ini
import com.comicapp.comicapp.web.admin.AdminDashboardFrame; // <--- Perhatikan import ini
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

@Component
@Lazy // <--- Tambahkan anotasi ini agar tidak dimuat terlalu dini
public class LoginForm extends JFrame {

    @Autowired
    private UserService userService;

    @Autowired
    private RegisterForm registerForm;

    @Autowired
    private MainDashboardFrame mainDashboardFrame;

    @Autowired
    private AdminDashboardFrame adminDashboardFrame;

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin, btnToRegister;

    public LoginForm() {
        setTitle("Login - Aplikasi Baca Komik Digital");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        setLayout(new GridLayout(4, 2, 10, 10));

        add(new JLabel("  Username:"));
        txtUsername = new JTextField();
        add(txtUsername);

        add(new JLabel("  Password:"));
        txtPassword = new JPasswordField();
        add(txtPassword);

        btnToRegister = new JButton("Register Akun");
        btnLogin = new JButton("Login");
        
        add(btnToRegister);
        add(btnLogin);

        // Event Listener Tombol Login
        btnLogin.addActionListener(e -> {
            String username = txtUsername.getText().trim();
            String password = new String(txtPassword.getPassword()).trim();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username dan Password wajib diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                User user = userService.login(username, password);
                SessionManager.setLoggedInUser(user);
                
                JOptionPane.showMessageDialog(this, "Login Berhasil! Selamat datang, " + user.getUsername() + " (" + user.getRole() + ")");
                this.dispose();

                // Arahkan berdasarkan Role
                if (user.getRole().equalsIgnoreCase("ADMIN")) {
                    this.dispose();
                    adminDashboardFrame.setVisible(true);
                } else {
                    this.dispose();
                    mainDashboardFrame.setVisible(true);
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Login Gagal", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Event Listener Pindah ke Register
        btnToRegister.addActionListener(e -> {
            this.dispose();
            registerForm.setVisible(true);
        });
    }
}