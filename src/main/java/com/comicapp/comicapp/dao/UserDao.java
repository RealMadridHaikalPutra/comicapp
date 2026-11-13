package com.comicapp.comicapp.dao;

import com.comicapp.comicapp.model.User;

import java.util.Optional;

/**
 * DAO (Data Access Object) untuk entity User.
 * Lapisan ini memisahkan logika bisnis (service) dari detail akses data (JPA).
 */
public interface UserDao {

    Optional<User> findById(Long id);

    Optional<User> findByUsername(String username);

    User save(User user);
}
