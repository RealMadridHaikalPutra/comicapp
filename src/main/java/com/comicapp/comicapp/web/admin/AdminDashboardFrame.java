package com.comicapp.comicapp.web.admin;

import org.springframework.context.annotation.Lazy;
import jakarta.annotation.PostConstruct;
import com.comicapp.comicapp.model.Comic;
import com.comicapp.comicapp.model.Chapter;
import com.comicapp.comicapp.model.User;
import com.comicapp.comicapp.service.ComicService;
import com.comicapp.comicapp.service.ChapterService;
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
    private ChapterService chapterService;
    @Autowired
    private LoginForm loginForm;

    private JTable tableComic;
    private DefaultTableModel modelComic;

    public AdminDashboardFrame() {
        setTitle("Dashboard Admin - Pengelolaan Konten Komik");
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
        modelComic = new DefaultTableModel(new String[]{"ID", "Judul", "Genre", "Penulis/Ilustrator", "Sinopsis"}, 0);
        tableComic = new JTable(modelComic);
        add(new JScrollPane(tableComic), BorderLayout.CENTER);

        // Bottom Panel: CRUD Buttons
        JPanel panelButtons = new JPanel();
        JButton btnAddComic = new JButton("Tambah Komik");
        JButton btnEditComic = new JButton("Ubah Komik");
        JButton btnDeleteComic = new JButton("Hapus Komik");
        JButton btnManageChapter = new JButton("Kelola Chapter");

        panelButtons.add(btnAddComic);
        panelButtons.add(btnEditComic);
        panelButtons.add(btnDeleteComic);
        panelButtons.add(btnManageChapter);
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

        btnManageChapter.addActionListener(e -> {
            int row = tableComic.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Pilih komik terlebih dahulu untuk mengelola chapter!");
                return;
            }
            Long id = (Long) modelComic.getValueAt(row, 0);
            Comic comic = comicService.getComicById(id).orElse(null);
            if (comic != null) {
                showChapterManagementDialog(comic);
            }
        });
    }

    public void loadComicData() {
        modelComic.setRowCount(0);
        List<Comic> comics = comicService.getAllComics();
        for (Comic c : comics) {
            modelComic.addRow(new Object[]{c.getId(), c.getTitle(), c.getGenre(), c.getAuthorOrIllustrator(), c.getSynopsis()});
        }
    }

    private void showComicForm(Comic existingComic) {
        JDialog dialog = new JDialog(this, existingComic == null ? "Tambah Komik" : "Ubah Komik", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridLayout(6, 2, 5, 5));

        JTextField txtTitle = new JTextField(existingComic != null ? existingComic.getTitle() : "");
        JTextField txtGenre = new JTextField(existingComic != null ? existingComic.getGenre() : "");
        JTextField txtAuthor = new JTextField(existingComic != null ? existingComic.getAuthorOrIllustrator() : "");
        JTextField txtCover = new JTextField(existingComic != null ? existingComic.getCoverUrl() : "");
        JTextArea txtSynopsis = new JTextArea(existingComic != null ? existingComic.getSynopsis() : "");

        dialog.add(new JLabel(" Judul:"));
        dialog.add(txtTitle);
        dialog.add(new JLabel(" Genre:"));
        dialog.add(txtGenre);
        dialog.add(new JLabel(" Penulis/Ilustrator:"));
        dialog.add(txtAuthor);
        dialog.add(new JLabel(" Cover URL:"));
        dialog.add(txtCover);
        dialog.add(new JLabel(" Sinopsis:"));
        dialog.add(new JScrollPane(txtSynopsis));

        JButton btnSave = new JButton("Simpan");
        dialog.add(new JLabel(""));
        dialog.add(btnSave);

        btnSave.addActionListener(e -> {
            Comic comic = existingComic != null ? existingComic : new Comic();
            comic.setTitle(txtTitle.getText().trim());
            comic.setGenre(txtGenre.getText().trim());
            comic.setAuthorOrIllustrator(txtAuthor.getText().trim());
            comic.setCoverUrl(txtCover.getText().trim());
            comic.setSynopsis(txtSynopsis.getText().trim());

            comicService.saveComic(comic);
            JOptionPane.showMessageDialog(dialog, "Data komik berhasil disimpan!");
            dialog.dispose();
            loadComicData();
        });

        dialog.setVisible(true);
    }

    private void showChapterManagementDialog(Comic comic) {
        JDialog dialog = new JDialog(this, "Kelola Chapter - " + comic.getTitle(), true);
        dialog.setSize(550, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        DefaultTableModel chModel = new DefaultTableModel(new String[]{"ID", "No. Chapter", "Judul Chapter"}, 0);
        JTable chTable = new JTable(chModel);
        dialog.add(new JScrollPane(chTable), BorderLayout.CENTER);

        Runnable loadChapters = () -> {
            chModel.setRowCount(0);
            List<Chapter> chapters = chapterService.getChaptersByComicId(comic.getId());
            for (Chapter ch : chapters) {
                chModel.addRow(new Object[]{ch.getId(), ch.getChapterNumber(), ch.getTitle()});
            }
        };
        loadChapters.run();

        JPanel btnPanel = new JPanel();
        JButton btnAddCh = new JButton("Tambah Chapter");
        JButton btnDelCh = new JButton("Hapus Chapter");
        btnPanel.add(btnAddCh);
        btnPanel.add(btnDelCh);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        btnAddCh.addActionListener(e -> {
            String numStr = JOptionPane.showInputDialog(dialog, "Masukkan Nomor Chapter (contoh: 1 atau 1.5):");
            if (numStr == null) return;
            String title = JOptionPane.showInputDialog(dialog, "Masukkan Judul Chapter:");
            if (title == null) return;

            try {
                Double num = Double.parseDouble(numStr);
                Chapter ch = new Chapter();
                ch.setChapterNumber(num);
                ch.setTitle(title);
                ch.setComic(comic);
                chapterService.saveChapter(ch);
                JOptionPane.showMessageDialog(dialog, "Chapter berhasil ditambahkan!");
                loadChapters.run();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Nomor chapter harus berupa angka!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnDelCh.addActionListener(e -> {
            int row = chTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(dialog, "Pilih chapter yang ingin dihapus!");
                return;
            }
            Long chId = (Long) chModel.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(dialog, "Yakin ingin menghapus chapter ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                chapterService.deleteChapter(chId);
                JOptionPane.showMessageDialog(dialog, "Chapter berhasil dihapus!");
                loadChapters.run();
            }
        });

        dialog.setVisible(true);
    }
}