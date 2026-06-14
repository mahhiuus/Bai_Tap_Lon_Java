package ui;

import dao.ChiTietHoaDonNhapDAO;
import dao.SanPhamDAO;
import model.ChiTietHoaDonNhap;
import model.HoaDonNhap;
import model.SanPham;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ChiTietHoaDonNhapDialog extends JDialog {
    private JComboBox<String> cbSanPham;
    private JTextField txtSoLuong, txtDonGiaNhap;
    private JTable table;
    private DefaultTableModel tableModel;
    private HoaDonNhap hdn;
    private ChiTietHoaDonNhapDAO ctDao = new ChiTietHoaDonNhapDAO();
    private SanPhamDAO spDao = new SanPhamDAO();

    public ChiTietHoaDonNhapDialog(Frame parent, HoaDonNhap hdn) {
        super(parent, "Nhập Chi Tiết Hàng Hóa - Phiếu " + hdn.getMaHDN(), true);
        this.hdn = hdn;
        setSize(850, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JPanel pnlForm = new JPanel(new GridLayout(4, 2, 10, 15));
        pnlForm.setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(15, 20, 15, 20),
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(LuxuryTheme.NAVY), "Thông tin món hàng")
        ));
        pnlForm.setBackground(LuxuryTheme.CREAM);

        pnlForm.add(new JLabel("Chọn Sản Phẩm:"));
        cbSanPham = new JComboBox<>();
        List<SanPham> dsSp = spDao.getAllSanPham();
        for (SanPham sp : dsSp) cbSanPham.addItem(sp.getMaSP() + " - " + sp.getTenSP());
        pnlForm.add(cbSanPham);

        pnlForm.add(new JLabel("Số Lượng Nhập (Ví dụ: 50):"));
        txtSoLuong = LuxuryTheme.createTextField(); txtSoLuong.setText("1"); pnlForm.add(txtSoLuong);

        pnlForm.add(new JLabel("Đơn Giá Nhập Gốc (VNĐ):"));
        txtDonGiaNhap = LuxuryTheme.createTextField(); txtDonGiaNhap.setText("0"); pnlForm.add(txtDonGiaNhap);

        JButton btnAdd = LuxuryTheme.createButton("THÊM VÀO PHIẾU NHẬP", LuxuryTheme.TEAL, Color.WHITE);
        btnAdd.addActionListener(e -> themChiTiet());
        pnlForm.add(new JLabel("")); pnlForm.add(btnAdd);

        add(pnlForm, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"Mã CT", "Sản Phẩm", "Số Lượng", "Đơn Giá Nhập", "Thành Tiền"}, 0);
        table = new JTable(tableModel);
        table.setRowHeight(35);
        table.getTableHeader().setBackground(LuxuryTheme.NAVY); table.getTableHeader().setForeground(LuxuryTheme.GOLD);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlBottom.setBackground(LuxuryTheme.CREAM);
        JButton btnDelete = LuxuryTheme.createButton("XÓA MÓN BỊ NHẬP SAI", new Color(192, 57, 43), Color.WHITE);
        btnDelete.addActionListener(e -> xoaChiTiet());
        pnlBottom.add(btnDelete);
        add(pnlBottom, BorderLayout.SOUTH);

        loadTable();
    }

    private void themChiTiet() {
        try {
            // TỰ ĐỘNG LỌC DẤU CHẤM, DẤU PHẨY, KHOẢNG TRẮNG ĐỂ KHÔNG BỊ LỖI
            String textSL = txtSoLuong.getText().replace(",", "").replace(".", "").trim();
            String textGia = txtDonGiaNhap.getText().replace(",", "").replace(".", "").trim();

            int soLuong = ValidationUtils.parsePositiveInt(textSL, "Số lượng nhập");
            double donGia = ValidationUtils.parsePositiveMoney(textGia, "Đơn giá nhập");
            if (soLuong > 100000) {
                JOptionPane.showMessageDialog(this, "Số lượng nhập không được vượt quá 100,000!", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (cbSanPham.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm!", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String maSP = cbSanPham.getSelectedItem().toString().split(" - ")[0];

            ChiTietHoaDonNhap ct = new ChiTietHoaDonNhap("", hdn.getMaHDN(), maSP, soLuong, donGia);
            ctDao.themChiTiet(ct); 
            
            loadTable();
            txtSoLuong.setText("1"); txtDonGiaNhap.setText("0");
            
            // Hiển thị thông báo để người dùng an tâm
            JOptionPane.showMessageDialog(this, "Nhập hàng thành công! Đã tự động cộng " + soLuong + " sản phẩm vào kho.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            // In thẳng lỗi Database ra màn hình nếu có trục trặc
            JOptionPane.showMessageDialog(this, "Lỗi Database: " + ex.getMessage(), "Lỗi Hệ Thống", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xoaChiTiet() {
        int r = table.getSelectedRow();
        if (r < 0) { JOptionPane.showMessageDialog(this, "Vui lòng chọn món bị nhập sai để xóa!"); return; }
        String maCT = table.getValueAt(r, 0).toString();
        int xacNhan = JOptionPane.showConfirmDialog(this, "Hệ thống sẽ xóa dòng này và TỰ ĐỘNG THU HỒI lại số lượng đã cộng vào kho. Bạn chắc chứ?", "Cảnh báo Thu hồi kho", JOptionPane.YES_NO_OPTION);
        if (xacNhan == JOptionPane.YES_OPTION) {
            ctDao.xoaChiTiet(maCT); 
            loadTable();
        }
    }

    private void loadTable() {
        tableModel.setRowCount(0);
        List<ChiTietHoaDonNhap> list = ctDao.getChiTietTheoMaHDN(hdn.getMaHDN());
        for (ChiTietHoaDonNhap ct : list) {
            SanPham sp = spDao.layTheoId(ct.getMaSP());
            String ten = (sp != null) ? sp.getTenSP() : ct.getMaSP();
            tableModel.addRow(new Object[]{ ct.getMaChiTiet(), ten, ct.getSoLuong(), String.format("%,.0f", ct.getDonGiaNhap()), String.format("%,.0f", ct.getSoLuong() * ct.getDonGiaNhap()) });
        }
    }
}
