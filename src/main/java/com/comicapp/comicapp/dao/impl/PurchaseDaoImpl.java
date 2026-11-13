package com.comicapp.comicapp.dao.impl;

import com.comicapp.comicapp.dao.PurchaseDao;
import com.comicapp.comicapp.model.Purchase;
import com.comicapp.comicapp.repository.PurchaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PurchaseDaoImpl implements PurchaseDao {

    private final PurchaseRepository purchaseRepository;

    public PurchaseDaoImpl(PurchaseRepository purchaseRepository) {
        this.purchaseRepository = purchaseRepository;
    }

    @Override
    public List<Purchase> findByUserIdOrderByPurchasedAtDesc(Long userId) {
        return purchaseRepository.findByUserIdOrderByPurchasedAtDesc(userId);
    }

    @Override
    public List<Purchase> findAllByOrderByPurchasedAtDesc() {
        return purchaseRepository.findAllByOrderByPurchasedAtDesc();
    }

    @Override
    public Optional<Purchase> findByUserIdAndComicId(Long userId, Long comicId) {
        return purchaseRepository.findByUserIdAndComicId(userId, comicId);
    }

    @Override
    public boolean existsByUserIdAndComicId(Long userId, Long comicId) {
        return purchaseRepository.existsByUserIdAndComicId(userId, comicId);
    }

    @Override
    public Purchase save(Purchase purchase) {
        return purchaseRepository.save(purchase);
    }
}
