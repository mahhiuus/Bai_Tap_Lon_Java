package ui;

import dao.ChiTietPhienDAO;
import dao.PhienChoiDAO;
import dao.SanPhamDAO;
import model.ChiTietPhien;
import model.PhienChoi;
import model.SanPham;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.List;

public class BanHangPanel extends JPanel {
    private JPanel gridPanel;
    private JComboBox<String> cbPhienChoi;
    private DefaultTableModel tableModel;
    private JLabel lblTongTien;
    
    private SanPhamDAO spDao = new SanPhamDAO();
    private PhienChoiDAO phienDao = new PhienChoiDAO();
    private ChiTietPhienDAO ctDao = new ChiTietPhienDAO();

    public BanHangPanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(LuxuryTheme.CREAM);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblHeader = new JLabel("Menu Dịch Vụ & Gọi Đồ");
        lblHeader.setFont(new Font("Arial", Font.BOLD, 28));
        lblHeader.setForeground(LuxuryTheme.NAVY);
        add(lblHeader, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(createProductGrid());
        splitPane.setRightComponent(createOrderPanel());
        splitPane.setDividerLocation(650);
        splitPane.setOpaque(false);
        splitPane.setBorder(null);

        add(splitPane, BorderLayout.CENTER);
        refreshPhienChoi();
    }

    private JPanel createProductGrid() {
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);

        gridPanel = new JPanel(new GridLayout(0, 3, 15, 15)); // Lưới 3 cột
        gridPanel.setOpaque(false);
        
        List<SanPham> dsSP = spDao.getAllSanPham();
        for (SanPham sp : dsSP) {
            gridPanel.add(createProductCard(sp));
        }

        JScrollPane scroll = new JScrollPane(gridPanel);
        scroll.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(LuxuryTheme.NAVY), "Danh Sách Sản Phẩm", 0, 0, 
            new Font("Arial", Font.BOLD, 16), LuxuryTheme.NAVY));
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        wrap.add(scroll, BorderLayout.CENTER);
        return wrap;
    }

    private JPanel createProductCard(SanPham sp) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        JLabel lblImg = new JLabel();
        lblImg.setHorizontalAlignment(SwingConstants.CENTER);
        String imgPath = sp.getHinhAnh();
        if (imgPath != null && !imgPath.trim().isEmpty()) {
            File f = new File(imgPath);
            if (f.exists() && !f.isDirectory()) {
                ImageIcon icon = new ImageIcon(new ImageIcon(imgPath).getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH));
                lblImg.setIcon(icon);
            } else { lblImg.setText("Chưa có ảnh"); }
        } else { lblImg.setText("Chưa có ảnh"); }
        
        lblImg.setPreferredSize(new Dimension(120, 130));
        card.add(lblImg, BorderLayout.CENTER);

        JPanel info = new JPanel(new GridLayout(3, 1, 0, 5));
        info.setBackground(Color.WHITE);
        info.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel name = new JLabel(sp.getTenSP(), SwingConstants.CENTER);
        name.setFont(new Font("Arial", Font.BOLD, 14));
        name.setForeground(LuxuryTheme.NAVY);

        JLabel price = new JLabel(String.format("%,.0f đ", sp.getGiaBan()), SwingConstants.CENTER);
        price.setForeground(Color.RED);
        price.setFont(new Font("Arial", Font.BOLD, 13));

        JButton btnAdd = LuxuryTheme.createButton("+ THÊM (1)", LuxuryTheme.TEAL, Color.WHITE);
        // Sự kiện: Bấm vào là ném thẳng xuống DB
        btnAdd.addActionListener(e -> themVaoBan(sp));

        info.add(name); info.add(price); info.add(btnAdd);
        card.add(info, BorderLayout.SOUTH);
        return card;
    }

    private JPanel createOrderPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(LuxuryTheme.GOLD), "Chi Tiết Bàn Đang Chọn", 0, 0, 
            new Font("Arial", Font.BOLD, 16), LuxuryTheme.NAVY));

        JPanel top = new JPanel(new BorderLayout()); top.setOpaque(false);
        top.add(new JLabel("  Chọn Bàn (Đang chơi): "), BorderLayout.WEST);
        cbPhienChoi = new JComboBox<>();
        cbPhienChoi.setFont(new Font("Arial", Font.PLAIN, 15));
        cbPhienChoi.addActionListener(e -> loadOrderDetails());
        top.add(cbPhienChoi, BorderLayout.CENTER);
        
        JButton btnRefresh = LuxuryTheme.createButton("🔄 Làm Mới", Color.GRAY, Color.WHITE);
        btnRefresh.addActionListener(e -> refreshPhienChoi());
        top.add(btnRefresh, BorderLayout.EAST);
        panel.add(top, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"Tên SP", "SL", "Đơn Giá", "Thành Tiền"}, 0);
        JTable table = new JTable(tableModel); table.setRowHeight(35);
        table.getTableHeader().setBackground(LuxuryTheme.NAVY); table.getTableHeader().setForeground(LuxuryTheme.GOLD);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        lblTongTien = new JLabel("Tổng Đồ Ăn: 0 VNĐ", SwingConstants.RIGHT);
        lblTongTien.setFont(new Font("Arial", Font.BOLD, 18));
        lblTongTien.setForeground(Color.RED);
        panel.add(lblTongTien, BorderLayout.SOUTH);

        return panel;
    }

    private void refreshPhienChoi() {
        cbPhienChoi.removeAllItems();
        List<PhienChoi> ds = phienDao.layTatCaPhien();
        for (PhienChoi p : ds) {
            if ("DANG_CHOI".equals(p.getTrangThaiPhien())) {
                cbPhienChoi.addItem(p.getMaPhien() + " - Bàn: " + p.getMaBan());
            }
        }
        if (cbPhienChoi.getItemCount() == 0) cbPhienChoi.addItem("--- Không có bàn nào đang chơi ---");
        loadOrderDetails();
    }

    private void themVaoBan(SanPham sp) {
        if (cbPhienChoi.getSelectedItem() == null || cbPhienChoi.getSelectedItem().toString().contains("Không có")) {
            JOptionPane.showMessageDialog(this, "Vui lòng mở một bàn chơi trước khi gọi đồ!"); return;
        }
        String maPhien = cbPhienChoi.getSelectedItem().toString().split(" - ")[0];
        
        // KIỂM TRA XEM MÓN NÀY ĐÃ CÓ TRONG BÀN CHƯA
        List<ChiTietPhien> dsCT = ctDao.timTheoMaPhien(maPhien);
        boolean isExist = false;
        
        for (ChiTietPhien ct : dsCT) {
            if (ct.getMaSanPham().equals(sp.getMaSP())) {
                ct.setSoLuong(ct.getSoLuong() + 1); // Cộng dồn số lượng
                ctDao.capNhatChiTietPhien(ct);
                isExist = true;
                break;
            }
        }
        
        // NẾU CHƯA CÓ THÌ THÊM DÒNG MỚI
        if (!isExist) {
            ChiTietPhien newCt = new ChiTietPhien();
            newCt.setMaChiTiet(ctDao.sinhMaMoi());
            newCt.setMaPhien(maPhien);
            newCt.setMaSanPham(sp.getMaSP());
            newCt.setSoLuong(1);
            newCt.setDonGia(sp.getGiaBan());
            ctDao.themChiTietPhien(newCt);
        }
        
        loadOrderDetails();
    }

    private void loadOrderDetails() {
        tableModel.setRowCount(0);
        if (cbPhienChoi.getSelectedItem() == null || cbPhienChoi.getSelectedItem().toString().contains("Không có")) {
            lblTongTien.setText("Tổng Đồ Ăn: 0 VNĐ"); return;
        }

        String maPhien = cbPhienChoi.getSelectedItem().toString().split(" - ")[0];
        List<ChiTietPhien> ds = ctDao.timTheoMaPhien(maPhien);
        
        double tongTien = 0;
        for (ChiTietPhien ct : ds) {
            SanPham sp = spDao.layTheoId(ct.getMaSanPham());
            String ten = sp != null ? sp.getTenSP() : ct.getMaSanPham();
            double thanhTien = ct.getSoLuong() * ct.getDonGia();
            tableModel.addRow(new Object[]{ten, ct.getSoLuong(), String.format("%,.0f", ct.getDonGia()), String.format("%,.0f", thanhTien)});
            tongTien += thanhTien;
        }
        lblTongTien.setText("Tổng Đồ Ăn: " + String.format("%,.0f VNĐ", tongTien));
    }
}