package com.comicapp.comicapp.model;

import jakarta.persistence.*;

@Entity
@Table(name = "chapters")
public class Chapter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "chapter_number", nullable = false)
    private Double chapterNumber;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(name = "content_path", columnDefinition = "TEXT")
    private String contentPath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comic_id", nullable = false)
    private Comic comic;

    public Chapter() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Double getChapterNumber() { return chapterNumber; }
    public void setChapterNumber(Double chapterNumber) { this.chapterNumber = chapterNumber; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContentPath() { return contentPath; }
    public void setContentPath(String contentPath) { this.contentPath = contentPath; }

    public Comic getComic() { return comic; }
    public void setComic(Comic comic) { this.comic = comic; }
}