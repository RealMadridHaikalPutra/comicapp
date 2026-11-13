package com.comicapp.comicapp.service;

import com.comicapp.comicapp.dao.UserDao;
import com.comicapp.comicapp.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    public User register(String username, String password, String role) {
        if (userDao.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username sudah terdaftar!");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(password); // Catatan: Plain text untuk kesederhanaan tugas kuliah/proyek awal
        user.setRole(role != null ? role : "USER");
        return userDao.save(user);
    }

    public User login(String username, String password) {
        Optional<User> userOpt = userDao.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getPassword().equals(password)) {
                return user;
            }
        }
        throw new RuntimeException("Username atau password salah!");
    }

    public Optional<User> getUserById(Long id) {
        return userDao.findById(id);
    }
}