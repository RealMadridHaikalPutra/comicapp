package com.comicapp.comicapp.web.auth;

import org.springframework.context.annotation.Lazy;
import org.springframework.beans.factory.ObjectProvider;
import com.comicapp.comicapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

@Component
@Lazy // <--- Tambahkan anotasi ini agar tidak dimuat terlalu dini
public class RegisterForm extends JFrame {

    @Autowired
    private UserService userService;

    @Autowired
    private ObjectProvider<LoginForm> loginFormProvider;

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<String> cbRole;
    private JButton btnRegister, btnBack;

    public RegisterForm() {
        setTitle("Registrasi Akun - Toko Komik Digital");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        setLayout(new GridLayout(5, 2, 10, 10));

        add(new JLabel("  Username:"));
        txtUsername = new JTextField();
        add(txtUsername);

        add(new JLabel("  Password:"));
        txtPassword = new JPasswordField();
        add(txtPassword);

        add(new JLabel("  Role (Peran):"));
        cbRole = new JComboBox<>(new String[]{"USER", "ADMIN"});
        add(cbRole);

        btnRegister = new JButton("Register");
        btnBack = new JButton("Kembali ke Login");
        add(btnBack);
        add(btnRegister);

        // Event Listener Register
        btnRegister.addActionListener(e -> {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String role = (String) cbRole.getSelectedItem();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Username dan Password tidak boleh kosong!",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        try {
            userService.register(username, password, role);

            JOptionPane.showMessageDialog(
                this,
                "Registrasi berhasil! Silakan login."
            );

            this.setVisible(false);
            loginFormProvider.getObject().setVisible(true);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this,
                ex.getMessage(),
                "Gagal Registrasi",
                JOptionPane.ERROR_MESSAGE
            );
        }
    });

        // Event Listener Tombol Kembali
        btnBack.addActionListener(e -> {
            this.setVisible(false);
            loginFormProvider.getObject().setVisible(true);
        });
    }
}