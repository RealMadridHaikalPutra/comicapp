package com.comicapp.comicapp.dao;

import com.comicapp.comicapp.model.Comic;

import java.util.List;
import java.util.Optional;

/**
 * DAO untuk entity Comic (katalog komik).
 */
public interface ComicDao {

    List<Comic> findAll();

    Optional<Comic> findById(Long id);

    Comic save(Comic comic);

    void deleteById(Long id);

    List<Comic> findByTitleContainingIgnoreCase(String title);

    List<Comic> findByGenreContainingIgnoreCase(String genre);
}
