package com.comicapp.comicapp.service;

import com.comicapp.comicapp.model.Comic;
import com.comicapp.comicapp.model.Purchase;
import com.comicapp.comicapp.model.User;
import com.comicapp.comicapp.repository.PurchaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PurchaseService {

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Transactional(readOnly = true)
    public List<Purchase> getPurchasesByUserId(Long userId) {
        return purchaseRepository.findByUserIdOrderByPurchasedAtDesc(userId);
    }

    @Transactional(readOnly = true)
    public List<Purchase> getAllPurchases() {
        return purchaseRepository.findAllByOrderByPurchasedAtDesc();
    }

    @Transactional(readOnly = true)
    public boolean hasPurchased(Long userId, Long comicId) {
        return purchaseRepository.existsByUserIdAndComicId(userId, comicId);
    }

    @Transactional
    public Purchase purchase(User user, Comic comic) {
        if (purchaseRepository.existsByUserIdAndComicId(user.getId(), comic.getId())) {
            throw new RuntimeException("Komik ini sudah pernah Anda beli!");
        }

        Purchase purchase = new Purchase();
        purchase.setUser(user);
        purchase.setComic(comic);
        purchase.setPrice(comic.getPrice());
        purchase.setPurchasedAt(LocalDateTime.now());
        return purchaseRepository.save(purchase);
    }
}
