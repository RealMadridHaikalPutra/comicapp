package com.comicapp.comicapp.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "purchases")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relasi dimuat EAGER agar data pembeli & komik tetap bisa diakses
    // oleh Swing di luar transaksi database.
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "comic_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Comic comic;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "purchased_at", nullable = false)
    private LocalDateTime purchasedAt;
}
