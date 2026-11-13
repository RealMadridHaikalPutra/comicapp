package com.comicapp.comicapp.repository;

import com.comicapp.comicapp.model.Purchase;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    // EntityGraph = fetch join ORM agar relasi user & comic diambil dalam satu query
    // (menghindari N+1) tanpa mengubah signature method yang sudah dipakai service.
    @EntityGraph(attributePaths = {"user", "comic"})
    List<Purchase> findByUserIdOrderByPurchasedAtDesc(Long userId);

    @EntityGraph(attributePaths = {"user", "comic"})
    List<Purchase> findAllByOrderByPurchasedAtDesc();

    Optional<Purchase> findByUserIdAndComicId(Long userId, Long comicId);

    boolean existsByUserIdAndComicId(Long userId, Long comicId);
}
