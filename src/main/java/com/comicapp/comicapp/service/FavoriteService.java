package com.comicapp.comicapp.service;

import com.comicapp.comicapp.model.Comic;
import com.comicapp.comicapp.model.Favorite;
import com.comicapp.comicapp.model.User;
import com.comicapp.comicapp.repository.FavoriteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoriteService {

    @Autowired
    private FavoriteRepository favoriteRepository;

    public List<Comic> getFavoriteComicsByUserId(Long userId) {
        List<Favorite> favorites = favoriteRepository.findByUserId(userId);
        return favorites.stream().map(Favorite::getComic).collect(Collectors.toList());
    }

    public void addFavorite(User user, Comic comic) {
        if (!favoriteRepository.existsByUserIdAndComicId(user.getId(), comic.getId())) {
            Favorite favorite = new Favorite();
            favorite.setUser(user);
            favorite.setComic(comic);
            favoriteRepository.save(favorite);
        }
    }

    @Transactional
    public void removeFavorite(Long userId, Long comicId) {
        favoriteRepository.deleteByUserIdAndComicId(userId, comicId);
    }

    public boolean isFavorite(Long userId, Long comicId) {
        return favoriteRepository.existsByUserIdAndComicId(userId, comicId);
    }
}