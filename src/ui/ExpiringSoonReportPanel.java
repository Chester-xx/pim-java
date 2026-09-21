package ui;

import model.Medicine;
import service.MedicineService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ExpiringSoonReportPanel extends JPanel {

    private final MedicineService medicineService = new MedicineService();
    private final DefaultTableModel tableModel;
    private final JTextField searchField = new JTextField(18);

    // Constructor to init expiry report panel
    public ExpiringSoonReportPanel() {

        // Set layout and border
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Initialize table model and jtable
        tableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Expiry Date", "Stock Qty", "Supplier ID"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Create search bar
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

        // Call refresh to load
        refresh();

    }

    // Method to refresh the table with expiring soon medicines
    private void refresh() {

        populateTable(medicineService.getExpiringSoonMedicines());
    
    }

    // Filters expiring soon list by medicine name
    private void search() {

        String query = searchField.getText().trim().toLowerCase();
        if (query.isEmpty()) { refresh(); return; }
        populateTable(medicineService.getExpiringSoonMedicines().stream().filter(m -> m.getName().toLowerCase().contains(query)).toList());

    }

    // Populates table with given medicine list
    private void populateTable(List<Medicine> medicines) {

        // Clear existing rows
        tableModel.setRowCount(0);
        
        // Init rows with data
        for (Medicine m : medicines) { tableModel.addRow(new Object[]{ m.getMedicineId(), m.getName(), m.getExpiryDate() != null ? m.getExpiryDate().toString() : "", m.getQuantityInStock(), m.getSupplierId() }); }
    
    }

}
