package com.comicapp.comicapp.service;

import com.comicapp.comicapp.model.Chapter;
import com.comicapp.comicapp.model.Comic;
import com.comicapp.comicapp.model.ReadingHistory;
import com.comicapp.comicapp.model.User;
import com.comicapp.comicapp.repository.ReadingHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReadingHistoryService {

    @Autowired
    private ReadingHistoryRepository historyRepository;

    public List<ReadingHistory> getHistoryByUserId(Long userId) {
        return historyRepository.findByUserIdOrderByUpdatedAtDesc(userId);
    }

    public void saveOrUpdateHistory(User user, Comic comic, Chapter chapter) {
        Optional<ReadingHistory> existingHistory = historyRepository.findByUserIdAndComicId(user.getId(), comic.getId());
        
        ReadingHistory history;
        if (existingHistory.isPresent()) {
            history = existingHistory.get();
            history.setLastChapter(chapter);
            history.setUpdatedAt(LocalDateTime.now());
        } else {
            history = new ReadingHistory();
            history.setUser(user);
            history.setComic(comic);
            history.setLastChapter(chapter);
            history.setUpdatedAt(LocalDateTime.now());
        }
        historyRepository.save(history);
    }
}