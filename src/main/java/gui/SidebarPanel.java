package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

public class SidebarPanel extends JPanel {
    private MainFrame mainFrame;
    private JPanel navButtonsPanel;

    // Constructor to display SidebarPanel
    public SidebarPanel(MainFrame mainFrame, String selectedSection) {
        this.mainFrame = mainFrame;

        setPreferredSize(new Dimension(300, 0));
        setBackground(new Color(211, 219, 245));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS)); // Change to vertical stacking

        // === Top Panel (Logo Container) ===
        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(29, 49, 106)); // Deep blue
        topPanel.setPreferredSize(new Dimension(300, 150)); // Fixed height
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        // Wrap topPanel in a container to enforce max height
        JPanel topWrapper = new JPanel();
        topWrapper.setLayout(new BorderLayout());
        topWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        topWrapper.setPreferredSize(new Dimension(300, 150));
        topWrapper.add(topPanel, BorderLayout.CENTER);
        
        // Load and resize the logo
        ImageIcon originalLogo = new ImageIcon(getClass().getResource("/images/logo.png"));
        Image img = originalLogo.getImage();
        Image resizedImg = img.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        ImageIcon resizedLogo = new ImageIcon(resizedImg);

        JLabel logo = new JLabel(resizedLogo);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        topPanel.add(logo);

        // === Branding Text ===
        JPanel brandingPanel = new JPanel();
        brandingPanel.setBackground(new Color(211, 219, 245));
        brandingPanel.setLayout(new BoxLayout(brandingPanel, BoxLayout.Y_AXIS));
        brandingPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));

        // Add title and tagline labels
        JLabel title = new JLabel("Azure Archives");
        title.setFont(FontLoader.loadFont("/fonts/Poppins-Bold.ttf", 30f));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setForeground(new Color(29, 49, 106));

        JLabel line1 = new JLabel("Imagine and Immerse");
        line1.setFont(FontLoader.loadFont("/fonts/Poppins-Regular.ttf", 20f));
        line1.setAlignmentX(Component.CENTER_ALIGNMENT);
        line1.setForeground(new Color(29, 49, 106));

        JLabel line2 = new JLabel("Yourselves through");
        line2.setFont(FontLoader.loadFont("/fonts/Poppins-Regular.ttf", 20f));
        line2.setAlignmentX(Component.CENTER_ALIGNMENT);
        line2.setForeground(new Color(29, 49, 106));

        JLabel line3 = new JLabel("Reading");
        line3.setFont(FontLoader.loadFont("/fonts/Poppins-Regular.ttf", 20f));
        line3.setAlignmentX(Component.CENTER_ALIGNMENT);
        line3.setForeground(new Color(29, 49, 106));

        // Add labels to branding panel with small spacing
        brandingPanel.add(title);
        brandingPanel.add(line1);
        brandingPanel.add(Box.createRigidArea(new Dimension(0, -5)));
        brandingPanel.add(line2);
        brandingPanel.add(Box.createRigidArea(new Dimension(0, -5)));
        brandingPanel.add(line3);

        // === Navigation Section ===
        JPanel navWrapper = new JPanel();
        navWrapper.setBackground(new Color(211, 219, 245));
        navWrapper.setLayout(new BoxLayout(navWrapper, BoxLayout.Y_AXIS));
        navWrapper.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0)); // push down from top slightly

        // Inner panel for nav buttons
        navButtonsPanel = new JPanel();
        navButtonsPanel.setBackground(new Color(211, 219, 245));
        navButtonsPanel.setLayout(new BoxLayout(navButtonsPanel, BoxLayout.Y_AXIS));
        navButtonsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // Create and add navigation buttons
        String[] navItems = {"Home", "Books", "Borrow"};
        for (String text : navItems) {
            final String pageName = text.toLowerCase();
            
            // Load the icon from resources
            String iconPath = "/icons/" + text.toLowerCase() + ".png";
            ImageIcon icon = new ImageIcon(getClass().getResource(iconPath));
            Image scaled = icon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
            ImageIcon resizedIcon = new ImageIcon(scaled);

            // Create button with icon and label
            JButton navButton = new JButton(text, resizedIcon);

            // Set navigation logic on click
            navButton.addActionListener(e -> {
                mainFrame.showPage(pageName, ""); // Show page by card name
            });
            
            // Customize button layout and appearance
            navButton.setHorizontalAlignment(SwingConstants.CENTER); // Align content to left
            navButton.setHorizontalTextPosition(SwingConstants.RIGHT); // Text on the right of icon
            navButton.setVerticalTextPosition(SwingConstants.CENTER); // Vertically center text
            navButton.setIconTextGap(15); // Space between icon and text

            navButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80)); // Consistent height
            navButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Center in panel
            navButton.setFont(FontLoader.loadFont("/fonts/Poppins-Regular.ttf", 18f));
            navButton.setFocusPainted(false);
            navButton.setBackground(new Color(211, 219, 245));
            navButton.setForeground(new Color(29, 49, 106));
            navButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
            
            // Add button and spacing
            navButtonsPanel.add(navButton);
            navButtonsPanel.add(Box.createRigidArea(new Dimension(0, 30)));
            
            // Highlight selected section
            if (text.equalsIgnoreCase(selectedSection)) {
                navButton.setBackground(new Color(245, 247, 253));
                navButton.setForeground(new Color(61, 90, 128));
            }
        }

        navWrapper.add(navButtonsPanel);

        // Add all major components to sidebar
        add(topWrapper);     // Logo
        add(brandingPanel);  // App title and tagline
        add(navWrapper);     // Navigation buttons
        
        // === Logout Button Section ===
        JPanel logoutPanel = new JPanel();
        logoutPanel.setBackground(new Color(211, 219, 245));
        logoutPanel.setLayout(new BoxLayout(logoutPanel, BoxLayout.Y_AXIS));
        logoutPanel.setBorder(BorderFactory.createEmptyBorder(50, 0, 20, 0)); // Padding from bottom

        // Load logout icon
        ImageIcon logoutIcon = new ImageIcon(getClass().getResource("/icons/logout.png"));
        Image logoutImg = logoutIcon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
        ImageIcon resizedLogoutIcon = new ImageIcon(logoutImg);

        // Create logout button styled like nav buttons
        JButton logoutButton = new JButton("Logout", resizedLogoutIcon);
        logoutButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        logoutButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutButton.setFont(FontLoader.loadFont("/fonts/Poppins-Regular.ttf", 18f));
        logoutButton.setFocusPainted(false);
        logoutButton.setBackground(new Color(211, 219, 245));
        logoutButton.setForeground(new Color(29, 49, 106));
        logoutButton.setHorizontalAlignment(SwingConstants.CENTER);
        logoutButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        logoutButton.setVerticalTextPosition(SwingConstants.CENTER);
        logoutButton.setIconTextGap(15);
        logoutButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        // Add confirmation dialog on click
        logoutButton.addActionListener(e -> {
            UIManager.put("Button.focus", new java.awt.Color(0, 0, 0, 0));
            
            int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to log out?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                mainFrame.logout();
            }
        });

        logoutPanel.add(logoutButton);
        add(Box.createRigidArea(new Dimension(0, 30))); // Push logout button to bottom
        add(logoutPanel);
    }
    
    // Helper method to updates the highlighted button in the navigation panel
    public void updateSelectedSection(String selectedSection) {
        Component[] components = navButtonsPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JButton) {
                JButton btn = (JButton) comp;
                
                // Highlight the selected button
                if (btn.getText().equalsIgnoreCase(selectedSection)) {
                    btn.setBackground(new Color(245, 247, 253));
                    btn.setForeground(new Color(61, 90, 128));
                } else {
                    // Reset other buttons
                    btn.setBackground(new Color(211, 219, 245));
                    btn.setForeground(new Color(29, 49, 106));
                }
            }
        }
    }
}