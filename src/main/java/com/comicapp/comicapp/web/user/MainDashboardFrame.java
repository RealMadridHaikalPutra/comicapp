package com.comicapp.comicapp.web.user;

import org.springframework.context.annotation.Lazy;
import com.comicapp.comicapp.model.Comic;
import com.comicapp.comicapp.model.Chapter;
import com.comicapp.comicapp.model.ReadingHistory;
import com.comicapp.comicapp.model.User;
import com.comicapp.comicapp.service.ComicService;
import com.comicapp.comicapp.service.ChapterService;
import com.comicapp.comicapp.service.FavoriteService;
import com.comicapp.comicapp.service.ReadingHistoryService;
import com.comicapp.comicapp.web.auth.LoginForm;
import com.comicapp.comicapp.util.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

@Component
@Lazy // <--- Tambahkan anotasi ini agar tidak dimuat terlalu dini
public class MainDashboardFrame extends JFrame {

    @Autowired
    private ComicService comicService;
    @Autowired
    private ChapterService chapterService;
    @Autowired
    private FavoriteService favoriteService;
    @Autowired
    private ReadingHistoryService historyService;
    @Autowired
    private LoginForm loginForm;

    private JTable tableCatalog, tableFavorite, tableHistory;
    private DefaultTableModel modelCatalog, modelFavorite, modelHistory;
    private JTextField txtSearch;
    private JComboBox<String> cbGenre;
    private JTabbedPane tabbedPane;

    public MainDashboardFrame() {
        setTitle("Dashboard Pembaca - Aplikasi Baca Komik Digital");
        setSize(950, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Top Panel: Info User & Logout
        JPanel topPanel = new JPanel(new BorderLayout());
        User user = SessionManager.getLoggedInUser();
        JLabel lblWelcome = new JLabel("  Selamat datang, " + (user != null ? user.getUsername() : "User") + "!");
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 14));
        topPanel.add(lblWelcome, BorderLayout.WEST);

        JButton btnLogout = new JButton("Logout");
        topPanel.add(btnLogout, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Tabbed Pane (Katalog, Favorit, Riwayat)
        tabbedPane = new JTabbedPane();

        // --- TAB 1: KATALOG & PENCARIAN ---
        JPanel panelCatalog = new JPanel(new BorderLayout());
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtSearch = new JTextField(15);
        cbGenre = new JComboBox<>(new String[]{"Semua Genre", "Action", "Romance", "Fantasy", "Comedy", "Drama", "Sci-Fi"});
        JButton btnSearch = new JButton("Cari / Filter");
        searchPanel.add(new JLabel("Judul:"));
        searchPanel.add(txtSearch);
        searchPanel.add(new JLabel("Genre:"));
        searchPanel.add(cbGenre);
        searchPanel.add(btnSearch);
        panelCatalog.add(searchPanel, BorderLayout.NORTH);

        modelCatalog = new DefaultTableModel(new String[]{"ID", "Judul", "Genre", "Penulis/Ilustrator", "Sinopsis"}, 0);
        tableCatalog = new JTable(modelCatalog);
        panelCatalog.add(new JScrollPane(tableCatalog), BorderLayout.CENTER);

        JPanel catalogActionPanel = new JPanel();
        JButton btnDetail = new JButton("Lihat Detail & Chapter");
        JButton btnFavorite = new JButton("Tambah ke Favorit");
        catalogActionPanel.add(btnDetail);
        catalogActionPanel.add(btnFavorite);
        panelCatalog.add(catalogActionPanel, BorderLayout.SOUTH);
        tabbedPane.addTab("Katalog Komik", panelCatalog);

        // --- TAB 2: FAVORIT ---
        JPanel panelFavorite = new JPanel(new BorderLayout());
        modelFavorite = new DefaultTableModel(new String[]{"ID", "Judul", "Genre", "Penulis"}, 0);
        tableFavorite = new JTable(modelFavorite);
        panelFavorite.add(new JScrollPane(tableFavorite), BorderLayout.CENTER);
        JButton btnRemoveFav = new JButton("Hapus dari Favorit");
        JPanel favBottom = new JPanel();
        favBottom.add(btnRemoveFav);
        panelFavorite.add(favBottom, BorderLayout.SOUTH);
        tabbedPane.addTab("Komik Favorit", panelFavorite);

        // --- TAB 3: RIWAYAT MEMBACA ---
        JPanel panelHistory = new JPanel(new BorderLayout());
        modelHistory = new DefaultTableModel(new String[]{"ID", "Judul Komik", "Chapter Terakhir", "Terakhir Dibaca"}, 0);
        tableHistory = new JTable(modelHistory);
        panelHistory.add(new JScrollPane(tableHistory), BorderLayout.CENTER);
        tabbedPane.addTab("Riwayat Membaca", panelHistory);

        add(tabbedPane, BorderLayout.CENTER);

        // Load data awal
        loadCatalogData(comicService.getAllComics());

        // Event Listeners
        btnSearch.addActionListener(e -> {
            String keyword = txtSearch.getText();
            String genre = (String) cbGenre.getSelectedItem();
            List<Comic> results = comicService.searchComics(keyword, genre);
            loadCatalogData(results);
        });

        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == 1) {
                loadFavoriteData();
            } else if (tabbedPane.getSelectedIndex() == 2) {
                loadHistoryData();
            }
        });

        btnFavorite.addActionListener(e -> {
            int row = tableCatalog.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Pilih komik terlebih dahulu!");
                return;
            }
            Long comicId = (Long) modelCatalog.getValueAt(row, 0);
            Comic comic = comicService.getComicById(comicId).orElse(null);
            if (comic != null && SessionManager.getLoggedInUser() != null) {
                favoriteService.addFavorite(SessionManager.getLoggedInUser(), comic);
                JOptionPane.showMessageDialog(this, "Komik berhasil ditambahkan ke daftar favorit!");
            }
        });

        btnRemoveFav.addActionListener(e -> {
            int row = tableFavorite.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Pilih komik dari daftar favorit!");
                return;
            }
            Long comicId = (Long) modelFavorite.getValueAt(row, 0);
            if (SessionManager.getLoggedInUser() != null) {
                favoriteService.removeFavorite(SessionManager.getLoggedInUser().getId(), comicId);
                JOptionPane.showMessageDialog(this, "Komik dihapus dari favorit!");
                loadFavoriteData();
            }
        });

        btnDetail.addActionListener(e -> {
            int row = tableCatalog.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Pilih komik yang ingin dilihat detailnya!");
                return;
            }
            Long comicId = (Long) modelCatalog.getValueAt(row, 0);
            Comic comic = comicService.getComicById(comicId).orElse(null);
            if (comic != null) {
                showComicDetailDialog(comic);
            }
        });

        btnLogout.addActionListener(e -> {
            SessionManager.logout();
            this.dispose();
            loginForm.setVisible(true);
        });
    }

    public void loadCatalogData(List<Comic> comics) {
        modelCatalog.setRowCount(0);
        for (Comic c : comics) {
            modelCatalog.addRow(new Object[]{c.getId(), c.getTitle(), c.getGenre(), c.getAuthorOrIllustrator(), c.getSynopsis()});
        }
    }

    public void loadFavoriteData() {
        modelFavorite.setRowCount(0);
        User user = SessionManager.getLoggedInUser();
        if (user != null) {
            List<Comic> favs = favoriteService.getFavoriteComicsByUserId(user.getId());
            for (Comic c : favs) {
                modelFavorite.addRow(new Object[]{c.getId(), c.getTitle(), c.getGenre(), c.getAuthorOrIllustrator()});
            }
        }
    }

    public void loadHistoryData() {
        modelHistory.setRowCount(0);
        User user = SessionManager.getLoggedInUser();
        if (user != null) {
            List<ReadingHistory> histories = historyService.getHistoryByUserId(user.getId());
            for (ReadingHistory h : histories) {
                modelHistory.addRow(new Object[]{
                    h.getComic().getId(),
                    h.getComic().getTitle(),
                    "Chapter " + h.getLastChapter().getChapterNumber() + " - " + h.getLastChapter().getTitle(),
                    h.getUpdatedAt().toString()
                });
            }
        }
    }

    private void showComicDetailDialog(Comic comic) {
        JDialog dialog = new JDialog(this, "Detail Komik: " + comic.getTitle(), true);
        dialog.setSize(550, 450);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel infoPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        infoPanel.add(new JLabel(" Judul: " + comic.getTitle()));
        infoPanel.add(new JLabel(" Genre: " + comic.getGenre()));
        infoPanel.add(new JLabel(" Penulis/Ilustrator: " + comic.getAuthorOrIllustrator()));
        infoPanel.add(new JLabel(" Sinopsis: " + comic.getSynopsis()));
        dialog.add(infoPanel, BorderLayout.NORTH);

        List<Chapter> chapters = chapterService.getChaptersByComicId(comic.getId());
        DefaultTableModel chapterModel = new DefaultTableModel(new String[]{"ID", "No. Chapter", "Judul Chapter"}, 0);
        for (Chapter ch : chapters) {
            chapterModel.addRow(new Object[]{ch.getId(), ch.getChapterNumber(), ch.getTitle()});
        }
        JTable tableChapter = new JTable(chapterModel);
        dialog.add(new JScrollPane(tableChapter), BorderLayout.CENTER);

        JButton btnRead = new JButton("Baca Chapter Dipilih (Simpan ke Riwayat)");
        dialog.add(btnRead, BorderLayout.SOUTH);

        btnRead.addActionListener(e -> {
            int row = tableChapter.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(dialog, "Pilih chapter yang ingin dibaca!");
                return;
            }
            Long chId = (Long) chapterModel.getValueAt(row, 0);
            Chapter chapter = chapterService.getChapterById(chId).orElse(null);
            if (chapter != null && SessionManager.getLoggedInUser() != null) {
                historyService.saveOrUpdateHistory(SessionManager.getLoggedInUser(), comic, chapter);
                JOptionPane.showMessageDialog(dialog, "Membaca Chapter " + chapter.getChapterNumber() + "\nRiwayat membaca berhasil diperbarui otomatis!");
            }
        });

        dialog.setVisible(true);
    }
}