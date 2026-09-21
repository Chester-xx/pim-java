package ui;

import model.User;
import service.UserService;
import util.PasswordUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginFrame extends JFrame {

    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JLabel statusLabel;
    private final UserService userService;

    // Constructor to set up the login frame UI
    public LoginFrame() {

        // Call new user service instance
        userService = new UserService();

        // Set frame props
        setTitle("PIMS - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 480);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(Theme.EDITOR_BACKGROUND);

        // Set up the layout and components
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(Theme.EDITOR_BACKGROUND);

        // Create the card panel
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Theme.SIDEBAR_BACKGROUND);
        card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1), new EmptyBorder(32, 36, 28, 36)));

        // Add title and subtitle labels
        JLabel titleLabel = new JLabel("Pharmacy Inventory Management");
        titleLabel.setFont(Theme.HEADER_FONT);
        titleLabel.setForeground(Theme.FOREGROUND);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add subtitle label
        JLabel subtitleLabel = new JLabel("Sign in to continue");
        subtitleLabel.setFont(Theme.SUBHEADER_FONT);
        subtitleLabel.setForeground(Color.GRAY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Create username field
        usernameField = new JTextField();
        usernameField.setMaximumSize(new Dimension(260, 32));

        // Create password field
        passwordField = new JPasswordField();
        passwordField.setMaximumSize(new Dimension(260, 32));

        // Init login button
        JButton loginButton = new JButton("Login");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setMaximumSize(new Dimension(260, 36));
        Theme.stylePrimaryButton(loginButton);

        // Init status label
        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Theme.DANGER_RED_HOVER);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel.setFont(Theme.SMALL_FONT);

        // Add components to card
        card.add(Box.createVerticalStrut(8));
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(subtitleLabel);
        card.add(Box.createVerticalStrut(24));
        card.add(labeledField("Username", usernameField));
        card.add(Box.createVerticalStrut(14));
        card.add(labeledField("Password", passwordField));
        card.add(Box.createVerticalStrut(22));
        card.add(loginButton);
        card.add(Box.createVerticalStrut(10));
        card.add(statusLabel);

        // Add card to outer panel
        outer.add(card);
        add(outer);

        // Add action listeners for login attempts
        loginButton.addActionListener(this::attemptLogin);
        usernameField.addActionListener(this::attemptLogin);
        passwordField.addActionListener(this::attemptLogin);
        
        // Add listener for enter key to work
        getRootPane().setDefaultButton(loginButton);

    }

    // Helper method to create a labeled field
    private JPanel labeledField(String labelText, JComponent field) {
        
        // Create a wrapper panel for the label and field
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setBackground(Theme.SIDEBAR_BACKGROUND);
        wrapper.setAlignmentX(Component.CENTER_ALIGNMENT);
        wrapper.setMaximumSize(new Dimension(260, 60));

        // Init label
        JLabel label = new JLabel(labelText);
        label.setFont(Theme.SMALL_FONT);
        label.setForeground(Color.LIGHT_GRAY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Align field to left
        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Add components to wrapper
        wrapper.add(label);
        wrapper.add(Box.createVerticalStrut(4));
        wrapper.add(field);

        // return obj
        return wrapper;
    
    }

    // Enables login attempt
    private void attemptLogin(ActionEvent e) {

        // Decl username and password vars
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        // Defer if either field is empty
        if (username.isEmpty() || password.isEmpty()) {

            statusLabel.setText("Please enter both username and password.");
            return;
        
        }

        // Create user service instance
        User user = userService.getUserByUsername(username);

        // Defer if returned user object does not exist
        if (user == null) {
        
            statusLabel.setText("Invalid username or password.");
            passwordField.setText("");
            return;
        
        }

        // Hash password
        String enteredHash = PasswordUtil.hash(password);

        // Check password hashes match
        if (!enteredHash.equals(user.getPassword())) {
           
            statusLabel.setText("Invalid username or password.");
            passwordField.setText("");
            return;
        
        }

        // Unload frame
        dispose();

        // Point user to either admin or cashier dashboard
        if (user.isAdmin()) { new AdminDashboard(user).setVisible(true); } 
        else { new CashierDashboard(user).setVisible(true); }
    
    }

}
