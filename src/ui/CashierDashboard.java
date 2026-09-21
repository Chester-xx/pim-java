package ui;

import model.User;

import javax.swing.*;
import java.awt.*;

public class CashierDashboard extends JFrame {

    // Constructor for cashier dashboard
    public CashierDashboard(User user) {

        // Set up the main frame
        setTitle("PIMS - Cashier Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 580);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Theme.EDITOR_BACKGROUND);

        // Set up the root panel with border layout
        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(Theme.EDITOR_BACKGROUND);

        // Init header panel
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.SIDEBAR_BACKGROUND);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER_COLOR));
        header.setPreferredSize(new Dimension(0, 52));

        // Add welcome label and logout button to the header
        JLabel welcomeLabel = new JLabel("  Welcome, " + user.getFullName() + " (Cashier)");
        welcomeLabel.setFont(Theme.HEADER_FONT);
        welcomeLabel.setForeground(Theme.FOREGROUND);
        header.add(welcomeLabel, BorderLayout.WEST);

        // Init logout button to the header
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });
        JPanel logoutWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 10));
        logoutWrapper.setBackground(Theme.SIDEBAR_BACKGROUND);
        logoutWrapper.add(logoutButton);
        header.add(logoutWrapper, BorderLayout.EAST);

        // Add header and POS panel to the root panel
        rootPanel.add(header, BorderLayout.NORTH);
        rootPanel.add(new POSPanel(user), BorderLayout.CENTER);

        // Finally add the root panel to the frame
        add(rootPanel);

    }
    
}
