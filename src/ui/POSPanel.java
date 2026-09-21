package ui;

import model.Medicine;
import model.Sale;
import model.SaleItem;
import model.User;
import service.MedicineService;
import service.SalesService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class POSPanel extends JPanel {

    private final MedicineService medicineService = new MedicineService();
    private final SalesService salesService = new SalesService();
    private final User currentUser;

    private final JTextField searchField = new JTextField(20);
    private JTable resultsTable;
    private final JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 9999, 1));

    private JTable cartTable;
    private final List<Medicine> cartMedicines = new ArrayList<>();
    private final List<Integer> cartQuantities = new ArrayList<>();
    private final JLabel totalLabel = new JLabel("Total: R 0.00");

    // Constructor for point of sale panel
    public POSPanel(User currentUser) {

        // Set current user context
        this.currentUser = currentUser;

        // Set layout and border for the panel
        setLayout(new GridLayout(1, 2, 10, 0));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Add components to the root
        add(buildSearchPanel());
        add(buildCartPanel());

        // Load all medicines on initialization
        loadAllMedicines();

    }

    // Build search panel with search field, search button, and show all button
    private JPanel buildSearchPanel() {

        // Create panel for border layout
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Search Medicines"));

        // Create saearch bar, buttons and add to panel
        JPanel searchBar = new JPanel(new BorderLayout(5, 5));
        searchBar.add(searchField, BorderLayout.CENTER);
        JButton searchButton = new JButton("Search");
        JButton showAllButton = new JButton("Show All");
        JPanel searchButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchButtons.add(searchButton);
        searchButtons.add(showAllButton);
        searchBar.add(searchButtons, BorderLayout.EAST);
        panel.add(searchBar, BorderLayout.NORTH);

        // Create table model and add to panel
        DefaultTableModel model = new DefaultTableModel(new Object[]{"ID", "Name", "Company", "Type", "Price", "Stock"}, 0) { @Override public boolean isCellEditable(int row, int column) { return false; }};
        resultsTable = new JTable(model);
        resultsTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        panel.add(new JScrollPane(resultsTable), BorderLayout.CENTER);

        // Create quantity and add to cart button for panel
        JPanel addPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addPanel.add(new JLabel("Qty:"));
        addPanel.add(quantitySpinner);
        JButton addToCartButton = new JButton("Add to Cart");
        Theme.stylePrimaryButton(addToCartButton);
        addPanel.add(addToCartButton);
        panel.add(addPanel, BorderLayout.SOUTH);

        // Add listeners for calling methods
        searchButton.addActionListener(e -> searchMedicines());
        showAllButton.addActionListener(e -> loadAllMedicines());
        addToCartButton.addActionListener(e -> addSelectedToCart());
        searchField.addActionListener(e -> searchMedicines());

        // return obj
        return panel;

    }

    // Builds the cart panel
    private JPanel buildCartPanel() {
        
        // Create new panel
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Cart"));

        // Create table model 
        DefaultTableModel model = new DefaultTableModel(new Object[]{"Name", "Qty", "Unit Price", "Line Total"}, 0) { @Override public boolean isCellEditable(int row, int column) { return false; }};
        cartTable = new JTable(model);
        cartTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        panel.add(new JScrollPane(cartTable), BorderLayout.CENTER);

        // Init remove button
        JButton removeButton = new JButton("Remove Selected");
        Theme.styleDangerButton(removeButton);
        removeButton.addActionListener(e -> removeSelectedFromCart());

        // Init total label
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        totalLabel.setFont(Theme.HEADER_FONT);
        bottomPanel.add(totalLabel, BorderLayout.WEST);

        // Init checkout button
        JButton checkoutButton = new JButton("Checkout");
        checkoutButton.setFont(Theme.HEADER_FONT.deriveFont(13f));
        Theme.stylePrimaryButton(checkoutButton);
        checkoutButton.addActionListener(e -> checkout());

        // Add actions to layout
        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionRow.add(removeButton);
        actionRow.add(checkoutButton);
        bottomPanel.add(actionRow, BorderLayout.EAST);

        // add layout to panel
        panel.add(bottomPanel, BorderLayout.SOUTH);

        // return obj
        return panel;
    
    }

    // Add medicine to cart
    private void loadAllMedicines() { populateResultsTable(medicineService.getAllMedicines()); }

    // Search medicine by name
    private void searchMedicines() {
        
        // Get query from user and check not empty
        String query = searchField.getText().trim().toLowerCase();
        if (query.isEmpty()) { loadAllMedicines(); return; }
        
        // Filter search for medicines matching query
        List<Medicine> filtered = medicineService.getAllMedicines().stream().filter(m -> m.getName().toLowerCase().contains(query)).toList();
        
        // Output results to table
        populateResultsTable(filtered);

    }

    // Add list defined to results table
    private void populateResultsTable(List<Medicine> medicines) {

        // Get table obj
        DefaultTableModel model = (DefaultTableModel) resultsTable.getModel();
        
        // Clear existing rows from table
        model.setRowCount(0);
        
        // Add each medicine to the table
        for (Medicine m : medicines) { model.addRow(new Object[]{ m.getMedicineId(), m.getName(), m.getCompany(), m.getMedicineType(), m.getPrice(), m.getQuantityInStock() }); }
    
    }

    // Add selected medicine to cart
    private void addSelectedToCart() {

        // Get selected medicine from the selection list, handle inaccessibles
        int row = resultsTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a medicine from the search results first.", "No Selection", JOptionPane.WARNING_MESSAGE); return; }

        // Get med id and quantity from user
        int medicineId = (int) resultsTable.getModel().getValueAt(row, 0);
        int requestedQty = (int) quantitySpinner.getValue();

        // Get med obj from db, rather than table (causes same issues when db and table dont match), handle edge cases
        Medicine medicine = medicineService.getMedicineById(medicineId);
        if (medicine == null) { JOptionPane.showMessageDialog(this, "This medicine could not be found — try refreshing the search.", "Error", JOptionPane.ERROR_MESSAGE); return; }

        // If it's already in the cart, combine quantities instead of adding a duplicate row
        int existingIndex = -1;
        for (int i = 0; i < cartMedicines.size(); i++) { if (cartMedicines.get(i).getMedicineId() == medicine.getMedicineId()) { existingIndex = i; break; } }
        
        // Check if already in cart, if so, combine quantities instead of adding a duplicate row
        int alreadyInCart = existingIndex >= 0 ? cartQuantities.get(existingIndex) : 0;
        int totalRequested = alreadyInCart + requestedQty;

        // Check if there's enough stock before adding to cart
        if (totalRequested > medicine.getQuantityInStock()) { JOptionPane.showMessageDialog(this, "Not enough stock. Available: " + medicine.getQuantityInStock() + ", already in cart: " + alreadyInCart, "Insufficient Stock", JOptionPane.WARNING_MESSAGE); return; }

        // Manages the cart by either updating the quantity of an existing medicine or adding a new medicine with its requested quantity
        if (existingIndex >= 0) { cartQuantities.set(existingIndex, totalRequested); } 
        else { cartMedicines.add(medicine); cartQuantities.add(requestedQty); }

        // Call table update after changes
        refreshCartTable();
    
    }

    // Remove medicine from cart
    private void removeSelectedFromCart() {
       
        // Check selection is valid
        int row = cartTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select an item in the cart first.", "No Selection", JOptionPane.WARNING_MESSAGE); return; }
        
        // Remove from lists
        cartMedicines.remove(row);
        cartQuantities.remove(row);
    
        // Update table after changes
        refreshCartTable();
    
    }

    // Refresh cart after changes
    private void refreshCartTable() {
        
        // Get table
        DefaultTableModel model = (DefaultTableModel) cartTable.getModel();
        
        // Clear table
        model.setRowCount(0);
        
        // Recalculate total
        double total = 0;
        for (int i = 0; i < cartMedicines.size(); i++) {
        
            // Get objects and quantities to calculate new total
            Medicine m = cartMedicines.get(i);
            int qty = cartQuantities.get(i);
            double lineTotal = m.getPrice() * qty;
            total += lineTotal;

            // Add to table
            model.addRow(new Object[]{m.getName(), qty, m.getPrice(), lineTotal});
        
        }

        // Update total to new value
        totalLabel.setText(String.format("Total: R %.2f", total));
    
    }

    // Checkout button action
    private void checkout() {

        // Check if cart is empty before proceeding with checkout
        if (cartMedicines.isEmpty()) { JOptionPane.showMessageDialog(this, "The cart is empty.", "Nothing to Checkout", JOptionPane.WARNING_MESSAGE); return; }

        // Init total, sale and reciept lists
        double total = 0;
        List<SaleItem> saleItems = new ArrayList<>();
        List<Object[]> receiptLines = new ArrayList<>();

        // Add items to sale and receipt lines
        for (int i = 0; i < cartMedicines.size(); i++) {
           
            // Get medicine and calculate line total
            Medicine m = cartMedicines.get(i);
            int qty = cartQuantities.get(i);
            double lineTotal = m.getPrice() * qty;
            total += lineTotal;

            // Create new sale item and add to list
            SaleItem item = new SaleItem();
            item.setMedicineId(m.getMedicineId());
            item.setQuantitySold(qty);
            item.setPriceAtSale(m.getPrice());
            saleItems.add(item);

            // Add line to reciept
            receiptLines.add(new Object[]{m.getName(), qty, m.getPrice(), lineTotal});
        
        }

        // Create sale object
        Sale sale = new Sale();
        sale.setTotalAmount(total);
        sale.setUserId(currentUser.getUserId());

        // Record sale and line items in the database, checking returned status
        boolean recorded = salesService.recordSale(sale, saleItems);
        if (!recorded) { JOptionPane.showMessageDialog(this, "Failed to record the sale. Nothing was charged — please try again.", "Checkout Error", JOptionPane.ERROR_MESSAGE); return; }

        // Reduce stock of each cart item in db
        for (int i = 0; i < cartMedicines.size(); i++) { medicineService.reduceStock(cartMedicines.get(i).getMedicineId(), cartQuantities.get(i)); }

        // Get root frame, display receipt dialog
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        new ReceiptDialog(owner, sale.getSaleId(), currentUser.getFullName(), receiptLines, total).setVisible(true);

        // reset cart and refresh stock levels shown in the search results
        cartMedicines.clear();
        cartQuantities.clear();
        refreshCartTable();
        loadAllMedicines();

    }
    
}
