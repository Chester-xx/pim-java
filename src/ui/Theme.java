package ui;

import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import java.awt.*;

// Global theme settings, i went for a dark, blue highlights and clean look
public class Theme {

    // Colors
    public static final Color EDITOR_BACKGROUND = Color.decode("#1E1E1E");
    public static final Color SIDEBAR_BACKGROUND = Color.decode("#252526");
    public static final Color BORDER_COLOR = Color.decode("#3C3C3C");
    public static final Color FOREGROUND = Color.decode("#D4D4D4");
    public static final Color ACCENT_BLUE = Color.decode("#007ACC");
    public static final Color BUTTON_BLUE = Color.decode("#0E639C");
    public static final Color BUTTON_BLUE_HOVER = Color.decode("#1177BB");
    public static final Color SELECTION_BLUE = Color.decode("#264F78");
    public static final Color INPUT_BACKGROUND = Color.decode("#3C3C3C");
    public static final Color DANGER_RED = Color.decode("#C0392B");
    public static final Color DANGER_RED_HOVER = Color.decode("#E74C3C");

    // Font families
    public static final String UI_FONT_FAMILY = pickFontFamily("Segoe UI", Font.SANS_SERIF);
    public static final String MONO_FONT_FAMILY = pickFontFamily("Cascadia Mono", pickFontFamily("Consolas", Font.MONOSPACED));

    // Fonts
    public static final Font BASE_FONT = new Font(UI_FONT_FAMILY, Font.PLAIN, 13);
    public static final Font TITLE_FONT = new Font(UI_FONT_FAMILY, Font.BOLD, 18);
    public static final Font HEADER_FONT = new Font(UI_FONT_FAMILY, Font.BOLD, 15);
    public static final Font SUBHEADER_FONT = new Font(UI_FONT_FAMILY, Font.PLAIN, 12);
    public static final Font SMALL_FONT = new Font(UI_FONT_FAMILY, Font.PLAIN, 11);
    public static final Font MONO_FONT = new Font(MONO_FONT_FAMILY, Font.PLAIN, 13);

    // Sets font family for preferred font, fallbacks if not available on OS (common)
    private static String pickFontFamily(String preferred, String fallback) {
    
        // Check preferred is available
        for (String name : GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()) { if (name.equalsIgnoreCase(preferred)) return preferred; }
        
        // Return fallback if fails
        return fallback;
    
    }

    // Applies theme to all components in the application
    public static void apply() {
        
        // Attempts to set dep template
        try { UIManager.setLookAndFeel(new FlatDarkLaf()); } 
        catch (Exception e) { e.printStackTrace(); return; }

        // Global colors
        UIManager.put("Panel.background", EDITOR_BACKGROUND);
        UIManager.put("OptionPane.background", EDITOR_BACKGROUND);
        UIManager.put("control", EDITOR_BACKGROUND);
        UIManager.put("text", FOREGROUND);
        UIManager.put("textForeground", FOREGROUND);
        UIManager.put("Label.foreground", FOREGROUND);

        // Global inputs
        UIManager.put("TextField.background", INPUT_BACKGROUND);
        UIManager.put("PasswordField.background", INPUT_BACKGROUND);
        UIManager.put("ComboBox.background", INPUT_BACKGROUND);
        UIManager.put("Spinner.background", INPUT_BACKGROUND);
        UIManager.put("TextField.foreground", FOREGROUND);
        UIManager.put("PasswordField.foreground", FOREGROUND);
        UIManager.put("Component.borderColor", BORDER_COLOR);
        UIManager.put("Component.focusColor", ACCENT_BLUE);
        UIManager.put("Component.focusedBorderColor", ACCENT_BLUE);

        // Global buttons
        UIManager.put("Button.background", "#3C3C3C");
        UIManager.put("Button.foreground", FOREGROUND);
        UIManager.put("Button.hoverBackground", "#454545");
        UIManager.put("Button.default.background", BUTTON_BLUE);
        UIManager.put("Button.default.foreground", Color.WHITE);
        UIManager.put("Button.default.hoverBackground", BUTTON_BLUE_HOVER);

        // Global tabs
        UIManager.put("TabbedPane.background", SIDEBAR_BACKGROUND);
        UIManager.put("TabbedPane.selectedBackground", EDITOR_BACKGROUND);
        UIManager.put("TabbedPane.underlineColor", ACCENT_BLUE);
        UIManager.put("TabbedPane.hoverColor", "#2A2D2E");

        // Global Tables
        UIManager.put("Table.background", EDITOR_BACKGROUND);
        UIManager.put("Table.foreground", FOREGROUND);
        UIManager.put("Table.selectionBackground", SELECTION_BLUE);
        UIManager.put("Table.selectionForeground", Color.WHITE);
        UIManager.put("Table.gridColor", BORDER_COLOR);
        UIManager.put("TableHeader.background", SIDEBAR_BACKGROUND);
        UIManager.put("TableHeader.foreground", FOREGROUND);

        //  Border definitions
        UIManager.put("TitledBorder.titleColor", FOREGROUND);
        UIManager.put("TitledBorder.font", HEADER_FONT.deriveFont(Font.BOLD, 13f));

        // Global fonts
        UIManager.put("defaultFont", BASE_FONT);
        UIManager.put("TableHeader.font", BASE_FONT.deriveFont(Font.BOLD));
        UIManager.put("Table.rowHeight", 26); // default row height (16) looks cramped once the base font size goes up

        // Global scrollbar properties
        UIManager.put("ScrollBar.width", 14);
        UIManager.put("ScrollBar.thumbArc", 8);
        UIManager.put("ScrollBar.trackArc", 0);
        UIManager.put("ScrollBar.track", SIDEBAR_BACKGROUND);
        UIManager.put("ScrollBar.thumb", Color.decode("#5A5A5A"));
        UIManager.put("ScrollBar.hoverThumbColor", Color.decode("#787878"));
        UIManager.put("ScrollBar.pressedThumbColor", Color.decode("#909090"));
        UIManager.put("ScrollBar.minimumThumbSize", new Dimension(14, 28));
    
    }

    // Sets primary button style
    public static void stylePrimaryButton(JButton button) {
        
        button.setBackground(BUTTON_BLUE);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(button.getFont().deriveFont(Font.BOLD));

        // Set border color, i noticed a slight difference so had to change this
        button.putClientProperty("FlatLaf.style", "borderColor: " + toHex(BUTTON_BLUE) + "; focusedBorderColor: " + toHex(BUTTON_BLUE) + "; default.borderColor: " + toHex(BUTTON_BLUE) + "; default.focusedBorderColor: " + toHex(BUTTON_BLUE) + ";");
    
    }

    // Sets delete button style
    public static void styleDangerButton(JButton button) {
        
        button.setBackground(DANGER_RED);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.putClientProperty("FlatLaf.style", "borderColor: " + toHex(DANGER_RED) + "; focusedBorderColor: " + toHex(DANGER_RED) + ";");
    
    }

    // Converts RGB color to hexadecimal string for color conversion
    private static String toHex(Color c) { return String.format("#%02X%02X%02X", c.getRed(), c.getGreen(), c.getBlue()); }

}
