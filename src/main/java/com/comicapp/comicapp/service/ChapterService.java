package com.comicapp.comicapp.service;

import com.comicapp.comicapp.model.Chapter;
import com.comicapp.comicapp.repository.ChapterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChapterService {

    @Autowired
    private ChapterRepository chapterRepository;

    public List<Chapter> getChaptersByComicId(Long comicId) {
        return chapterRepository.findByComicIdOrderByChapterNumberAsc(comicId);
    }

    public Optional<Chapter> getChapterById(Long id) {
        return chapterRepository.findById(id);
    }

    public Chapter saveChapter(Chapter chapter) {
        return chapterRepository.save(chapter);
    }

    public void deleteChapter(Long id) {
        chapterRepository.deleteById(id);
    }
}