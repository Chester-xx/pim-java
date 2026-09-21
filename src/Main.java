import db.DBConnection;
import ui.LoginFrame;
import ui.Theme;

import javax.swing.*;
import java.sql.Connection;

// Entry point for pim
public class Main {
    public static void main(String[] args) {

        // Apply theme cascading
        Theme.apply();

        // run on edt
        SwingUtilities.invokeLater(() -> {

            // call db connection to show login frame if success
            // method is async for swing components to load correctly5
            try (Connection conn = DBConnection.getConnection()) {

                // I know seperate login screens were wanted, but role identification is handled anyway so i dont think it matters
                System.out.println("Database connection successful: " + conn.getCatalog());
                new LoginFrame().setVisible(true);

            } catch (Exception e) { e.printStackTrace(); JOptionPane.showMessageDialog(null, "Failed to connect to the database:\n" + e.getMessage(), "DB Connection Error", JOptionPane.ERROR_MESSAGE); }

        });

    }

}
