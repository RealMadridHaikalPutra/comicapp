package com.comicapp.comicapp.reports;

import com.comicapp.comicapp.model.Purchase;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Membuat laporan PDF riwayat pembelian memakai template laporan
 * reports/template_purchase.jrxml.
 */
public final class PurchaseReportPdf {

    private static final String TEMPLATE_PATH =
            "/com/comicapp/comicapp/reports/template_purchase.jrxml";

    private static JasperReport compiledReport;

    private PurchaseReportPdf() {
    }

    /**
     * Mengisi template laporan dengan data pembelian lalu menyimpannya sebagai file PDF.
     */
    public static void save(Component parent, List<Purchase> purchases) {
        if (purchases == null || purchases.isEmpty()) {
            JOptionPane.showMessageDialog(parent,
                    "Belum ada data pembelian untuk dibuat laporan.",
                    "Informasi", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        File target = chooseTargetFile(parent);
        if (target == null) {
            return;
        }

        try {
            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    loadReport(), new HashMap<>(), createDataSource(purchases));
            JasperExportManager.exportReportToPdfFile(jasperPrint, target.getAbsolutePath());
            JOptionPane.showMessageDialog(parent,
                    "Laporan PDF berhasil dibuat:\n" + target.getAbsolutePath(),
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(parent,
                    "Gagal membuat laporan PDF: " + ex.getMessage(),
                    "Error Laporan", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static File chooseTargetFile(Component parent) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Simpan Laporan Pembelian (PDF)");
        chooser.setFileFilter(new FileNameExtensionFilter("File PDF (*.pdf)", "pdf"));

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        chooser.setSelectedFile(new File("laporan_pembelian_" + timestamp + ".pdf"));

        if (chooser.showSaveDialog(parent) != JFileChooser.APPROVE_OPTION) {
            return null;
        }

        File file = chooser.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(".pdf")) {
            file = new File(file.getParentFile(), file.getName() + ".pdf");
        }

        if (file.exists()) {
            int confirm = JOptionPane.showConfirmDialog(parent,
                    "File \"" + file.getName() + "\" sudah ada. Timpa file tersebut?",
                    "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) {
                return null;
            }
        }
        return file;
    }

    private static synchronized JasperReport loadReport() throws JRException {
        if (compiledReport == null) {
            try (InputStream template = PurchaseReportPdf.class.getResourceAsStream(TEMPLATE_PATH)) {
                if (template == null) {
                    throw new JRException("Template laporan tidak ditemukan: " + TEMPLATE_PATH);
                }
                compiledReport = JasperCompileManager.compileReport(template);
            } catch (IOException ex) {
                throw new JRException("Gagal membaca template laporan", ex);
            }
        }
        return compiledReport;
    }

    /** Field mengikuti nama field pada template_purchase.jrxml. */
    private static JRMapCollectionDataSource createDataSource(List<Purchase> purchases) {
        List<Map<String, ?>> rows = new ArrayList<>();
        for (Purchase p : purchases) {
            Map<String, Object> row = new HashMap<>();
            row.put("id", p.getComic() != null ? p.getComic().getId() : p.getId());
            row.put("title", p.getComic() != null ? p.getComic().getTitle() : "-");
            row.put("author_illustrator",
                    p.getComic() != null ? p.getComic().getAuthorOrIllustrator() : "-");
            row.put("price", p.getPrice() != null ? p.getPrice() : 0.0);
            row.put("quantity", p.getQuantity() != null ? p.getQuantity() : 0);
            row.put("user_id", p.getUser() != null ? p.getUser().getId() : null);
            rows.add(row);
        }
        return new JRMapCollectionDataSource(rows);
    }
}
