package com.comicapp.comicapp.dao;

import com.comicapp.comicapp.model.Purchase;

import java.util.List;
import java.util.Optional;

/**
 * DAO untuk entity Purchase (transaksi pembelian).
 */
public interface PurchaseDao {

    List<Purchase> findByUserIdOrderByPurchasedAtDesc(Long userId);

    List<Purchase> findAllByOrderByPurchasedAtDesc();

    Optional<Purchase> findByUserIdAndComicId(Long userId, Long comicId);

    boolean existsByUserIdAndComicId(Long userId, Long comicId);

    Purchase save(Purchase purchase);
}
