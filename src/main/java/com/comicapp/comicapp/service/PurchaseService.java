package com.comicapp.comicapp.service;

import com.comicapp.comicapp.dao.PurchaseDao;
import com.comicapp.comicapp.model.Comic;
import com.comicapp.comicapp.model.Purchase;
import com.comicapp.comicapp.model.User;
import com.comicapp.comicapp.service.ComicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PurchaseService {

    @Autowired
    private PurchaseDao purchaseDao;

    @Autowired
    private ComicService comicService;

    @Transactional(readOnly = true)
    public List<Purchase> getPurchasesByUserId(Long userId) {
        return purchaseDao.findByUserIdOrderByPurchasedAtDesc(userId);
    }

    @Transactional(readOnly = true)
    public List<Purchase> getAllPurchases() {
        return purchaseDao.findAllByOrderByPurchasedAtDesc();
    }

    @Transactional(readOnly = true)
    public boolean hasPurchased(Long userId, Long comicId) {
        return purchaseDao.existsByUserIdAndComicId(userId, comicId);
    }

    /**
     * Beli 1 eksemplar (backward-compatible, memanggil purchase(user, comic, 1))
     */
    @Transactional
    public Purchase purchase(User user, Comic comic) {
        return purchase(user, comic, 1);
    }

    /**
     * Beli sejumlah qty eksemplar.
     * - Validasi stok mencukupi
     * - Kurangi stok di tabel comic
     * - Simpan record Purchase dengan quantity & total harga
     */
    @Transactional
    public Purchase purchase(User user, Comic comic, int qty) {
        if (qty <= 0) {
            throw new RuntimeException("Jumlah pembelian harus lebih dari 0!");
        }

        // Ambil data comic terbaru langsung dari DB agar stok akurat
        Comic freshComic = comicService.getComicById(comic.getId())
                .orElseThrow(() -> new RuntimeException("Komik tidak ditemukan!"));

        int stockSekarang = freshComic.getStock() != null ? freshComic.getStock() : 0;

        if (stockSekarang <= 0) {
            throw new RuntimeException("Stok komik \"" + freshComic.getTitle() + "\" sudah habis!");
        }

        if (qty > stockSekarang) {
            throw new RuntimeException(
                "Stok tidak mencukupi! Tersedia: " + stockSekarang + " eksemplar.");
        }

        // Kurangi stok
        freshComic.setStock(stockSekarang - qty);
        comicService.saveComic(freshComic);

        // Simpan transaksi pembelian
        Purchase purchase = new Purchase();
        purchase.setUser(user);
        purchase.setComic(freshComic);
        purchase.setQuantity(qty);
        purchase.setPrice(freshComic.getPrice());   // harga satuan
        purchase.setPurchasedAt(LocalDateTime.now());
        return purchaseDao.save(purchase);
    }
}