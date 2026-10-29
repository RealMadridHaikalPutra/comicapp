package com.comicapp.comicapp.service;

import com.comicapp.comicapp.model.Comic;
import com.comicapp.comicapp.repository.ComicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ComicService {

    @Autowired
    private ComicRepository comicRepository;

    public List<Comic> getAllComics() {
        return comicRepository.findAll();
    }

    public Optional<Comic> getComicById(Long id) {
        return comicRepository.findById(id);
    }

    public Comic saveComic(Comic comic) {
        return comicRepository.save(comic);
    }

    public void deleteComic(Long id) {
        comicRepository.deleteById(id);
    }

    public List<Comic> searchComics(String keyword, String genre) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return comicRepository.findByTitleContainingIgnoreCase(keyword.trim());
        } else if (genre != null && !genre.trim().isEmpty() && !genre.equalsIgnoreCase("Semua Genre")) {
            return comicRepository.findByGenreContainingIgnoreCase(genre.trim());
        }
        return getAllComics();
    }
}