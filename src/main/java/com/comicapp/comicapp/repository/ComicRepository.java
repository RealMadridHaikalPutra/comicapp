package com.comicapp.comicapp.repository;

import com.comicapp.comicapp.model.Comic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ComicRepository extends JpaRepository<Comic, Long> {
    List<Comic> findByTitleContainingIgnoreCase(String title);
    List<Comic> findByGenreContainingIgnoreCase(String genre);
}