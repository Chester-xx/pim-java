package ui;

import model.ItemSalesSummary;
import service.SalesService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ItemWiseSalesReportPanel extends JPanel {

    private final SalesService salesService = new SalesService();
    private final DefaultTableModel tableModel;
    private final JTextField searchField = new JTextField(18);

    // Constructor for specific item sales report panel
    public ItemWiseSalesReportPanel() {

        // Set layout and border for the panel
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Init table model & add to panel
        tableModel = new DefaultTableModel(new Object[]{"Medicine", "Total Qty Sold", "Total Revenue"}, 0) { @Override public boolean isCellEditable(int row, int column) { return false; } };
        JTable table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Add search bar and top panel
        JButton searchButton = new JButton("Search");
        JButton showAllButton = new JButton("Show All");
        Theme.stylePrimaryButton(searchButton);
        Theme.stylePrimaryButton(showAllButton);
        searchButton.addActionListener(e -> search());
        showAllButton.addActionListener(e -> refresh());
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

        populateTable(salesService.getItemWiseSalesReport());
    
    }

    // Filters report by medicine name
    private void search() {

        String query = searchField.getText().trim().toLowerCase();
        if (query.isEmpty()) { refresh(); return; }
        populateTable(salesService.getItemWiseSalesReport().stream().filter(row -> row.getMedicineName().toLowerCase().contains(query)).toList());

    }

    // Populates table with given report data
    private void populateTable(List<ItemSalesSummary> report) {

        // Clear existing data
        tableModel.setRowCount(0);
        
        // Init table with data
        for (ItemSalesSummary row : report) { tableModel.addRow(new Object[]{ row.getMedicineName(), row.getTotalQuantitySold(), row.getTotalRevenue() }); }
    
    }

}
