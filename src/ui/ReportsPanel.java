package ui;

import javax.swing.*;

public class ReportsPanel extends JTabbedPane {

    // Constructor for reusable report panels
    public ReportsPanel() {

        // Add tabs to panel
        addTab("Low Stock", new LowStockReportPanel());
        addTab("Expiring Soon", new ExpiringSoonReportPanel());
        addTab("Item-Wise Sales", new ItemWiseSalesReportPanel());
        addTab("Sales by Date Range", new SalesByDateRangeReportPanel());

    }

}
