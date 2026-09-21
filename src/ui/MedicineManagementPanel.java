package ui;

import model.Medicine;
import model.Supplier;
import service.MedicineService;
import service.SupplierService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MedicineManagementPanel extends JPanel {

    private final MedicineService medicineService = new MedicineService();
    private final SupplierService supplierService = new SupplierService();

    private final DefaultTableModel tableModel;
    private final JTable table;

    private final JTextField nameField = new JTextField(18);
    private final JTextField companyField = new JTextField(18);
    private final JComboBox<String> typeCombo = new JComboBox<>(new String[]{"Tablet", "Capsule", "Syrup", "Injection", "Cream"});
    private final JTextField priceField = new JTextField(18);
    private final JTextField stockField = new JTextField(18);
    private final JTextField reorderField = new JTextField(18);
    private final JTextField expiryField = new JTextField(18); // enforced yyyy-mm-dd
    private final JComboBox<String> supplierCombo = new JComboBox<>();
    private final JTextField searchField = new JTextField(18);

    private final Map<String, Integer> supplierNameToId = new HashMap<>();
    private Integer selectedMedicineId = null;

    // Constructor for medicine panel
    public MedicineManagementPanel() {

        // Set frame properties
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Init table for medicine panel + table reformer (allows dragging fields to different positions)
        tableModel = new DefaultTableModel( new Object[]{"ID", "Name", "Company", "Type", "Price", "Stock", "Reorder Lvl", "Expiry", "Supplier ID"}, 0) { @Override public boolean isCellEditable(int row, int column) { return false; } };
        table = new JTable(tableModel);
        table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> { if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) { loadSelectedRowIntoForm(); } });
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Init search bar
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton searchButton = new JButton("Search");
        JButton showAllButton = new JButton("Show All");
        searchPanel.add(new JLabel("Search Name:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(showAllButton);
        add(searchPanel, BorderLayout.NORTH);

        // Init panel and grid
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Medicine Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Add components to form
        addFormRow(formPanel, gbc, 0, "Name:", nameField);
        addFormRow(formPanel, gbc, 1, "Company:", companyField);
        addFormRow(formPanel, gbc, 2, "Type:", typeCombo);
        addFormRow(formPanel, gbc, 3, "Price:", priceField);
        addFormRow(formPanel, gbc, 4, "Stock Qty:", stockField);
        addFormRow(formPanel, gbc, 5, "Reorder Level:", reorderField);
        addFormRow(formPanel, gbc, 6, "Expiry (yyyy-MM-dd):", expiryField);
        addFormRow(formPanel, gbc, 7, "Supplier:", supplierCombo);

        // Create crud buttons
        JButton addButton = new JButton("Add");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");
        JButton clearButton = new JButton("Clear");

        // Add listeners to buttons
        addButton.addActionListener(e -> addMedicine());
        updateButton.addActionListener(e -> updateMedicine());
        deleteButton.addActionListener(e -> deleteMedicine());
        clearButton.addActionListener(e -> clearForm());
        searchButton.addActionListener(e -> searchMedicines());
        showAllButton.addActionListener(e -> refreshTable());

        // Init theme for buttons
        Theme.stylePrimaryButton(addButton);
        Theme.stylePrimaryButton(updateButton);
        Theme.styleDangerButton(deleteButton);

        // Create new panel for buttons + add them
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);

        // Set grid layout
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        // Add panel to root
        add(formPanel, BorderLayout.SOUTH);

        // call refresh methods for selection box and table
        refreshSupplierCombo();
        refreshTable();
    
    }

    // Helper to add form row to panel with layout specs
    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent field) {
        
        // Set grid layout
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;

        // add label
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;

        // add field to panel
        panel.add(field, gbc);

    }

    // Helper to refresh supplier combo box
    private void refreshSupplierCombo() {

        // Clear combo box and clear map of supplier
        supplierCombo.removeAllItems();
        supplierNameToId.clear();

        // Get suppliers from db
        List<Supplier> suppliers = supplierService.getAllSuppliers();
    
        // Add each supplier to the combo box and map it to its ID
        for (Supplier s : suppliers) {
           
            supplierCombo.addItem(s.getName());
            supplierNameToId.put(s.getName(), s.getSupplierId());
        
        }
    
    }

    // Helper to refresh table
    private void refreshTable() {

        populateTable(medicineService.getAllMedicines());

    }

    // Filters table by medicine name
    private void searchMedicines() {

        String query = searchField.getText().trim().toLowerCase();
        if (query.isEmpty()) { refreshTable(); return; }
        populateTable(medicineService.getAllMedicines().stream().filter(m -> m.getName().toLowerCase().contains(query)).toList());

    }

    // Populates table with given medicine list
    private void populateTable(List<Medicine> medicines) {

        // Clear table model
        tableModel.setRowCount(0);
        
        // Add each medicine to the table model
        for (Medicine m : medicines) { tableModel.addRow(new Object[]{ m.getMedicineId(), m.getName(), m.getCompany(), m.getMedicineType(), m.getPrice(), m.getQuantityInStock(), m.getReorderLevel(), m.getExpiryDate() != null ? m.getExpiryDate().toString() : "", m.getSupplierId() }); }

    }

    // Loads data selected from table into form fields
    private void loadSelectedRowIntoForm() {

        // Get selected medicine id from table
        int row = table.getSelectedRow();

        // Set form fields to selected medicine's data
        selectedMedicineId = (Integer) tableModel.getValueAt(row, 0);
        nameField.setText((String) tableModel.getValueAt(row, 1));
        companyField.setText((String) tableModel.getValueAt(row, 2));
        typeCombo.setSelectedItem(tableModel.getValueAt(row, 3));
        priceField.setText(String.valueOf(tableModel.getValueAt(row, 4)));
        stockField.setText(String.valueOf(tableModel.getValueAt(row, 5)));
        reorderField.setText(String.valueOf(tableModel.getValueAt(row, 6)));
        expiryField.setText((String) tableModel.getValueAt(row, 7));

        // Get suplier id
        int supplierId = (Integer) tableModel.getValueAt(row, 8);
        
        // Map each supplier id to its name, checking supplier ids match entries
        for (Map.Entry<String, Integer> entry : supplierNameToId.entrySet()) { if (entry.getValue() == supplierId) { supplierCombo.setSelectedItem(entry.getKey()); break; } }

    }

    // Clears form data
    private void clearForm() {
        
        // Sets init values for form fields
        selectedMedicineId = null;
        nameField.setText("");
        companyField.setText("");
        typeCombo.setSelectedIndex(0);
        priceField.setText("");
        stockField.setText("");
        reorderField.setText("");
        expiryField.setText("");
        if (supplierCombo.getItemCount() > 0) supplierCombo.setSelectedIndex(0);
        table.clearSelection();

    }

    // Returns a Medicine object built from the form data, or null if validation fails
    private Medicine buildMedicineFromForm() {

        // Check empty
        if (nameField.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(this, "Medicine name is required.", "Validation Error", JOptionPane.WARNING_MESSAGE); return null; }
        if (companyField.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(this, "Company name is required.", "Validation Error", JOptionPane.WARNING_MESSAGE); return null; }
        if (priceField.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(this, "Price is required.", "Validation Error", JOptionPane.WARNING_MESSAGE); return null; }

        // length checks
        if (nameField.getText().trim().length() > 150) { JOptionPane.showMessageDialog(this, "Medicine name must be 150 characters or fewer.", "Validation Error", JOptionPane.WARNING_MESSAGE); return null; }
        if (companyField.getText().trim().length() > 100) { JOptionPane.showMessageDialog(this, "Company name must be 100 characters or fewer.", "Validation Error", JOptionPane.WARNING_MESSAGE); return null; }

        // Check selection boxs not empty
        if (supplierCombo.getSelectedItem() == null) { JOptionPane.showMessageDialog(this, "Add a supplier first (Suppliers tab) before adding medicines.", "Validation Error", JOptionPane.WARNING_MESSAGE); return null; }
        if (typeCombo.getSelectedItem() == null) { JOptionPane.showMessageDialog(this, "Add a type first (Types tab) before adding medicines.", "Validation Error", JOptionPane.WARNING_MESSAGE); return null; }

        // Init vars
        double price;
        int stock;
        int reorderLevel;
        
        // Attempt parsing to dt, catch errors
        try {
        
            price = Double.parseDouble(priceField.getText().trim());
            stock = Integer.parseInt(stockField.getText().trim());
            reorderLevel = Integer.parseInt(reorderField.getText().trim());
        
        } catch (NumberFormatException e) { JOptionPane.showMessageDialog(this, "Price, Stock Qty, and Reorder Level must be valid numbers.", "Validation Error", JOptionPane.WARNING_MESSAGE); return null; }

        // Init vars
        LocalDate expiry = null;
        String expiryText = expiryField.getText().trim();
        
        // Check expiry date is not empty
        if (!expiryText.isEmpty()) {
            
            try { expiry = LocalDate.parse(expiryText); } 
            catch (DateTimeParseException e) { JOptionPane.showMessageDialog(this, "Expiry date must be in yyyy-MM-dd format, e.g. 2027-06-30.", "Validation Error", JOptionPane.WARNING_MESSAGE); return null; }
        
        }

        // Check expiry date isn't in the past for a new medicine
        if (selectedMedicineId == null && expiry != null && expiry.isBefore(LocalDate.now())) { JOptionPane.showMessageDialog(this, "Expiry date cannot be in the past for a new medicine.", "Validation Error", JOptionPane.WARNING_MESSAGE); return null; }

        // Check positive values
        if (price <= 0) { JOptionPane.showMessageDialog(this, "Price must be greater than 0.", "Validation Error", JOptionPane.WARNING_MESSAGE); return null; }
        if (stock < 0) { JOptionPane.showMessageDialog(this, "Stock quantity cannot be negative.", "Validation Error", JOptionPane.WARNING_MESSAGE); return null; }
        if (reorderLevel < 0) { JOptionPane.showMessageDialog(this, "Reorder level cannot be negative.", "Validation Error", JOptionPane.WARNING_MESSAGE); return null; }

        // Create new med obj and return
        Medicine medicine = new Medicine();
        medicine.setMedicineId(selectedMedicineId != null ? selectedMedicineId : 0);
        medicine.setName(nameField.getText().trim());
        medicine.setCompany(companyField.getText().trim());
        medicine.setMedicineType((String) typeCombo.getSelectedItem());
        medicine.setPrice(price);
        medicine.setQuantityInStock(stock);
        medicine.setReorderLevel(reorderLevel);
        medicine.setExpiryDate(expiry);
        medicine.setSupplierId(supplierNameToId.get((String) supplierCombo.getSelectedItem()));
        return medicine;

    }

    // Add medicine to service and table
    private void addMedicine() {
        
        // Build med obj
        Medicine medicine = buildMedicineFromForm();
        if (medicine == null) return;

        // Check service status addition & handle otherwise
        boolean success = medicineService.addMedicine(medicine);
        if (success) { refreshTable(); clearForm(); } 
        else { JOptionPane.showMessageDialog(this, "Failed to add medicine.", "Error", JOptionPane.ERROR_MESSAGE); }
    
    }

    // Update medicine in service and table
    private void updateMedicine() {
        
        // Check id is not null
        if (selectedMedicineId == null) { JOptionPane.showMessageDialog(this, "Select a medicine from the table first.", "No Selection", JOptionPane.WARNING_MESSAGE); return; }
        
        // Build med obj
        Medicine medicine = buildMedicineFromForm();
        if (medicine == null) return;

        // Call update service on new obj & validate status
        boolean success = medicineService.updateMedicine(medicine);
        if (success) { refreshTable(); clearForm(); } 
        else { JOptionPane.showMessageDialog(this, "Failed to update medicine.", "Error", JOptionPane.ERROR_MESSAGE); }
    
    }

    // Delete medicine from service and table
    private void deleteMedicine() {

        // Check id is not null
        if (selectedMedicineId == null) { JOptionPane.showMessageDialog(this, "Select a medicine from the table first.", "No Selection", JOptionPane.WARNING_MESSAGE); return; }
        
        // Confirm users deletion selection
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this medicine? This cannot be undone.", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        // Call service to delete medicine & refresh table, handle errors
        boolean success = medicineService.deleteMedicine(selectedMedicineId);
        if (success) { refreshTable(); clearForm(); } 
        else { JOptionPane.showMessageDialog(this, "Failed to delete medicine.\nIt may still be referenced by past sales — that's expected and generally shouldn't be deleted.", "Error", JOptionPane.ERROR_MESSAGE); }
    
    }

}
