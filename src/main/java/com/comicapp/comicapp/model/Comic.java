package com.comicapp.comicapp.model;

import jakarta.persistence.*;

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

    @Column(name = "price")
    private Double price = 0.0;

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

    public Double getPrice() { return price != null ? price : 0.0; }
    public void setPrice(Double price) { this.price = price; }
}