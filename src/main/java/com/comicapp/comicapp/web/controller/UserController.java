package com.comicapp.comicapp.web.controller;

import com.comicapp.comicapp.model.Comic;
import com.comicapp.comicapp.model.User;
import com.comicapp.comicapp.service.ComicService;
import com.comicapp.comicapp.service.FavoriteService;
import com.comicapp.comicapp.service.PurchaseService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller MVC untuk aksi pembeli: pembelian dan favorit.
 */
@Controller
public class UserController extends BaseController {

    @Autowired
    private ComicService comicService;

    @Autowired
    private PurchaseService purchaseService;

    @Autowired
    private FavoriteService favoriteService;

    @GetMapping("/purchases")
    public String purchases(HttpSession session, Model model) {
        User user = currentUser(session);
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("purchases", purchaseService.getPurchasesByUserId(user.getId()));
        return "purchases";
    }

    @GetMapping("/favorites")
    public String favorites(HttpSession session, Model model) {
        User user = currentUser(session);
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("comics", favoriteService.getFavoriteComicsByUserId(user.getId()));
        return "favorites";
    }

    @PostMapping("/comics/{id}/buy")
    public String buy(@PathVariable Long id,
                      HttpSession session,
                      RedirectAttributes redirectAttributes) {
        User user = currentUser(session);
        if (user == null) {
            return "redirect:/login";
        }
        Comic comic = comicService.getComicById(id).orElse(null);
        if (comic == null) {
            redirectAttributes.addFlashAttribute("error", "Komik tidak ditemukan.");
            return "redirect:/catalog";
        }
        try {
            purchaseService.purchase(user, comic);
            redirectAttributes.addFlashAttribute("message", "Pembelian berhasil!");
            return "redirect:/purchases";
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/comics/" + id;
        }
    }

    @PostMapping("/favorites/{id}/add")
    public String addFavorite(@PathVariable Long id,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        User user = currentUser(session);
        if (user == null) {
            return "redirect:/login";
        }
        Comic comic = comicService.getComicById(id).orElse(null);
        if (comic != null) {
            favoriteService.addFavorite(user, comic);
            redirectAttributes.addFlashAttribute("message", "Komik ditambahkan ke favorit!");
        }
        return "redirect:/comics/" + id;
    }

    @PostMapping("/favorites/{id}/remove")
    public String removeFavorite(@PathVariable Long id,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        User user = currentUser(session);
        if (user == null) {
            return "redirect:/login";
        }
        favoriteService.removeFavorite(user.getId(), id);
        redirectAttributes.addFlashAttribute("message", "Komik dihapus dari favorit!");
        return "redirect:/favorites";
    }
}
