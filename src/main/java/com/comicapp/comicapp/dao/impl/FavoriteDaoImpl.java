package com.comicapp.comicapp.dao.impl;

import com.comicapp.comicapp.dao.FavoriteDao;
import com.comicapp.comicapp.model.Favorite;
import com.comicapp.comicapp.repository.FavoriteRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FavoriteDaoImpl implements FavoriteDao {

    private final FavoriteRepository favoriteRepository;

    public FavoriteDaoImpl(FavoriteRepository favoriteRepository) {
        this.favoriteRepository = favoriteRepository;
    }

    @Override
    public List<Favorite> findByUserId(Long userId) {
        return favoriteRepository.findByUserId(userId);
    }

    @Override
    public boolean existsByUserIdAndComicId(Long userId, Long comicId) {
        return favoriteRepository.existsByUserIdAndComicId(userId, comicId);
    }

    @Override
    public void deleteByUserIdAndComicId(Long userId, Long comicId) {
        favoriteRepository.deleteByUserIdAndComicId(userId, comicId);
    }

    @Override
    public Favorite save(Favorite favorite) {
        return favoriteRepository.save(favorite);
    }
}
