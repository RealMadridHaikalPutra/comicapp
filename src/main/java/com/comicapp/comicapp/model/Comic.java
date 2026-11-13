package com.comicapp.comicapp.model;

import jakarta.persistence.*;

import java.util.ArrayList;
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

    @Column(name = "price")
    private Double price = 0.0;

    @Column(name = "stock")
    private Integer stock = 0;

    // Relasi ORM dua arah ke tabel favorit dan pembelian.
    @OneToMany(mappedBy = "comic", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Favorite> favorites = new ArrayList<>();

    @OneToMany(mappedBy = "comic", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Purchase> purchases = new ArrayList<>();

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

    public Integer getStock() { return stock != null ? stock : 0; }
    public void setStock(Integer stock) { this.stock = stock; }

    public List<Favorite> getFavorites() { return favorites; }
    public void setFavorites(List<Favorite> favorites) { this.favorites = favorites; }

    public List<Purchase> getPurchases() { return purchases; }
    public void setPurchases(List<Purchase> purchases) { this.purchases = purchases; }
}