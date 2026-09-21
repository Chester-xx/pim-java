package ui;

import model.User;
import service.UserService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class UserManagementPanel extends JPanel {

    private final UserService userService = new UserService();
    private final User currentUser;

    private final DefaultTableModel tableModel;
    private final JTable table;

    private final JTextField usernameField = new JTextField(18);
    private final JPasswordField passwordField = new JPasswordField(18);
    private final JComboBox<String> roleCombo = new JComboBox<>(new String[]{"Admin", "Cashier"});
    private final JTextField fullNameField = new JTextField(18);
    private final JTextField searchField = new JTextField(18);

    private Integer selectedUserId = null;

    // Constructor for user management panel
    public UserManagementPanel(User currentUser) {

        this.currentUser = currentUser;

        // Set window properties
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create table model for users
        tableModel = new DefaultTableModel(new Object[]{"ID", "Username", "Role", "Full Name"}, 0) { @Override public boolean isCellEditable(int row, int column) { return false; }};
        table = new JTable(tableModel);
        table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> { if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) { loadSelectedRowIntoForm(); }});
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Create search bar
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton searchButton = new JButton("Search");
        JButton showAllButton = new JButton("Show All");
        searchPanel.add(new JLabel("Search Full Name:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(showAllButton);
        add(searchPanel, BorderLayout.NORTH);

        // Create panel for form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("User Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Add fields to form
        addFormRow(formPanel, gbc, 0, "Username:", usernameField);
        addFormRow(formPanel, gbc, 1, "Password (new users only):", passwordField);
        addFormRow(formPanel, gbc, 2, "Role:", roleCombo);
        addFormRow(formPanel, gbc, 3, "Full Name:", fullNameField);

        // Add buttons to form
        JButton addButton = new JButton("Add");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");
        JButton clearButton = new JButton("Clear");

        // Add listeners to buttons
        addButton.addActionListener(e -> addUser());
        updateButton.addActionListener(e -> updateUser());
        deleteButton.addActionListener(e -> deleteUser());
        clearButton.addActionListener(e -> clearForm());
        searchButton.addActionListener(e -> searchUsers());
        showAllButton.addActionListener(e -> refreshTable());

        // Apply theme
        Theme.stylePrimaryButton(addButton);
        Theme.stylePrimaryButton(updateButton);
        Theme.styleDangerButton(deleteButton);

        // Create & init button panel 
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);

        // Set grid properties
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        // Add form panel to main panel
        add(formPanel, BorderLayout.SOUTH);

        // Call table refresh
        refreshTable();
    
    }

    // Adds panel rows to form
    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent field) {
        
        // Set grid properties
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;

        // Add label & set field
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        panel.add(field, gbc);
    
    }

    // Refreshes table with new data
    private void refreshTable() {

        populateTable(userService.getAllUsers());

    }

    // Filters table by full name
    private void searchUsers() {

        String query = searchField.getText().trim().toLowerCase();
        if (query.isEmpty()) { refreshTable(); return; }
        populateTable(userService.getAllUsers().stream().filter(u -> u.getFullName().toLowerCase().contains(query)).toList());

    }

    // Populates table with given user list
    private void populateTable(List<User> users) {

        // Clear table
        tableModel.setRowCount(0);
        
        // Add users to table
        for (User u : users) { tableModel.addRow(new Object[]{u.getUserId(), u.getUsername(), u.getRole(), u.getFullName()}); }
    
    }

    // Gets selected user from table and loads data into form fields
    private void loadSelectedRowIntoForm() {

        // Get row selected
        int row = table.getSelectedRow();

        // Gets data and sets respective field
        selectedUserId = (Integer) tableModel.getValueAt(row, 0);
        usernameField.setText((String) tableModel.getValueAt(row, 1));
        usernameField.setEnabled(false);
        passwordField.setText("");
        passwordField.setEnabled(false);
        roleCombo.setSelectedItem(tableModel.getValueAt(row, 2));
        fullNameField.setText((String) tableModel.getValueAt(row, 3));
        roleCombo.setEnabled("Admin".equals(tableModel.getValueAt(row, 2)) ? selectedUserId == currentUser.getUserId() : true);
    
    }

    // Clears the form fields
    private void clearForm() {

        selectedUserId = null;
        usernameField.setText("");
        usernameField.setEnabled(true);
        passwordField.setText("");
        passwordField.setEnabled(true);
        roleCombo.setSelectedIndex(0);
        roleCombo.setEnabled(true);
        fullNameField.setText("");
        table.clearSelection();

    }

    // Adds new user to db
    private void addUser() {

        // Validate inputs
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String fullName = fullNameField.getText().trim();

        // Check if fields are not empty
        if (username.isEmpty() || password.isEmpty() || fullName.isEmpty()) { JOptionPane.showMessageDialog(this, "Username, password, and full name are all required for a new user.", "Validation Error", JOptionPane.WARNING_MESSAGE); return; }

        // Check if username is not too short
        if (username.length() < 3) { JOptionPane.showMessageDialog(this, "Username must be at least 3 characters long.", "Validation Error", JOptionPane.WARNING_MESSAGE); return; }

        // Check username is not too long
        if (username.length() > 50) { JOptionPane.showMessageDialog(this, "Username must be 50 characters or fewer.", "Validation Error", JOptionPane.WARNING_MESSAGE); return; }

        // Check username contains only letters, numbers, and underscores
        if (!username.matches("^[a-zA-Z0-9_]+$")) { JOptionPane.showMessageDialog(this, "Username can only contain letters, numbers, and underscores.", "Validation Error", JOptionPane.WARNING_MESSAGE); return; }

        // Check password is at least 8 characters long
        if (password.length() < 8) { JOptionPane.showMessageDialog(this, "Password must be at least 8 characters long.", "Validation Error", JOptionPane.WARNING_MESSAGE); return; }

        // Check full name is not too long
        if (fullName.length() > 100) { JOptionPane.showMessageDialog(this, "Full name must be 100 characters or fewer.", "Validation Error", JOptionPane.WARNING_MESSAGE); return; }

        // Check role selected
        if (roleCombo.getSelectedItem() == null) { JOptionPane.showMessageDialog(this, "Please select a role for the new user.", "Validation Error", JOptionPane.WARNING_MESSAGE); return; }

        // Create and set new user object 
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setRole((String) roleCombo.getSelectedItem());
        user.setFullName(fullName);

        // Calls service to add new user to db, handles errors
        boolean success = userService.createUser(user);
        if (success) { refreshTable(); clearForm(); } 
        else { JOptionPane.showMessageDialog(this, "Failed to add user. Username may already be taken.", "Error", JOptionPane.ERROR_MESSAGE); }
    
    }

    // Updates user info in db
    private void updateUser() {

        // Check if user is selected
        if (selectedUserId == null) { JOptionPane.showMessageDialog(this, "Select a user from the table first.", "No Selection", JOptionPane.WARNING_MESSAGE); return; }

        // Check if fields are not empty
        if (fullNameField.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(this, "Full name is required.", "Validation Error", JOptionPane.WARNING_MESSAGE); return; }

        // Check full name is not too long
        if (fullNameField.getText().trim().length() > 100) { JOptionPane.showMessageDialog(this, "Full name must be 100 characters or fewer.", "Validation Error", JOptionPane.WARNING_MESSAGE); return; }

        // Creates user obj
        User user = new User();
        user.setUserId(selectedUserId);
        user.setRole((String) roleCombo.getSelectedItem());
        user.setFullName(fullNameField.getText().trim());

        // Capture before clearForm() resets selectedUserId back to null below
        boolean isEditingSelf = selectedUserId == currentUser.getUserId();

        // Call service to update user details in db, checks status & handles errors
        boolean success = userService.updateUser(user);
        if (success) { refreshTable(); clearForm(); }
        else { JOptionPane.showMessageDialog(this, "Failed to update user.", "Error", JOptionPane.ERROR_MESSAGE); }

        // Force logout if the admin just demoted their own account to cashier
        if (success && isEditingSelf && "Cashier".equals(user.getRole())) {

            JOptionPane.showMessageDialog(this, "You changed your own role to Cashier. You will now be logged out.", "Role Changed", JOptionPane.INFORMATION_MESSAGE);
            Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
            
            // Force window close and new login frame
            owner.dispose();
            new LoginFrame().setVisible(true);

        }
    
    }

    // Deletes user from db
    private void deleteUser() {
        
        // Check if user is selected
        if (selectedUserId == null) { JOptionPane.showMessageDialog(this, "Select a user from the table first.", "No Selection", JOptionPane.WARNING_MESSAGE); return; }
        
        // Confirm delete before proceeding
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this user? This cannot be undone.", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        // Call service to delete from db, handle success
        boolean success = userService.deleteUser(selectedUserId);
        if (success) { refreshTable(); clearForm(); } 
        else { JOptionPane.showMessageDialog(this, "Failed to delete user.\nThey may still be referenced by past sales records.", "Error", JOptionPane.ERROR_MESSAGE); }
    
    }

}
