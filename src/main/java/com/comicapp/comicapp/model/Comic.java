package com.comicapp.comicapp.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "comics")
public class Comic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String synopsis;

    @Column(nullable = false, length = 100)
    private String genre;

    @Column(name = "author_illustrator", nullable = false, length = 100)
    private String authorOrIllustrator;

    @Column(name = "cover_url")
    private String coverUrl;

    @OneToMany(mappedBy = "comic", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Chapter> chapters;

    public Comic() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSynopsis() { return synopsis; }
    public void setSynopsis(String synopsis) { this.synopsis = synopsis; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public String getAuthorOrIllustrator() { return authorOrIllustrator; }
    public void setAuthorOrIllustrator(String authorOrIllustrator) { this.authorOrIllustrator = authorOrIllustrator; }

    public String getCoverUrl() { return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }

    public List<Chapter> getChapters() { return chapters; }
    public void setChapters(List<Chapter> chapters) { this.chapters = chapters; }
}