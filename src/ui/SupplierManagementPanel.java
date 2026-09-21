package ui;

import model.Supplier;
import service.SupplierService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class SupplierManagementPanel extends JPanel {

    private final SupplierService supplierService = new SupplierService();

    private final DefaultTableModel tableModel;
    private final JTable table;

    private final JTextField nameField = new JTextField(20);
    private final JTextField contactField = new JTextField(20);
    private final JTextField phoneField = new JTextField(20);
    private final JTextField emailField = new JTextField(20);
    private final JTextField addressField = new JTextField(20);
    private final JTextField searchField = new JTextField(20);

    private Integer selectedSupplierId = null;

    // Constructor to initialize supplier view
    public SupplierManagementPanel() {

        // Set window properties
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create table model and add columns, disable editing
        tableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Contact", "Phone", "Email", "Address"}, 0) { @Override public boolean isCellEditable(int row, int column) { return false; }};
        table = new JTable(tableModel);
        table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> { if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) { loadSelectedRowIntoForm(); }});
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Create search bar
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton searchButton = new JButton("Search");
        JButton showAllButton = new JButton("Show All");
        searchPanel.add(new JLabel("Search Name:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(showAllButton);
        add(searchPanel, BorderLayout.NORTH);

        // Create form panel and add fields
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Supplier Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Add fields to form panel
        addFormRow(formPanel, gbc, 0, "Name:", nameField);
        addFormRow(formPanel, gbc, 1, "Contact Person:", contactField);
        addFormRow(formPanel, gbc, 2, "Phone:", phoneField);
        addFormRow(formPanel, gbc, 3, "Email:", emailField);
        addFormRow(formPanel, gbc, 4, "Address:", addressField);

        // Create crud buttons
        JButton addButton = new JButton("Add");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");
        JButton clearButton = new JButton("Clear");

        // Add listeners to buttons
        addButton.addActionListener(e -> addSupplier());
        updateButton.addActionListener(e -> updateSupplier());
        deleteButton.addActionListener(e -> deleteSupplier());
        clearButton.addActionListener(e -> clearForm());
        searchButton.addActionListener(e -> searchSuppliers());
        showAllButton.addActionListener(e -> refreshTable());

        // Apply theme
        Theme.stylePrimaryButton(addButton);
        Theme.stylePrimaryButton(updateButton);
        Theme.styleDangerButton(deleteButton);

        // Create panel and add buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);

        // Set grid properties
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        // Add panel to root
        add(formPanel, BorderLayout.SOUTH);

        // Call table refresh
        refreshTable();
    
    }

    // Adds fields to the form panel
    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String label, JTextField field) {
        
        // Init grid
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        
        // Add label and field
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        panel.add(field, gbc);
    
    }

    // Update table when form is updated
    private void refreshTable() {

        populateTable(supplierService.getAllSuppliers());

    }

    // Filters table by supplier name
    private void searchSuppliers() {

        String query = searchField.getText().trim().toLowerCase();
        if (query.isEmpty()) { refreshTable(); return; }
        populateTable(supplierService.getAllSuppliers().stream().filter(s -> s.getName().toLowerCase().contains(query)).toList());

    }

    // Populates table with given supplier list
    private void populateTable(List<Supplier> suppliers) {

        // Clear table
        tableModel.setRowCount(0);
        
        // Add suppliers to table
        for (Supplier s : suppliers) { tableModel.addRow(new Object[]{ s.getSupplierId(), s.getName(), s.getContactPerson(), s.getPhone(), s.getEmail(), s.getAddress() }); }
    
    }

    // Gets supplier details from table and inserts into form fields
    private void loadSelectedRowIntoForm() {

        // Get row
        int row = table.getSelectedRow();
        
        // Insert row data
        selectedSupplierId = (Integer) tableModel.getValueAt(row, 0);
        nameField.setText((String) tableModel.getValueAt(row, 1));
        contactField.setText((String) tableModel.getValueAt(row, 2));
        phoneField.setText((String) tableModel.getValueAt(row, 3));
        emailField.setText((String) tableModel.getValueAt(row, 4));
        addressField.setText((String) tableModel.getValueAt(row, 5));
    
    }

    // Clears form data 
    private void clearForm() {
        
        selectedSupplierId = null;
        nameField.setText("");
        contactField.setText("");
        phoneField.setText("");
        emailField.setText("");
        addressField.setText("");
        table.clearSelection();
    
    }

    // Builds a new supplier object from the form data
    private Supplier buildSupplierFromForm() {
        
        // Create new sup obj
        Supplier s = new Supplier();
        
        // Set sup entries
        s.setSupplierId(selectedSupplierId != null ? selectedSupplierId : 0);
        s.setName(nameField.getText().trim());
        s.setContactPerson(contactField.getText().trim());
        s.setPhone(phoneField.getText().trim());
        s.setEmail(emailField.getText().trim());
        s.setAddress(addressField.getText().trim());
    
        // Return obj
        return s;
    
    }

    // Checks entry fields for supplier addition pass certain criteria
    private boolean validateForm() {

        // empty fields
        if (nameField.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(this, "Supplier name is required.", "Validation Error", JOptionPane.WARNING_MESSAGE); return false; }
        if (contactField.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(this, "Contact person is required.", "Validation Error", JOptionPane.WARNING_MESSAGE); return false; }
        if (phoneField.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(this, "Phone number is required.", "Validation Error", JOptionPane.WARNING_MESSAGE); return false; }
        if (emailField.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(this, "Email is required.", "Validation Error", JOptionPane.WARNING_MESSAGE); return false; }
        if (addressField.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(this, "Address is required.", "Validation Error", JOptionPane.WARNING_MESSAGE); return false; }

        // length checks
        if (nameField.getText().trim().length() > 100) { JOptionPane.showMessageDialog(this, "Supplier name must be 100 characters or fewer.", "Validation Error", JOptionPane.WARNING_MESSAGE); return false; }
        if (contactField.getText().trim().length() > 100) { JOptionPane.showMessageDialog(this, "Contact person must be 100 characters or fewer.", "Validation Error", JOptionPane.WARNING_MESSAGE); return false; }
        if (emailField.getText().trim().length() > 100) { JOptionPane.showMessageDialog(this, "Email must be 100 characters or fewer.", "Validation Error", JOptionPane.WARNING_MESSAGE); return false; }

        // email and phone checks
        if (phoneField.getText().trim().length() != 10) { JOptionPane.showMessageDialog(this, "Phone number must be 10 characters long.", "Validation Error", JOptionPane.WARNING_MESSAGE); return false; }
        if (!phoneField.getText().trim().matches("\\d+")) { JOptionPane.showMessageDialog(this, "Phone number must contain digits only.", "Validation Error", JOptionPane.WARNING_MESSAGE); return false; }
        if (!isValidEmail(emailField.getText().trim())) { JOptionPane.showMessageDialog(this, "Invalid email format.", "Validation Error", JOptionPane.WARNING_MESSAGE); return false; }

        // Checks succeeded
        return true;

    }

    // Returns whether an email matches regular expression
    private boolean isValidEmail(String email) { return email.matches("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"); }

    // Adds a new supplier to db
    private void addSupplier() {

        // Check form is filled correctly
        if (!validateForm()) return;
        
        // Create supplier object from form data and send to supplier service for db addition
        boolean success = supplierService.addSupplier(buildSupplierFromForm());
        
        // Check addition status
        if (success) { refreshTable(); clearForm(); } 
        else { JOptionPane.showMessageDialog(this, "Failed to add supplier.", "Error", JOptionPane.ERROR_MESSAGE); }
    
    }

    // Updates a supplier in db
    private void updateSupplier() {
        
        // Check if supplier selected
        if (selectedSupplierId == null) { JOptionPane.showMessageDialog(this, "Select a supplier from the table first.", "No Selection", JOptionPane.WARNING_MESSAGE); return; }
        
        // Check form is filled correctly
        if (!validateForm()) return;
        
        // Create supplier object from form data and send to supplier service for db update, validate update
        boolean success = supplierService.updateSupplier(buildSupplierFromForm());
        if (success) { refreshTable(); clearForm(); } 
        else { JOptionPane.showMessageDialog(this, "Failed to update supplier.", "Error", JOptionPane.ERROR_MESSAGE); }
    
    }

    private void deleteSupplier() {
        
        // Check if supplier is selected
        if (selectedSupplierId == null) { JOptionPane.showMessageDialog(this, "Select a supplier from the table first.", "No Selection", JOptionPane.WARNING_MESSAGE); return; }
        
        // Confirm delete to user
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this supplier? This cannot be undone.", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        // Call service to delete supplier from db
        boolean success = supplierService.deleteSupplier(selectedSupplierId);
        
        // Check success, may fail due to foreign key constraint
        if (success) { refreshTable(); clearForm(); }
        else { JOptionPane.showMessageDialog(this, "Failed to delete supplier.\nIt may still have medicines linked to it — reassign or remove those first.", "Error", JOptionPane.ERROR_MESSAGE); }
    
    }

}
