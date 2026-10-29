package com.comicapp.comicapp.repository;

import com.comicapp.comicapp.model.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    
    List<Favorite> findByUserId(Long userId);
    
    boolean existsByUserIdAndComicId(Long userId, Long comicId); // <--- Perhatikan spasinya di sini
    
    void deleteByUserIdAndComicId(Long userId, Long comicId);
}