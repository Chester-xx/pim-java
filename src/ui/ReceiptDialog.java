package ui;

import javax.swing.*;
import java.awt.*;
import java.util.List;

// Receipt dialog class for displaying receipt details
public class ReceiptDialog extends JDialog {

    // Constructor for calling reciept window
    public ReceiptDialog(Frame owner, int saleId, String cashierName, List<Object[]> lineItems, double total) {
        
        // Set window properties
        super(owner, "Receipt - Sale #" + saleId, true);
        setSize(400, 450);
        setLocationRelativeTo(owner);

        // Create receipt area
        JTextArea receiptArea = new JTextArea();
        receiptArea.setEditable(false);
        receiptArea.setFont(Theme.MONO_FONT);
        receiptArea.setBackground(Theme.EDITOR_BACKGROUND);
        receiptArea.setForeground(Theme.FOREGROUND);
        receiptArea.setCaretColor(Theme.FOREGROUND);

        // Create receipt header with sale and cashier details to output
        StringBuilder sb = new StringBuilder();
        sb.append("===== HealthFirst Pharmacy =====\n");
        sb.append("Sale #: ").append(saleId).append("\n");
        sb.append("Cashier: ").append(cashierName).append("\n");
        sb.append("---------------------------------\n");
        sb.append(String.format("%-16s %3s %8s%n", "Item", "Qty", "Total"));
        sb.append("---------------------------------\n");

        // Add each line record to output
        for (Object[] line : lineItems) {
           
            // Initialize variables
            String name = (String) line[0];
            int qty = (int) line[1];
            double lineTotal = (double) line[3];
            
            // Check if name is too long and truncate it if necessary
            if (name.length() > 16) name = name.substring(0, 16);

            // Add to output
            sb.append(String.format("%-16s %3d %8.2f%n", name, qty, lineTotal));
        
        }

        // Create receipt footer
        sb.append("---------------------------------\n");
        sb.append(String.format("%-20s %10.2f%n", "TOTAL:", total));
        sb.append("=================================\n");
        sb.append("Thank you for your purchase\n");

        // Set window text to output
        receiptArea.setText(sb.toString());

        // Init print button
        JButton printButton = new JButton("Print");
        Theme.stylePrimaryButton(printButton);
        
        // Add listener for print call
        printButton.addActionListener(e -> {
            
            try { receiptArea.print(); } 
            catch (Exception ex) { JOptionPane.showMessageDialog(this, "Printing failed: " + ex.getMessage(), "Print Error", JOptionPane.ERROR_MESSAGE); }
        
        });

        // Init close button for receipt
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());

        // Add panel to frame
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(printButton);
        buttonPanel.add(closeButton);

        // Set border layout and add components to panel
        setLayout(new BorderLayout());
        add(new JScrollPane(receiptArea), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    
    }

}
