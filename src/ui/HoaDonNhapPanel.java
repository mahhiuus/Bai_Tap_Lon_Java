package ui;

import dao.HoaDonNhapDAO;
import dao.ChiTietHoaDonNhapDAO;
import dao.HoaDonNhapDAO;
import dao.NhaCungCapDAO;
import dao.NhanVienDAO;
import dao.SanPhamDAO;
import model.ChiTietHoaDonNhap;
import model.HoaDonNhap;
import model.NhaCungCap;
import model.NhanVien;
import model.SanPham;
import model.TaiKhoan;
import com.toedter.calendar.JDateChooser;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.Font;
import java.awt.event.*;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HoaDonNhapPanel extends JPanel {
    private JTextField txtMaHDN, txtTongTien, txtSearch;
    private JComboBox<String> cbNhaCungCap, cbNhanVien;
    private JDateChooser dateChooser;
    private DefaultTableModel tableModel;
    private JTable table;
    private HoaDonNhapDAO dao;
    private TaiKhoan currentUser;

    // --- CẬP NHẬT: Biến phân trang và Nhãn hiển thị Tổng tiền trang ---
    private List<HoaDonNhap> allData;
    private PhanTrangPanel phanTrang;
    private JLabel lblTongTienTrang;

    public HoaDonNhapPanel(TaiKhoan user) {
        this.currentUser = user;
        dao = new HoaDonNhapDAO();
        setLayout(new BorderLayout(20, 20));
        setBackground(LuxuryTheme.CREAM);
        setBorder(new EmptyBorder(30, 40, 30, 40));

        JLabel lblHeader = new JLabel("Quản Lý Hóa Đơn Nhập Hàng");
        lblHeader.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 28));
        lblHeader.setForeground(LuxuryTheme.NAVY);
        add(lblHeader, BorderLayout.NORTH);

        add(createFormPanel(), BorderLayout.WEST);
        add(createTableAndSearchPanel(), BorderLayout.CENTER);

        refreshForm();
    }

    private JPanel createFormPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(LuxuryTheme.CREAM);
        form.setPreferredSize(new Dimension(380, 0));
        form.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(LuxuryTheme.NAVY), "Thông tin Phiếu Nhập",
            TitledBorder.LEFT, TitledBorder.TOP, new java.awt.Font("Arial", java.awt.Font.BOLD, 14), LuxuryTheme.NAVY));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.insets = new Insets(10, 10, 10, 10); gbc.weightx = 1.0;

        int y = 0;
        form.add(createLabel("Mã Hóa Đơn Nhập:"), gbc); gbc.gridy = ++y;
        txtMaHDN = LuxuryTheme.createTextField(); 
        txtMaHDN.setEditable(false); 
        txtMaHDN.setBackground(new Color(240, 240, 240));
        form.add(txtMaHDN, gbc);

        gbc.gridy = ++y; form.add(createLabel("Nhà Cung Cấp:"), gbc); gbc.gridy = ++y;
        cbNhaCungCap = new JComboBox<>(); 
        cbNhaCungCap.setBackground(Color.WHITE);
        form.add(cbNhaCungCap, gbc);

        gbc.gridy = ++y; form.add(createLabel("Nhân Viên Nhập:"), gbc); gbc.gridy = ++y;
        cbNhanVien = new JComboBox<>(); 
        cbNhanVien.setBackground(Color.WHITE);
        form.add(cbNhanVien, gbc);

        loadDataToComboBox();

        gbc.gridy = ++y; form.add(createLabel("Ngày Nhập:"), gbc); gbc.gridy = ++y;
        dateChooser = new JDateChooser(); 
        dateChooser.setDateFormatString("dd/MM/yyyy");
        dateChooser.setDate(new java.util.Date()); 
        form.add(dateChooser, gbc);

        gbc.gridy = ++y; form.add(createLabel("Tổng Tiền (Hệ thống tự tính):"), gbc); gbc.gridy = ++y;
        txtTongTien = LuxuryTheme.createTextField(); 
        txtTongTien.setText("0");
        txtTongTien.setEditable(false); 
        txtTongTien.setForeground(Color.RED);
        form.add(txtTongTien, gbc);

        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        btnPanel.setOpaque(false);
        JButton btnAdd = LuxuryTheme.createButton("Tạo Vỏ", LuxuryTheme.TEAL, Color.WHITE);
        
        JButton btnDelete = LuxuryTheme.createButton("Xóa Vỏ", new Color(192, 57, 43), Color.WHITE);
        if (currentUser != null && !currentUser.getVaiTro().equals("ADMIN")) {
            btnDelete.setVisible(false); 
        }

        JButton btnNhapHang = LuxuryTheme.createButton("NHẬP CHI TIẾT", LuxuryTheme.GOLD, LuxuryTheme.NAVY);
        JButton btnInPDF = LuxuryTheme.createButton("IN PDF", LuxuryTheme.NAVY, LuxuryTheme.GOLD);

        // --- CẬP NHẬT: NÚT MỚI ---
        JButton btnClear = LuxuryTheme.createButton("Mới", Color.GRAY, Color.WHITE);

        btnAdd.addActionListener(e -> {
            try {
                HoaDonNhap hdn = taoHoaDonNhapTuForm();
                dao.themHoaDonNhap(hdn);
                refreshForm();
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage()); }
        });

        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) return;
            dao.xoaHoaDonNhap(txtMaHDN.getText()); 
            refreshForm();
        });

        btnNhapHang.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Vui lòng Chọn 1 Phiếu ở bảng để Nhập hàng!"); return; }
            HoaDonNhap hdn = taoHoaDonNhapTuForm();
            Window w = SwingUtilities.getWindowAncestor(this);
            ChiTietHoaDonNhapDialog d = new ChiTietHoaDonNhapDialog((Frame)w, hdn);
            d.setVisible(true);
            refreshForm(); 
        });

        btnInPDF.addActionListener(e -> inPhieuNhapPDF());

        // LOGIC NÚT MỚI
        btnClear.addActionListener(e -> { refreshForm(); table.clearSelection(); });

        btnPanel.add(btnAdd); btnPanel.add(btnDelete); btnPanel.add(btnNhapHang); btnPanel.add(btnInPDF); btnPanel.add(btnClear);
        gbc.gridy = ++y; gbc.insets = new Insets(20, 10, 10, 10); form.add(btnPanel, gbc);
        return form;
    }

    private void inPhieuNhapPDF() {
        int r = table.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 phiếu nhập để in!");
            return;
        }

        String maHDN = table.getValueAt(r, 0).toString();
        String maNCC = table.getValueAt(r, 1).toString();
        String maNV = table.getValueAt(r, 2).toString();
        String ngayNhapStr = table.getValueAt(r, 3).toString();
        String tongTienStr = table.getValueAt(r, 4).toString();

        NhaCungCap ncc = new NhaCungCapDAO().timTheoMaNhaCungCap(maNCC);
        NhanVien nv = new NhanVienDAO().timTheoMaNhanVien(maNV);
        List<ChiTietHoaDonNhap> chiTiets = new ChiTietHoaDonNhapDAO().getChiTietTheoMaHDN(maHDN);

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn nơi lưu Phiếu Nhập PDF");
        fileChooser.setSelectedFile(new File(maHDN + "_PhieuNhap.pdf"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".pdf")) {
                file = new File(file.getParentFile(), file.getName() + ".pdf");
            }

            try {
                Document document = new Document(PageSize.A4);
                PdfWriter.getInstance(document, new FileOutputStream(file));
                document.open();

                BaseFont bf = BaseFont.createFont("C:/Windows/Fonts/arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(bf, 16, com.itextpdf.text.Font.BOLD);
                com.itextpdf.text.Font headerFont = new com.itextpdf.text.Font(bf, 12, com.itextpdf.text.Font.BOLD);
                com.itextpdf.text.Font normalFont = new com.itextpdf.text.Font(bf, 11, com.itextpdf.text.Font.NORMAL);

                Paragraph title = new Paragraph("PHIẾU NHẬP HÀNG", titleFont);
                title.setAlignment(Element.ALIGN_CENTER);
                document.add(title);
                document.add(new Paragraph(" ", normalFont));

                Paragraph company = new Paragraph("BILLIARDS CLUB CENTER - LUXURY EDITION", headerFont);
                company.setAlignment(Element.ALIGN_CENTER);
                document.add(company);
                document.add(new Paragraph("Địa chỉ: 123 Đường Cầu Giấy, Q. Cầu Giấy, Hà Nội", normalFont));
                document.add(new Paragraph("Hotline: 0988.888.888", normalFont));
                document.add(new Paragraph(" ", normalFont));
                document.add(new Paragraph("Thông tin phiếu nhập:", headerFont));

                PdfPTable infoTable = new PdfPTable(2);
                infoTable.setWidthPercentage(100);
                infoTable.setSpacingBefore(8);
                infoTable.setWidths(new float[]{2f, 5f});
                infoTable.addCell(new Phrase("Mã HDN:", normalFont));
                infoTable.addCell(new Phrase(maHDN, normalFont));
                infoTable.addCell(new Phrase("Nhà cung cấp:", normalFont));
                infoTable.addCell(new Phrase(ncc != null ? ncc.getTenCongTy() + " (" + maNCC + ")" : maNCC, normalFont));
                infoTable.addCell(new Phrase("Nhân viên nhập:", normalFont));
                infoTable.addCell(new Phrase(nv != null ? nv.getTenNV() + " (" + maNV + ")" : maNV, normalFont));
                infoTable.addCell(new Phrase("Ngày nhập:", normalFont));
                infoTable.addCell(new Phrase(ngayNhapStr, normalFont));
                infoTable.addCell(new Phrase("Tổng tiền:", normalFont));
                infoTable.addCell(new Phrase(String.format("%,.0f VNĐ", Double.parseDouble(tongTienStr.replace(",", ""))), normalFont));
                document.add(infoTable);

                document.add(new Paragraph(" ", normalFont));
                document.add(new Paragraph("Chi tiết sản phẩm:", headerFont));

                PdfPTable pdfTable = new PdfPTable(5);
                pdfTable.setWidthPercentage(100);
                pdfTable.setSpacingBefore(8);
                pdfTable.setWidths(new float[]{2f, 4f, 2f, 2f, 3f});
                pdfTable.addCell(new Phrase("Mã SP", headerFont));
                pdfTable.addCell(new Phrase("Tên SP", headerFont));
                pdfTable.addCell(new Phrase("Số lượng", headerFont));
                pdfTable.addCell(new Phrase("Đơn giá", headerFont));
                pdfTable.addCell(new Phrase("Thành tiền", headerFont));

                double total = 0;
                SanPhamDAO spDao = new SanPhamDAO();
                for (ChiTietHoaDonNhap ct : chiTiets) {
                    String maSP = ct.getMaSP();
                    SanPham sp = spDao.layTheoId(maSP);
                    String tenSP = sp != null ? sp.getTenSP() : maSP;
                    double thanhTien = ct.getSoLuong() * ct.getDonGiaNhap();
                    total += thanhTien;

                    pdfTable.addCell(new Phrase(maSP, normalFont));
                    pdfTable.addCell(new Phrase(tenSP, normalFont));
                    pdfTable.addCell(new Phrase(String.valueOf(ct.getSoLuong()), normalFont));
                    pdfTable.addCell(new Phrase(String.format("%,.0f", ct.getDonGiaNhap()), normalFont));
                    pdfTable.addCell(new Phrase(String.format("%,.0f", thanhTien), normalFont));
                }
                document.add(pdfTable);

                document.add(new Paragraph(" ", normalFont));
                Paragraph totalParagraph = new Paragraph("Tổng tiền nhập: " + String.format("%,.0f VNĐ", total), headerFont);
                totalParagraph.setAlignment(Element.ALIGN_RIGHT);
                document.add(totalParagraph);

                document.add(new Paragraph(" ", normalFont));
                document.add(new Paragraph("Xin cảm ơn!", normalFont));

                document.close();
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(file);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi in PDF: " + ex.getMessage());
            }
        }
    }

    private void loadDataToComboBox() {
        cbNhaCungCap.removeAllItems();
        for (NhaCungCap ncc : new NhaCungCapDAO().getAllNhaCungCap()) cbNhaCungCap.addItem(ncc.getMaNCC() + " - " + ncc.getTenCongTy());
        cbNhanVien.removeAllItems();
        for (NhanVien nv : new NhanVienDAO().layTatCaNhanVien()) cbNhanVien.addItem(nv.getMaNV() + " - " + nv.getTenNV());
    }

    private HoaDonNhap taoHoaDonNhapTuForm() {
        HoaDonNhap hdn = new HoaDonNhap();
        hdn.setMaHDN(txtMaHDN.getText());
        hdn.setMaNCC(cbNhaCungCap.getSelectedItem().toString().split(" - ")[0]);
        hdn.setMaNV(cbNhanVien.getSelectedItem().toString().split(" - ")[0]);
        java.util.Date utilDate = dateChooser.getDate();
        if(utilDate != null) hdn.setNgayNhap(utilDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        return hdn;
    }

    private JPanel createTableAndSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setOpaque(false);
        searchPanel.add(createLabel("Tìm kiếm (Mã HĐN): "));
        txtSearch = LuxuryTheme.createTextField();
        txtSearch.setPreferredSize(new Dimension(250, 35));
        searchPanel.add(txtSearch);
        JButton btnSearch = LuxuryTheme.createButton("Tìm Kiếm", LuxuryTheme.GOLD, LuxuryTheme.NAVY);
        btnSearch.addActionListener(e -> loadData(dao.timKiemTheoMa(txtSearch.getText().trim())));
        searchPanel.add(btnSearch);
        panel.add(searchPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"Mã HĐN", "Mã NCC", "Mã NV", "Ngày Nhập", "Tổng Tiền"}, 0);
        table = new JTable(tableModel);
        table.setRowHeight(40); 
        table.getTableHeader().setBackground(LuxuryTheme.NAVY);
        table.getTableHeader().setForeground(LuxuryTheme.GOLD);
        table.getTableHeader().setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int r = table.getSelectedRow();
                txtMaHDN.setText(table.getValueAt(r, 0).toString());
                String maNCC = table.getValueAt(r, 1).toString();
                for (int i = 0; i < cbNhaCungCap.getItemCount(); i++) if (cbNhaCungCap.getItemAt(i).startsWith(maNCC)) { cbNhaCungCap.setSelectedIndex(i); break; }
                String maNV = table.getValueAt(r, 2).toString();
                for (int i = 0; i < cbNhanVien.getItemCount(); i++) if (cbNhanVien.getItemAt(i).startsWith(maNV)) { cbNhanVien.setSelectedIndex(i); break; }
                txtTongTien.setText(table.getValueAt(r, 4).toString().replace(",", ""));
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(LuxuryTheme.NAVY, 1));
        panel.add(scroll, BorderLayout.CENTER);

        // --- CẬP NHẬT: GÓI PHÂN TRANG VÀ NHÃN TỔNG TIỀN VÀO SOUTH ---
        JPanel pnlSouth = new JPanel(new BorderLayout());
        pnlSouth.setOpaque(false);

        phanTrang = new PhanTrangPanel(this::updateTableDisplay);
        pnlSouth.add(phanTrang, BorderLayout.CENTER);

        lblTongTienTrang = new JLabel("Tổng (Trang 1): 0 VNĐ", SwingConstants.RIGHT);
        lblTongTienTrang.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 20));
        lblTongTienTrang.setForeground(Color.RED);
        lblTongTienTrang.setBorder(new EmptyBorder(5, 0, 5, 10)); // Canh lề phải cho đẹp
        pnlSouth.add(lblTongTienTrang, BorderLayout.SOUTH);

        panel.add(pnlSouth, BorderLayout.SOUTH);

        return panel;
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        lbl.setForeground(LuxuryTheme.NAVY);
        return lbl;
    }

    private void refreshForm() {
        txtMaHDN.setText(dao.sinhMaMoi());
        txtTongTien.setText("0");
        if (txtSearch != null) txtSearch.setText("");
        dateChooser.setDate(new java.util.Date());
        
        loadData(dao.getAllHoaDonNhap());
    }

    private void loadData(List<HoaDonNhap> data) {
        allData = data;
        phanTrang.setTotalItems(allData.size());
        updateTableDisplay();
    }

    // --- CẬP NHẬT: LOGIC HIỂN THỊ DỮ LIỆU BẢNG & TÍNH TỔNG TIỀN ---
    private void updateTableDisplay() {
        tableModel.setRowCount(0);
        double tongTienTrang = 0;

        if (allData == null || allData.isEmpty()) {
            lblTongTienTrang.setText("Tổng (Trang 1): 0 VNĐ");
            return;
        }

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        int start = phanTrang.getStartIndex();
        int end = phanTrang.getEndIndex();

        for (int i = start; i < end; i++) {
            HoaDonNhap hdn = allData.get(i);
            String ngayNhap = hdn.getNgayNhap() != null ? hdn.getNgayNhap().format(fmt) : "";
            tableModel.addRow(new Object[]{hdn.getMaHDN(), hdn.getMaNCC(), hdn.getMaNV(), ngayNhap, String.format("%,.0f", hdn.getTongTien())});
            
            tongTienTrang += hdn.getTongTien();
        }

        lblTongTienTrang.setText("Tổng (Trang " + phanTrang.getCurrentPage() + "): " + String.format("%,.0f VNĐ", tongTienTrang));
    }
}