package ui;

import javax.swing.*;
import java.awt.*;

public class PhanTrangPanel extends JPanel {
    private int currentPage = 1;
    private int totalItems = 0;
    private int itemsPerPage = 10;

    private JLabel lblPageInfo;
    private JComboBox<Integer> cbItemsPerPage;
    private JButton btnPrev, btnNext;

    // Interface để kích hoạt sự kiện khi đổi trang
    private Runnable onPageChange;

    public PhanTrangPanel(Runnable onPageChange) {
        this.onPageChange = onPageChange;
        setLayout(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        setBackground(LuxuryTheme.CREAM);

        // Nút điều hướng
        btnPrev = LuxuryTheme.createButton("< Trước", LuxuryTheme.NAVY, Color.WHITE);
        btnNext = LuxuryTheme.createButton("Sau >", LuxuryTheme.NAVY, Color.WHITE);

        // Nhãn trang
        lblPageInfo = new JLabel("Trang 1 / 1");
        lblPageInfo.setFont(new Font("Arial", Font.BOLD, 14));
        lblPageInfo.setForeground(LuxuryTheme.NAVY);

        // Tùy chọn số dòng
        cbItemsPerPage = new JComboBox<>(new Integer[]{10, 20, 50});
        cbItemsPerPage.setFont(new Font("Arial", Font.PLAIN, 14));
        cbItemsPerPage.setBackground(Color.WHITE);

        // Bắt sự kiện
        btnPrev.addActionListener(e -> {
            if (currentPage > 1) { currentPage--; triggerChange(); }
        });

        btnNext.addActionListener(e -> {
            if (currentPage < getMaxPage()) { currentPage++; triggerChange(); }
        });

        cbItemsPerPage.addActionListener(e -> {
            itemsPerPage = (Integer) cbItemsPerPage.getSelectedItem();
            currentPage = 1; // Đổi số dòng thì quay về trang 1
            triggerChange();
        });

        // Add UI
        add(new JLabel("Hiển thị:"));
        add(cbItemsPerPage);
        add(btnPrev);
        add(lblPageInfo);
        add(btnNext);
    }

    // Nhận tổng số item để tính tổng số trang
    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
        if (currentPage > getMaxPage() && getMaxPage() > 0) currentPage = getMaxPage();
        else if (currentPage == 0 && totalItems > 0) currentPage = 1;
        updateLabel();
    }

    private int getMaxPage() { return Math.max(1, (int) Math.ceil((double) totalItems / itemsPerPage)); }

    private void updateLabel() {
        lblPageInfo.setText("Trang " + currentPage + " / " + getMaxPage());
        btnPrev.setEnabled(currentPage > 1);
        btnNext.setEnabled(currentPage < getMaxPage());
    }

    private void triggerChange() {
        updateLabel();
        if (onPageChange != null) onPageChange.run(); // Báo cho Panel cha biết để vẽ lại bảng
    }

    public int getStartIndex() { return (currentPage - 1) * itemsPerPage; }
    public int getEndIndex() { return Math.min(getStartIndex() + itemsPerPage, totalItems);}
        // --- THÊM ĐÚNG DÒNG NÀY VÀO LÀ HẾT LỖI ---
    public int getCurrentPage() { return currentPage; }
     
}