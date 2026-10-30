package com.comicapp.comicapp.web.user;

import org.springframework.context.annotation.Lazy;
import jakarta.annotation.PostConstruct;
import com.comicapp.comicapp.model.Comic;
import com.comicapp.comicapp.model.Purchase;
import com.comicapp.comicapp.model.User;
import com.comicapp.comicapp.service.ComicService;
import com.comicapp.comicapp.service.FavoriteService;
import com.comicapp.comicapp.service.PurchaseService;
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
    private FavoriteService favoriteService;
    @Autowired
    private PurchaseService purchaseService;
    @Autowired
    private LoginForm loginForm;

    private JTable tableCatalog, tableFavorite, tablePurchase;
    private DefaultTableModel modelCatalog, modelFavorite, modelPurchase;
    private JTextField txtSearch;
    private JComboBox<String> cbGenre;
    private JTabbedPane tabbedPane;

    public MainDashboardFrame() {
        setTitle("Dashboard Pembeli - Toko Komik Digital");
        setSize(950, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    @PostConstruct
    private void initialize() {
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

        // Tabbed Pane (Katalog, Favorit, Pembelian)
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

        modelCatalog = new DefaultTableModel(new String[]{"ID", "Judul", "Genre", "Penulis/Ilustrator", "Harga", "Sinopsis"}, 0);
        tableCatalog = new JTable(modelCatalog);
        panelCatalog.add(new JScrollPane(tableCatalog), BorderLayout.CENTER);

        JPanel catalogActionPanel = new JPanel();
        JButton btnDetail = new JButton("Lihat Detail");
        JButton btnBuy = new JButton("Beli Komik");
        JButton btnFavorite = new JButton("Tambah ke Favorit");
        catalogActionPanel.add(btnDetail);
        catalogActionPanel.add(btnBuy);
        catalogActionPanel.add(btnFavorite);
        panelCatalog.add(catalogActionPanel, BorderLayout.SOUTH);
        tabbedPane.addTab("Katalog Komik", panelCatalog);

        // --- TAB 2: FAVORIT (WISHLIST) ---
        JPanel panelFavorite = new JPanel(new BorderLayout());
        modelFavorite = new DefaultTableModel(new String[]{"ID", "Judul", "Genre", "Penulis", "Harga"}, 0);
        tableFavorite = new JTable(modelFavorite);
        panelFavorite.add(new JScrollPane(tableFavorite), BorderLayout.CENTER);
        JPanel favBottom = new JPanel();
        JButton btnBuyFromFav = new JButton("Beli Komik");
        JButton btnRemoveFav = new JButton("Hapus dari Favorit");
        favBottom.add(btnBuyFromFav);
        favBottom.add(btnRemoveFav);
        panelFavorite.add(favBottom, BorderLayout.SOUTH);
        tabbedPane.addTab("Komik Favorit", panelFavorite);

        // --- TAB 3: RIWAYAT PEMBELIAN ---
        JPanel panelPurchase = new JPanel(new BorderLayout());
        modelPurchase = new DefaultTableModel(new String[]{"ID", "Judul Komik", "Genre", "Harga", "Tanggal Pembelian"}, 0);
        tablePurchase = new JTable(modelPurchase);
        panelPurchase.add(new JScrollPane(tablePurchase), BorderLayout.CENTER);
        tabbedPane.addTab("Riwayat Pembelian", panelPurchase);

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
                loadPurchaseData();
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

        btnBuyFromFav.addActionListener(e -> {
            int row = tableFavorite.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Pilih komik dari daftar favorit!");
                return;
            }
            Long comicId = (Long) modelFavorite.getValueAt(row, 0);
            Comic comic = comicService.getComicById(comicId).orElse(null);
            if (comic != null) {
                beliKomik(comic);
            }
        });

        btnBuy.addActionListener(e -> {
            int row = tableCatalog.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Pilih komik yang ingin dibeli!");
                return;
            }
            Long comicId = (Long) modelCatalog.getValueAt(row, 0);
            Comic comic = comicService.getComicById(comicId).orElse(null);
            if (comic != null) {
                beliKomik(comic);
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
            modelCatalog.addRow(new Object[]{c.getId(), c.getTitle(), c.getGenre(), c.getAuthorOrIllustrator(), formatHarga(c.getPrice()), c.getSynopsis()});
        }
    }

    public void loadFavoriteData() {
        modelFavorite.setRowCount(0);
        User user = SessionManager.getLoggedInUser();
        if (user != null) {
            List<Comic> favs = favoriteService.getFavoriteComicsByUserId(user.getId());
            for (Comic c : favs) {
                modelFavorite.addRow(new Object[]{c.getId(), c.getTitle(), c.getGenre(), c.getAuthorOrIllustrator(), formatHarga(c.getPrice())});
            }
        }
    }

    public void loadPurchaseData() {
        modelPurchase.setRowCount(0);
        User user = SessionManager.getLoggedInUser();
        if (user != null) {
            List<Purchase> purchases = purchaseService.getPurchasesByUserId(user.getId());
            for (Purchase p : purchases) {
                modelPurchase.addRow(new Object[]{
                    p.getId(),
                    p.getComic().getTitle(),
                    p.getComic().getGenre(),
                    formatHarga(p.getPrice()),
                    p.getPurchasedAt().toString()
                });
            }
        }
    }

    private void beliKomik(Comic comic) {
        User user = SessionManager.getLoggedInUser();
        if (user == null) {
            return;
        }

        if (purchaseService.hasPurchased(user.getId(), comic.getId())) {
            JOptionPane.showMessageDialog(this, "Komik ini sudah pernah Anda beli.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Beli komik \"" + comic.getTitle() + "\" seharga " + formatHarga(comic.getPrice()) + "?",
                "Konfirmasi Pembelian",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            purchaseService.purchase(user, comic);
            JOptionPane.showMessageDialog(this, "Pembelian berhasil!\nKomik tersedia pada tab Riwayat Pembelian.");
            loadPurchaseData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Pembelian Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showComicDetailDialog(Comic comic) {
        JDialog dialog = new JDialog(this, "Detail Komik: " + comic.getTitle(), true);
        dialog.setSize(550, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel infoPanel = new JPanel(new GridLayout(5, 1, 5, 5));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        infoPanel.add(new JLabel(" Judul: " + comic.getTitle()));
        infoPanel.add(new JLabel(" Genre: " + comic.getGenre()));
        infoPanel.add(new JLabel(" Penulis/Ilustrator: " + comic.getAuthorOrIllustrator()));
        infoPanel.add(new JLabel(" Harga: " + formatHarga(comic.getPrice())));
        infoPanel.add(new JLabel(" Sinopsis: " + comic.getSynopsis()));
        dialog.add(infoPanel, BorderLayout.NORTH);

        User user = SessionManager.getLoggedInUser();
        boolean sudahDibeli = user != null && purchaseService.hasPurchased(user.getId(), comic.getId());

        JPanel actionPanel = new JPanel();
        JButton btnBuy = new JButton("Beli Komik");
        btnBuy.setEnabled(!sudahDibeli);
        JLabel lblStatus = new JLabel(sudahDibeli ? "Sudah Anda beli" : "");
        actionPanel.add(lblStatus);
        actionPanel.add(btnBuy);
        dialog.add(actionPanel, BorderLayout.SOUTH);

        btnBuy.addActionListener(e -> {
            beliKomik(comic);
            btnBuy.setEnabled(false);
            lblStatus.setText("Sudah Anda beli");
        });

        dialog.setVisible(true);
    }

    private static String formatHarga(Double harga) {
        double nilai = harga != null ? harga : 0.0;
        return "Rp " + String.format("%,.0f", nilai);
    }
}
