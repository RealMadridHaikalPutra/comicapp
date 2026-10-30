package com.comicapp.comicapp.web.admin;

import org.springframework.context.annotation.Lazy;
import jakarta.annotation.PostConstruct;
import com.comicapp.comicapp.model.Comic;
import com.comicapp.comicapp.model.Purchase;
import com.comicapp.comicapp.model.User;
import com.comicapp.comicapp.service.ComicService;
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
public class AdminDashboardFrame extends JFrame {

    @Autowired
    private ComicService comicService;
    @Autowired
    private PurchaseService purchaseService;
    @Autowired
    private LoginForm loginForm;

    private JTable tableComic;
    private DefaultTableModel modelComic;

    public AdminDashboardFrame() {
        setTitle("Dashboard Admin - Penjualan Komik Digital");
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    @PostConstruct
    private void initialize() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Top Panel
        JPanel topPanel = new JPanel(new BorderLayout());
        User user = SessionManager.getLoggedInUser();
        JLabel lblWelcome = new JLabel("  Admin Panel: " + (user != null ? user.getUsername() : "Admin"));
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 14));
        topPanel.add(lblWelcome, BorderLayout.WEST);

        JButton btnLogout = new JButton("Logout");
        topPanel.add(btnLogout, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Center Panel: Comic Table
        modelComic = new DefaultTableModel(new String[]{"ID", "Judul", "Genre", "Penulis/Ilustrator", "Harga", "Sinopsis"}, 0);
        tableComic = new JTable(modelComic);
        add(new JScrollPane(tableComic), BorderLayout.CENTER);

        // Bottom Panel: CRUD Buttons
        JPanel panelButtons = new JPanel();
        JButton btnAddComic = new JButton("Tambah Komik");
        JButton btnEditComic = new JButton("Ubah Komik");
        JButton btnDeleteComic = new JButton("Hapus Komik");
        JButton btnSales = new JButton("Data Penjualan");

        panelButtons.add(btnAddComic);
        panelButtons.add(btnEditComic);
        panelButtons.add(btnDeleteComic);
        panelButtons.add(btnSales);
        add(panelButtons, BorderLayout.SOUTH);

        loadComicData();

        // Event Listeners
        btnLogout.addActionListener(e -> {
            SessionManager.logout();
            this.dispose();
            loginForm.setVisible(true);
        });

        btnAddComic.addActionListener(e -> showComicForm(null));

        btnEditComic.addActionListener(e -> {
            int row = tableComic.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Pilih komik yang ingin diubah!");
                return;
            }
            Long id = (Long) modelComic.getValueAt(row, 0);
            Comic comic = comicService.getComicById(id).orElse(null);
            if (comic != null) {
                showComicForm(comic);
            }
        });

        btnDeleteComic.addActionListener(e -> {
            int row = tableComic.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Pilih komik yang ingin dihapus!");
                return;
            }
            Long id = (Long) modelComic.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus komik ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                comicService.deleteComic(id);
                JOptionPane.showMessageDialog(this, "Komik berhasil dihapus!");
                loadComicData();
            }
        });

        btnSales.addActionListener(e -> showSalesDialog());
    }

    public void loadComicData() {
        modelComic.setRowCount(0);
        List<Comic> comics = comicService.getAllComics();
        for (Comic c : comics) {
            modelComic.addRow(new Object[]{c.getId(), c.getTitle(), c.getGenre(), c.getAuthorOrIllustrator(), formatHarga(c.getPrice()), c.getSynopsis()});
        }
    }

    private void showComicForm(Comic existingComic) {
        JDialog dialog = new JDialog(this, existingComic == null ? "Tambah Komik" : "Ubah Komik", true);
        dialog.setSize(400, 380);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridLayout(7, 2, 5, 5));

        JTextField txtTitle = new JTextField(existingComic != null ? existingComic.getTitle() : "");
        JTextField txtGenre = new JTextField(existingComic != null ? existingComic.getGenre() : "");
        JTextField txtAuthor = new JTextField(existingComic != null ? existingComic.getAuthorOrIllustrator() : "");
        JTextField txtCover = new JTextField(existingComic != null ? existingComic.getCoverUrl() : "");
        JTextField txtPrice = new JTextField(existingComic != null ? String.valueOf(existingComic.getPrice()) : "");
        JTextArea txtSynopsis = new JTextArea(existingComic != null ? existingComic.getSynopsis() : "");

        dialog.add(new JLabel(" Judul:"));
        dialog.add(txtTitle);
        dialog.add(new JLabel(" Genre:"));
        dialog.add(txtGenre);
        dialog.add(new JLabel(" Penulis/Ilustrator:"));
        dialog.add(txtAuthor);
        dialog.add(new JLabel(" Cover URL:"));
        dialog.add(txtCover);
        dialog.add(new JLabel(" Harga (Rp):"));
        dialog.add(txtPrice);
        dialog.add(new JLabel(" Sinopsis:"));
        dialog.add(new JScrollPane(txtSynopsis));

        JButton btnSave = new JButton("Simpan");
        dialog.add(new JLabel(""));
        dialog.add(btnSave);

        btnSave.addActionListener(e -> {
            Comic comic = existingComic != null ? existingComic : new Comic();

            double price;
            try {
                price = Double.parseDouble(txtPrice.getText().trim());
                if (price < 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Harga harus berupa angka dan tidak boleh negatif!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            comic.setTitle(txtTitle.getText().trim());
            comic.setGenre(txtGenre.getText().trim());
            comic.setAuthorOrIllustrator(txtAuthor.getText().trim());
            comic.setCoverUrl(txtCover.getText().trim());
            comic.setPrice(price);
            comic.setSynopsis(txtSynopsis.getText().trim());

            comicService.saveComic(comic);
            JOptionPane.showMessageDialog(dialog, "Data komik berhasil disimpan!");
            dialog.dispose();
            loadComicData();
        });

        dialog.setVisible(true);
    }

    private void showSalesDialog() {
        JDialog dialog = new JDialog(this, "Data Penjualan Komik", true);
        dialog.setSize(700, 420);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        DefaultTableModel salesModel = new DefaultTableModel(new String[]{"ID", "Pembeli", "Judul Komik", "Harga", "Tanggal Pembelian"}, 0);
        JTable salesTable = new JTable(salesModel);
        dialog.add(new JScrollPane(salesTable), BorderLayout.CENTER);

        List<Purchase> purchases = purchaseService.getAllPurchases();
        double total = 0;
        for (Purchase p : purchases) {
            salesModel.addRow(new Object[]{
                p.getId(),
                p.getUser().getUsername(),
                p.getComic().getTitle(),
                formatHarga(p.getPrice()),
                p.getPurchasedAt().toString()
            });
            if (p.getPrice() != null) {
                total += p.getPrice();
            }
        }

        JLabel lblTotal = new JLabel("  Total Pendapatan: " + formatHarga(total) + "  (" + purchases.size() + " transaksi)");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 13));
        lblTotal.setBorder(BorderFactory.createEmptyBorder(8, 5, 8, 5));
        dialog.add(lblTotal, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private static String formatHarga(Double harga) {
        double nilai = harga != null ? harga : 0.0;
        return "Rp " + String.format("%,.0f", nilai);
    }
}
