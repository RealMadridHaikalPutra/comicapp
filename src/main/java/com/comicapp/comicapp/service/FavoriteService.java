package com.comicapp.comicapp.service;

import com.comicapp.comicapp.dao.FavoriteDao;
import com.comicapp.comicapp.model.Comic;
import com.comicapp.comicapp.model.Favorite;
import com.comicapp.comicapp.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoriteService {

    @Autowired
    private FavoriteDao favoriteDao;

    public List<Comic> getFavoriteComicsByUserId(Long userId) {
        List<Favorite> favorites = favoriteDao.findByUserId(userId);
        return favorites.stream().map(Favorite::getComic).collect(Collectors.toList());
    }

    public void addFavorite(User user, Comic comic) {
        if (!favoriteDao.existsByUserIdAndComicId(user.getId(), comic.getId())) {
            Favorite favorite = new Favorite();
            favorite.setUser(user);
            favorite.setComic(comic);
            favoriteDao.save(favorite);
        }
    }

    @Transactional
    public void removeFavorite(Long userId, Long comicId) {
        favoriteDao.deleteByUserIdAndComicId(userId, comicId);
    }

    public boolean isFavorite(Long userId, Long comicId) {
        return favoriteDao.existsByUserIdAndComicId(userId, comicId);
    }
}