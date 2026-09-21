package ui;

import model.Sale;
import model.User;
import service.SalesService;
import service.UserService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SalesByDateRangeReportPanel extends JPanel {

    private final SalesService salesService = new SalesService();
    private final UserService userService = new UserService();

    private final JTextField fromField = new JTextField(10);
    private final JTextField toField = new JTextField(10);
    private final DefaultTableModel tableModel;
    private final JLabel totalLabel = new JLabel("Total: R 0.00");

    // Constructor for report panel of sales date range
    public SalesByDateRangeReportPanel() {

        // Set window properties
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Add top panel with input fields and run button
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("From (yyyy-MM-dd):"));
        topPanel.add(fromField);
        topPanel.add(new JLabel("To (yyyy-MM-dd):"));
        topPanel.add(toField);
        JButton runButton = new JButton("Run Report");
        Theme.stylePrimaryButton(runButton);
        runButton.addActionListener(e -> runReport());
        topPanel.add(runButton);
        add(topPanel, BorderLayout.NORTH);

        // Create new table for sales
        tableModel = new DefaultTableModel(new Object[]{"Sale ID", "Date", "Cashier", "Total"}, 0) { @Override public boolean isCellEditable(int row, int column) { return false; }};
        JTable table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Add total label at bottom of panel
        totalLabel.setFont(Theme.HEADER_FONT.deriveFont(14f));
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(totalLabel);
        add(bottomPanel, BorderLayout.SOUTH);

        // Default date for simplicity
        String today = LocalDate.now().toString();
        fromField.setText(today);
        toField.setText(today);

        // Call report action
        runReport();

    }

    // Gets report details and displays it in the table from date range
    private void runReport() {
       
        // Init vars
        LocalDate fromDate;
        LocalDate toDate;
       
        // Parse dates from text fields
        try {
       
            fromDate = LocalDate.parse(fromField.getText().trim());
            toDate = LocalDate.parse(toField.getText().trim());
       
        } catch (DateTimeParseException e) { JOptionPane.showMessageDialog(this, "Dates must be in yyyy-MM-dd format, e.g. 2026-09-20.", "Validation Error", JOptionPane.WARNING_MESSAGE); return; }

        // Check date range is valid (from must not be after to)
        if (fromDate.isAfter(toDate)) { JOptionPane.showMessageDialog(this, "The 'From' date must not be after the 'To' date.", "Validation Error", JOptionPane.WARNING_MESSAGE); return; }

        // Get date ranges from user safe
        Timestamp from = Timestamp.valueOf(fromDate.atStartOfDay());
        Timestamp to = Timestamp.valueOf(toDate.atTime(23, 59, 59));

        // Build user id map to fullName
        Map<Integer, String> userNames = new HashMap<>();
        for (User u : userService.getAllUsers()) { userNames.put(u.getUserId(), u.getFullName()); }

        // Clear table
        tableModel.setRowCount(0);
        
        // Add rows to table, plus calculate total
        double total = 0;
        List<Sale> sales = salesService.getSalesByDateRange(from, to);
        for (Sale sale : sales) {
       
            total += sale.getTotalAmount();
            String cashierName = userNames.getOrDefault(sale.getUserId(), "Unknown (ID " + sale.getUserId() + ")");
            tableModel.addRow(new Object[]{sale.getSaleId(), sale.getSaleDate(), cashierName, sale.getTotalAmount()});
       
        }

        // Set total to label
        totalLabel.setText(String.format("Total: R %.2f  (%d sale%s)", total, sales.size(), sales.size() == 1 ? "" : "s"));
    
    }

}
