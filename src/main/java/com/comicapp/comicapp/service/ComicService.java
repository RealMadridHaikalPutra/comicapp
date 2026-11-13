package com.comicapp.comicapp.service;

import com.comicapp.comicapp.dao.ComicDao;
import com.comicapp.comicapp.model.Comic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ComicService {

    @Autowired
    private ComicDao comicDao;

    public List<Comic> getAllComics() {
        return comicDao.findAll();
    }

    public Optional<Comic> getComicById(Long id) {
        return comicDao.findById(id);
    }

    public Comic saveComic(Comic comic) {
        return comicDao.save(comic);
    }

    public void deleteComic(Long id) {
        comicDao.deleteById(id);
    }

    public List<Comic> searchComics(String keyword, String genre) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return comicDao.findByTitleContainingIgnoreCase(keyword.trim());
        } else if (genre != null && !genre.trim().isEmpty() && !genre.equalsIgnoreCase("Semua Genre")) {
            return comicDao.findByGenreContainingIgnoreCase(genre.trim());
        }
        return getAllComics();
    }
}