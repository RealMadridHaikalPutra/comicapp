package com.comicapp.comicapp.web.controller;

import com.comicapp.comicapp.model.Comic;
import com.comicapp.comicapp.model.Purchase;
import com.comicapp.comicapp.service.ComicService;
import com.comicapp.comicapp.service.PurchaseService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controller MVC untuk panel admin: CRUD komik dan laporan penjualan.
 */
@Controller
public class AdminController extends BaseController {

    @Autowired
    private ComicService comicService;

    @Autowired
    private PurchaseService purchaseService;

    @GetMapping("/admin")
    public String comics(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        model.addAttribute("comics", comicService.getAllComics());
        return "admin/comics";
    }

    @GetMapping("/admin/comics/new")
    public String createForm(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        model.addAttribute("comic", new Comic());
        model.addAttribute("mode", "Tambah");
        return "admin/comic-form";
    }

    @GetMapping("/admin/comics/{id}/edit")
    public String editForm(@PathVariable Long id, HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        Comic comic = comicService.getComicById(id).orElse(null);
        if (comic == null) {
            return "redirect:/admin";
        }
        model.addAttribute("comic", comic);
        model.addAttribute("mode", "Ubah");
        return "admin/comic-form";
    }

    @PostMapping("/admin/comics")
    public String create(@RequestParam String title,
                         @RequestParam String genre,
                         @RequestParam String authorOrIllustrator,
                         @RequestParam(required = false) String coverUrl,
                         @RequestParam String price,
                         @RequestParam Integer stock,
                         @RequestParam(required = false) String synopsis,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        Comic comic = new Comic();
        if (!applyForm(comic, title, genre, authorOrIllustrator, coverUrl, price, stock, synopsis, redirectAttributes)) {
            return "redirect:/admin/comics/new";
        }
        comicService.saveComic(comic);
        redirectAttributes.addFlashAttribute("message", "Data komik berhasil disimpan!");
        return "redirect:/admin";
    }

    @PostMapping("/admin/comics/{id}")
    public String update(@PathVariable Long id,
                         @RequestParam String title,
                         @RequestParam String genre,
                         @RequestParam String authorOrIllustrator,
                         @RequestParam(required = false) String coverUrl,
                         @RequestParam String price,
                         @RequestParam Integer stock,
                         @RequestParam(required = false) String synopsis,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        Comic comic = comicService.getComicById(id).orElse(null);
        if (comic == null) {
            redirectAttributes.addFlashAttribute("error", "Komik tidak ditemukan.");
            return "redirect:/admin";
        }
        if (!applyForm(comic, title, genre, authorOrIllustrator, coverUrl, price, stock, synopsis, redirectAttributes)) {
            return "redirect:/admin/comics/" + id + "/edit";
        }
        comicService.saveComic(comic);
        redirectAttributes.addFlashAttribute("message", "Data komik berhasil diperbarui!");
        return "redirect:/admin";
    }

    @PostMapping("/admin/comics/{id}/delete")
    public String delete(@PathVariable Long id,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        try {
            comicService.deleteComic(id);
            redirectAttributes.addFlashAttribute("message", "Komik berhasil dihapus!");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error",
                    "Komik tidak dapat dihapus karena sudah memiliki transaksi/favorit.");
        }
        return "redirect:/admin";
    }

    @GetMapping("/admin/sales")
    public String sales(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        List<Purchase> purchases = purchaseService.getAllPurchases();
        double total = purchases.stream()
                .filter(p -> p.getPrice() != null)
                .mapToDouble(Purchase::getPrice)
                .sum();
        model.addAttribute("purchases", purchases);
        model.addAttribute("total", total);
        return "admin/sales";
    }

    private boolean applyForm(Comic comic,
                              String title,
                              String genre,
                              String authorOrIllustrator,
                              String coverUrl,
                              String price,
                              Integer stock,
                              String synopsis,
                              RedirectAttributes redirectAttributes) {
        if (title == null || title.trim().isEmpty()
                || genre == null || genre.trim().isEmpty()
                || authorOrIllustrator == null || authorOrIllustrator.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error",
                    "Judul, genre, dan penulis/ilustrator wajib diisi!");
            return false;
        }
        double harga;
        try {
            harga = Double.parseDouble(price.trim());
            if (harga < 0) {
                throw new NumberFormatException();
            }
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error",
                    "Harga harus berupa angka dan tidak boleh negatif!");
            return false;
        }
        comic.setTitle(title.trim());
        comic.setGenre(genre.trim());
        comic.setAuthorOrIllustrator(authorOrIllustrator.trim());
        comic.setCoverUrl(coverUrl != null ? coverUrl.trim() : null);
        comic.setPrice(harga);
        comic.setStock(stock != null ? stock : 0);
        comic.setSynopsis(synopsis != null ? synopsis.trim() : null);
        return true;
    }
}
