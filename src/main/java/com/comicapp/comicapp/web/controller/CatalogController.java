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
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Controller MVC untuk katalog komik (halaman publik).
 */
@Controller
public class CatalogController extends BaseController {

    private static final List<String> GENRES = List.of(
            "Semua Genre", "Action", "Romance", "Fantasy", "Comedy", "Drama", "Sci-Fi");

    @Autowired
    private ComicService comicService;

    @Autowired
    private PurchaseService purchaseService;

    @Autowired
    private FavoriteService favoriteService;

    @GetMapping({"/", "/catalog"})
    public String catalog(@RequestParam(required = false) String keyword,
                          @RequestParam(required = false) String genre,
                          Model model) {
        if (genre == null || genre.trim().isEmpty()) {
            genre = "Semua Genre";
        }
        model.addAttribute("comics", comicService.searchComics(keyword, genre));
        model.addAttribute("keyword", keyword);
        model.addAttribute("genre", genre);
        model.addAttribute("genres", GENRES);
        return "catalog";
    }

    @GetMapping("/comics/{id}")
    public String detail(@PathVariable Long id, HttpSession session, Model model) {
        Comic comic = comicService.getComicById(id).orElse(null);
        if (comic == null) {
            return "redirect:/catalog";
        }
        User user = currentUser(session);
        model.addAttribute("comic", comic);
        model.addAttribute("sudahDibeli", user != null
                && purchaseService.hasPurchased(user.getId(), comic.getId()));
        model.addAttribute("isFavorite", user != null
                && favoriteService.isFavorite(user.getId(), comic.getId()));
        return "detail";
    }
}
