package com.comicapp.comicapp.dao.impl;

import com.comicapp.comicapp.dao.ComicDao;
import com.comicapp.comicapp.model.Comic;
import com.comicapp.comicapp.repository.ComicRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ComicDaoImpl implements ComicDao {

    private final ComicRepository comicRepository;

    public ComicDaoImpl(ComicRepository comicRepository) {
        this.comicRepository = comicRepository;
    }

    @Override
    public List<Comic> findAll() {
        return comicRepository.findAll();
    }

    @Override
    public Optional<Comic> findById(Long id) {
        return comicRepository.findById(id);
    }

    @Override
    public Comic save(Comic comic) {
        return comicRepository.save(comic);
    }

    @Override
    public void deleteById(Long id) {
        comicRepository.deleteById(id);
    }

    @Override
    public List<Comic> findByTitleContainingIgnoreCase(String title) {
        return comicRepository.findByTitleContainingIgnoreCase(title);
    }

    @Override
    public List<Comic> findByGenreContainingIgnoreCase(String genre) {
        return comicRepository.findByGenreContainingIgnoreCase(genre);
    }
}
