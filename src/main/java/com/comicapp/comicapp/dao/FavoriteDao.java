package com.comicapp.comicapp.dao;

import com.comicapp.comicapp.model.Favorite;

import java.util.List;

/**
 * DAO untuk entity Favorite (wishlist pembeli).
 */
public interface FavoriteDao {

    List<Favorite> findByUserId(Long userId);

    boolean existsByUserIdAndComicId(Long userId, Long comicId);

    void deleteByUserIdAndComicId(Long userId, Long comicId);

    Favorite save(Favorite favorite);
}
