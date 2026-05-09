package ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class NhaCungCapPanel extends JPanel {

    private JTable table;

    public NhaCungCapPanel() {

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JLabel title = new JLabel("QUẢN LÝ NHÀ CUNG CẤP");
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        add(title, BorderLayout.NORTH);

        // ===== FORM =====
        JPanel formPanel = new JPanel(new GridLayout(3,4,15,15));
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin nhà cung cấp"));

        JTextField txtMa = new JTextField();
        JTextField txtTen = new JTextField();
        JTextField txtSDT = new JTextField();
        JTextField txtDiaChi = new JTextField();

        formPanel.add(new JLabel("Mã NCC"));
        formPanel.add(txtMa);

        formPanel.add(new JLabel("Tên NCC"));
        formPanel.add(txtTen);

        formPanel.add(new JLabel("SĐT"));
        formPanel.add(txtSDT);

        formPanel.add(new JLabel("Địa chỉ"));
        formPanel.add(txtDiaChi);

        // ===== BUTTON =====
        JPanel buttonPanel = new JPanel();

        JButton btnThem = new JButton("Thêm");
        JButton btnSua = new JButton("Sửa");
        JButton btnXoa = new JButton("Xóa");
        JButton btnLamMoi = new JButton("Làm mới");

        buttonPanel.add(btnThem);
        buttonPanel.add(btnSua);
        buttonPanel.add(btnXoa);
        buttonPanel.add(btnLamMoi);

        // ===== TABLE =====
        String[] column = {
                "Mã NCC",
                "Tên nhà cung cấp",
                "SĐT",
                "Địa chỉ"
        };

        DefaultTableModel model = new DefaultTableModel(column,0);

        table = new JTable(model);

        JScrollPane scrollPane = new JScrollPane(table);

        JPanel centerPanel = new JPanel(new BorderLayout());

        centerPanel.add(formPanel, BorderLayout.NORTH);
        centerPanel.add(buttonPanel, BorderLayout.CENTER);
        centerPanel.add(scrollPane, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);
    }
}
