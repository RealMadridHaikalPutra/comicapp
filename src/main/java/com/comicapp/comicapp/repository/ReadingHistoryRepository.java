package com.comicapp.comicapp.repository;

import com.comicapp.comicapp.model.ReadingHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReadingHistoryRepository extends JpaRepository<ReadingHistory, Long> {
    List<ReadingHistory> findByUserIdOrderByUpdatedAtDesc(Long userId);
    Optional<ReadingHistory> findByUserIdAndComicId(Long userId, Long comicId);
}