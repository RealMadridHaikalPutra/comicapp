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
@Lazy
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

        // Tabbed Pane
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

        modelCatalog = new DefaultTableModel(new String[]{"ID", "Judul", "Genre", "Penulis/Ilustrator", "Harga", "Stock", "Sinopsis"}, 0);
        tableCatalog = new JTable(modelCatalog);
        panelCatalog.add(new JScrollPane(tableCatalog), BorderLayout.CENTER);

        JPanel catalogActionPanel = new JPanel();
        JButton btnDetail   = new JButton("Lihat Detail");
        JButton btnBuy      = new JButton("Beli Komik");
        JButton btnFavorite = new JButton("Tambah ke Favorit");
        catalogActionPanel.add(btnDetail);
        catalogActionPanel.add(btnBuy);
        catalogActionPanel.add(btnFavorite);
        panelCatalog.add(catalogActionPanel, BorderLayout.SOUTH);
        tabbedPane.addTab("Katalog Komik", panelCatalog);

        // --- TAB 2: FAVORIT (WISHLIST) ---
        JPanel panelFavorite = new JPanel(new BorderLayout());
        modelFavorite = new DefaultTableModel(new String[]{"ID", "Judul", "Genre", "Penulis", "Harga", "Stock"}, 0);
        tableFavorite = new JTable(modelFavorite);
        panelFavorite.add(new JScrollPane(tableFavorite), BorderLayout.CENTER);
        JPanel favBottom = new JPanel();
        JButton btnBuyFromFav  = new JButton("Beli Komik");
        JButton btnRemoveFav   = new JButton("Hapus dari Favorit");
        favBottom.add(btnBuyFromFav);
        favBottom.add(btnRemoveFav);
        panelFavorite.add(favBottom, BorderLayout.SOUTH);
        tabbedPane.addTab("Komik Favorit", panelFavorite);

        // --- TAB 3: RIWAYAT PEMBELIAN ---
        JPanel panelPurchase = new JPanel(new BorderLayout());
        modelPurchase = new DefaultTableModel(new String[]{"ID", "Judul Komik", "Genre", "Harga Satuan", "Jumlah", "Total Harga", "Tanggal Pembelian"}, 0);
        tablePurchase = new JTable(modelPurchase);
        panelPurchase.add(new JScrollPane(tablePurchase), BorderLayout.CENTER);
        tabbedPane.addTab("Riwayat Pembelian", panelPurchase);

        add(tabbedPane, BorderLayout.CENTER);

        // Load data awal
        loadCatalogData(comicService.getAllComics());

        // ── Event Listeners ──────────────────────────────────────────────

        btnSearch.addActionListener(e -> {
            String keyword = txtSearch.getText();
            String genre   = (String) cbGenre.getSelectedItem();
            loadCatalogData(comicService.searchComics(keyword, genre));
        });

        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == 1) loadFavoriteData();
            else if (tabbedPane.getSelectedIndex() == 2) loadPurchaseData();
        });

        btnFavorite.addActionListener(e -> {
            int row = tableCatalog.getSelectedRow();
            if (row == -1) { JOptionPane.showMessageDialog(this, "Pilih komik terlebih dahulu!"); return; }
            Long comicId = (Long) modelCatalog.getValueAt(row, 0);
            Comic comic  = comicService.getComicById(comicId).orElse(null);
            if (comic != null && SessionManager.getLoggedInUser() != null) {
                favoriteService.addFavorite(SessionManager.getLoggedInUser(), comic);
                JOptionPane.showMessageDialog(this, "Komik berhasil ditambahkan ke daftar favorit!");
            }
        });

        btnRemoveFav.addActionListener(e -> {
            int row = tableFavorite.getSelectedRow();
            if (row == -1) { JOptionPane.showMessageDialog(this, "Pilih komik dari daftar favorit!"); return; }
            Long comicId = (Long) modelFavorite.getValueAt(row, 0);
            if (SessionManager.getLoggedInUser() != null) {
                favoriteService.removeFavorite(SessionManager.getLoggedInUser().getId(), comicId);
                JOptionPane.showMessageDialog(this, "Komik dihapus dari favorit!");
                loadFavoriteData();
            }
        });

        btnBuyFromFav.addActionListener(e -> {
            int row = tableFavorite.getSelectedRow();
            if (row == -1) { JOptionPane.showMessageDialog(this, "Pilih komik dari daftar favorit!"); return; }
            Long comicId = (Long) modelFavorite.getValueAt(row, 0);
            Comic comic  = comicService.getComicById(comicId).orElse(null);
            if (comic != null) beliKomik(comic);
        });

        btnBuy.addActionListener(e -> {
            int row = tableCatalog.getSelectedRow();
            if (row == -1) { JOptionPane.showMessageDialog(this, "Pilih komik yang ingin dibeli!"); return; }
            Long comicId = (Long) modelCatalog.getValueAt(row, 0);
            Comic comic  = comicService.getComicById(comicId).orElse(null);
            if (comic != null) beliKomik(comic);
        });

        btnDetail.addActionListener(e -> {
            int row = tableCatalog.getSelectedRow();
            if (row == -1) { JOptionPane.showMessageDialog(this, "Pilih komik yang ingin dilihat detailnya!"); return; }
            Long comicId = (Long) modelCatalog.getValueAt(row, 0);
            Comic comic  = comicService.getComicById(comicId).orElse(null);
            if (comic != null) showComicDetailDialog(comic);
        });

        btnLogout.addActionListener(e -> {
            SessionManager.logout();
            this.dispose();
            loginForm.setVisible(true);
        });
    }

    // ── Data loaders ─────────────────────────────────────────────────────

    public void loadCatalogData(List<Comic> comics) {
        modelCatalog.setRowCount(0);
        for (Comic c : comics) {
            modelCatalog.addRow(new Object[]{
                c.getId(), c.getTitle(), c.getGenre(),
                c.getAuthorOrIllustrator(), formatHarga(c.getPrice()),
                c.getStock(), c.getSynopsis()
            });
        }
    }

    public void loadFavoriteData() {
        modelFavorite.setRowCount(0);
        User user = SessionManager.getLoggedInUser();
        if (user != null) {
            for (Comic c : favoriteService.getFavoriteComicsByUserId(user.getId())) {
                modelFavorite.addRow(new Object[]{
                    c.getId(), c.getTitle(), c.getGenre(),
                    c.getAuthorOrIllustrator(), formatHarga(c.getPrice()), c.getStock()
                });
            }
        }
    }

    public void loadPurchaseData() {
        modelPurchase.setRowCount(0);
        User user = SessionManager.getLoggedInUser();
        if (user != null) {
            for (Purchase p : purchaseService.getPurchasesByUserId(user.getId())) {
                double total = p.getPrice() * p.getQuantity();
                modelPurchase.addRow(new Object[]{
                    p.getId(),
                    p.getComic().getTitle(),
                    p.getComic().getGenre(),
                    formatHarga(p.getPrice()),
                    p.getQuantity(),
                    formatHarga(total),
                    p.getPurchasedAt().toString()
                });
            }
        }
    }

    // ── Pembelian dengan input jumlah ─────────────────────────────────────

    /**
     * Menampilkan dialog konfirmasi pembelian dengan input jumlah (quantity).
     * Validasi: stock tersedia, qty >= 1, qty <= stock.
     */
    private void beliKomik(Comic comic) {
        User user = SessionManager.getLoggedInUser();
        if (user == null) return;

        // Ambil data comic terbaru agar stock selalu up-to-date
        Comic freshComic = comicService.getComicById(comic.getId()).orElse(comic);
        int stockTersedia = freshComic.getStock() != null ? freshComic.getStock() : 0;

        if (stockTersedia <= 0) {
            JOptionPane.showMessageDialog(this,
                "Maaf, stok komik \"" + freshComic.getTitle() + "\" sudah habis.",
                "Stok Habis", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // ── Dialog input jumlah ───────────────────────────────────────────
        JDialog dialogBeli = new JDialog(this, "Beli Komik", true);
        dialogBeli.setSize(420, 260);
        dialogBeli.setLocationRelativeTo(this);
        dialogBeli.setLayout(new BorderLayout(10, 10));

        // Info komik
        JPanel infoPanel = new JPanel(new GridLayout(4, 1, 4, 4));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(12, 16, 4, 16));
        infoPanel.add(new JLabel("Judul  : " + freshComic.getTitle()));
        infoPanel.add(new JLabel("Harga  : " + formatHarga(freshComic.getPrice()) + " / eksemplar"));
        infoPanel.add(new JLabel("Stock  : " + stockTersedia + " tersedia"));
        dialogBeli.add(infoPanel, BorderLayout.NORTH);

        // Input jumlah
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 6));
        inputPanel.add(new JLabel("Jumlah beli:"));
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(1, 1, stockTersedia, 1);
        JSpinner spinnerQty = new JSpinner(spinnerModel);
        spinnerQty.setPreferredSize(new Dimension(70, 26));
        inputPanel.add(spinnerQty);

        JLabel lblTotal = new JLabel("Total: " + formatHarga(freshComic.getPrice()));
        inputPanel.add(lblTotal);
        dialogBeli.add(inputPanel, BorderLayout.CENTER);

        // Update label total setiap kali qty berubah
        spinnerQty.addChangeListener(e -> {
            int qty   = (int) spinnerQty.getValue();
            double total = freshComic.getPrice() * qty;
            lblTotal.setText("Total: " + formatHarga(total));
        });

        // Tombol aksi
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        JButton btnKonfirmasi = new JButton("Konfirmasi Beli");
        JButton btnBatal      = new JButton("Batal");
        btnPanel.add(btnBatal);
        btnPanel.add(btnKonfirmasi);
        dialogBeli.add(btnPanel, BorderLayout.SOUTH);

        // ── Aksi tombol ───────────────────────────────────────────────────
        btnBatal.addActionListener(e -> dialogBeli.dispose());

        btnKonfirmasi.addActionListener(e -> {
            int qty   = (int) spinnerQty.getValue();
            double total = freshComic.getPrice() * qty;

            int confirm = JOptionPane.showConfirmDialog(
                dialogBeli,
                "Beli " + qty + " eksemplar \"" + freshComic.getTitle() + "\"\n"
                + "Total pembayaran: " + formatHarga(total) + "\n\nLanjutkan?",
                "Konfirmasi Pembelian",
                JOptionPane.YES_NO_OPTION);

            if (confirm != JOptionPane.YES_OPTION) return;

            try {
                purchaseService.purchase(user, freshComic, qty);   // <-- kirim qty
                JOptionPane.showMessageDialog(dialogBeli,
                    "Pembelian berhasil!\n"
                    + qty + " eksemplar \"" + freshComic.getTitle() + "\"\n"
                    + "Total: " + formatHarga(total) + "\n\n"
                    + "Komik tersedia di tab Riwayat Pembelian.",
                    "Pembelian Berhasil", JOptionPane.INFORMATION_MESSAGE);
                dialogBeli.dispose();
                loadCatalogData(comicService.getAllComics()); // refresh stock di tabel
                loadPurchaseData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialogBeli,
                    ex.getMessage(), "Pembelian Gagal", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialogBeli.setVisible(true);
    }

    // ── Detail komik ──────────────────────────────────────────────────────

    private void showComicDetailDialog(Comic comic) {
        JDialog dialog = new JDialog(this, "Detail Komik: " + comic.getTitle(), true);
        dialog.setSize(550, 420);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel infoPanel = new JPanel(new GridLayout(6, 1, 5, 5));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        infoPanel.add(new JLabel(" Judul              : " + comic.getTitle()));
        infoPanel.add(new JLabel(" Genre              : " + comic.getGenre()));
        infoPanel.add(new JLabel(" Penulis/Ilustrator : " + comic.getAuthorOrIllustrator()));
        infoPanel.add(new JLabel(" Harga              : " + formatHarga(comic.getPrice())));
        infoPanel.add(new JLabel(" Stock              : " + comic.getStock()));
        infoPanel.add(new JLabel(" Sinopsis           : " + comic.getSynopsis()));
        dialog.add(infoPanel, BorderLayout.NORTH);

        JPanel actionPanel = new JPanel();
        JButton btnBuyDetail = new JButton("Beli Komik");
        int stockSekarang = comic.getStock() != null ? comic.getStock() : 0;
        btnBuyDetail.setEnabled(stockSekarang > 0);
        actionPanel.add(btnBuyDetail);
        if (stockSekarang <= 0) actionPanel.add(new JLabel("Stok habis"));
        dialog.add(actionPanel, BorderLayout.SOUTH);

        btnBuyDetail.addActionListener(e -> {
            dialog.dispose(); // tutup detail dulu, baru buka dialog beli
            beliKomik(comic);
        });

        dialog.setVisible(true);
    }

    // ── Util ──────────────────────────────────────────────────────────────

    private static String formatHarga(Double harga) {
        double nilai = harga != null ? harga : 0.0;
        return "Rp " + String.format("%,.0f", nilai);
    }
}