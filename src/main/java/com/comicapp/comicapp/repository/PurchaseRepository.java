package com.comicapp.comicapp.repository;

import com.comicapp.comicapp.model.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    List<Purchase> findByUserIdOrderByPurchasedAtDesc(Long userId);

    List<Purchase> findAllByOrderByPurchasedAtDesc();

    Optional<Purchase> findByUserIdAndComicId(Long userId, Long comicId);

    boolean existsByUserIdAndComicId(Long userId, Long comicId);
}
