package ui;

import model.User;

import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends JFrame {

    // Constructor for the admin dashboard
    public AdminDashboard(User user) {
        
        // Set up frame
        setTitle("PIMS - Admin Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 580);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Theme.EDITOR_BACKGROUND);

        // Init border layout & background
        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(Theme.EDITOR_BACKGROUND);

        // Init header panel
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.SIDEBAR_BACKGROUND);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER_COLOR));
        header.setPreferredSize(new Dimension(0, 52));

        // Init welcome label
        JLabel welcomeLabel = new JLabel("  Welcome, " + user.getFullName() + " (Admin)");
        welcomeLabel.setFont(Theme.HEADER_FONT);
        welcomeLabel.setForeground(Theme.FOREGROUND);
        header.add(welcomeLabel, BorderLayout.WEST);

        // Init logout button
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });
        JPanel logoutWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 10));
        logoutWrapper.setBackground(Theme.SIDEBAR_BACKGROUND);
        logoutWrapper.add(logoutButton);
        header.add(logoutWrapper, BorderLayout.EAST);

        // Add header to root panel
        rootPanel.add(header, BorderLayout.NORTH);

        // Init tabbed pane for different management sections
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Medicines", new MedicineManagementPanel());
        tabbedPane.addTab("Suppliers", new SupplierManagementPanel());
        tabbedPane.addTab("Users", new UserManagementPanel(user));
        tabbedPane.addTab("Reports", new ReportsPanel());
        rootPanel.add(tabbedPane, BorderLayout.CENTER);

        // Finally add root panel to frame
        add(rootPanel);

    }

}
