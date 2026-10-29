package com.comicapp.comicapp;

import com.comicapp.comicapp.web.auth.LoginForm;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class ComicappApplication {

    public static void main(String[] args) {
        // PENTING: Menonaktifkan mode headless agar GUI Swing JFrame bisa muncul di layar
        ConfigurableApplicationContext context = new SpringApplicationBuilder(ComicappApplication.class)
                .headless(false)
                .run(args);

        // Membuka Form Login saat aplikasi pertama kali berjalan
        java.awt.EventQueue.invokeLater(() -> {
            LoginForm loginForm = context.getBean(LoginForm.class);
            loginForm.setVisible(true);
        });
    }
}