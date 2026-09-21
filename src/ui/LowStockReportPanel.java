package ui;

import model.Medicine;
import service.MedicineService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class LowStockReportPanel extends JPanel {

    private final MedicineService medicineService = new MedicineService();
    private final DefaultTableModel tableModel;
    private final JTextField searchField = new JTextField(18);

    // Constructor for low stock report panel
    public LowStockReportPanel() {

        // Set frame properties
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Init table for outputs
        tableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Stock Qty", "Reorder Level", "Supplier ID"}, 0) { @Override public boolean isCellEditable(int row, int column) { return false; } };
        JTable table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Init search bar
        JButton searchButton = new JButton("Search");
        JButton showAllButton = new JButton("Show All");
        Theme.stylePrimaryButton(searchButton);
        Theme.stylePrimaryButton(showAllButton);
        searchButton.addActionListener(e -> search());
        showAllButton.addActionListener(e -> refresh());

        // Init new panel for label and buttons
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Search Name:"));
        topPanel.add(searchField);
        topPanel.add(searchButton);
        topPanel.add(showAllButton);
        add(topPanel, BorderLayout.NORTH);

        // Init data load
        refresh();
    }

    // Refreshes table with latest call
    private void refresh() {

        populateTable(medicineService.getLowStockMedicines());

    }

    // Filters low stock list by medicine name
    private void search() {

        String query = searchField.getText().trim().toLowerCase();
        if (query.isEmpty()) { refresh(); return; }
        populateTable(medicineService.getLowStockMedicines().stream().filter(m -> m.getName().toLowerCase().contains(query)).toList());

    }

    // Populates table with given medicine list
    private void populateTable(List<Medicine> medicines) {

        // Clear table
        tableModel.setRowCount(0);

        // Add data to table
        for (Medicine m : medicines) { tableModel.addRow(new Object[]{m.getMedicineId(), m.getName(), m.getQuantityInStock(), m.getReorderLevel(), m.getSupplierId()}); }

    }

}
