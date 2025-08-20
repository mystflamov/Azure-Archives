package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import db.UserAuth;

public class LoginPanel extends JPanel {
    private JTextField emailField;
    private JPasswordField passwordField;

    public LoginPanel(MainFrame mainFrame) {

        setLayout(new BorderLayout());

        // === Background Image ===
        ImageIcon bgIcon = new ImageIcon(getClass().getResource("/images/background.png"));
        Image bgImage = bgIcon.getImage();

        // Panel to hold the background image and paint it
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        backgroundPanel.setOpaque(false);
        backgroundPanel.setLayout(new BorderLayout());

        // === Header ===
        ImageIcon logo = new ImageIcon(getClass().getResource("/images/logo.png"));
        Image resizedImage = logo.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        ImageIcon resizedLogo = new ImageIcon(resizedImage);

        JLabel label1 = new JLabel("Azure Archives", resizedLogo, SwingConstants.CENTER);
        label1.setHorizontalTextPosition(JLabel.CENTER);
        label1.setVerticalTextPosition(JLabel.BOTTOM);
        label1.setForeground(new Color(29, 49, 106));
        label1.setFont(FontLoader.loadFont("/fonts/Poppins-Bold.ttf", 30f));
        label1.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel label2 = new JLabel("Library Management System", SwingConstants.CENTER);
        label2.setForeground(new Color(29, 49, 106));
        label2.setFont(FontLoader.loadFont("/fonts/Poppins-Regular.ttf", 25f));
        label2.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(50, 0, 0, 0));
        headerPanel.add(label1);
        headerPanel.add(label2);
        
        // === Banner ===
        JLabel label3 = new JLabel("Welcome back!", SwingConstants.CENTER);
        label3.setForeground(new Color(29, 49, 106));
        label3.setFont(FontLoader.loadFont("/fonts/Poppins-Bold.ttf", 40f));

        JPanel bannerPanel = new JPanel();
        bannerPanel.setOpaque(false);
        bannerPanel.setLayout(new BoxLayout(bannerPanel, BoxLayout.Y_AXIS));
        label3.setAlignmentX(Component.CENTER_ALIGNMENT);
        bannerPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        bannerPanel.add(label3);
        bannerPanel.add(Box.createVerticalStrut(20));

        // === Form Input Fields ===
        JPanel formPanel = new JPanel();
        formPanel.setOpaque(false);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        
        // Email field
        emailField = createTextField("Email");
        emailField.setAlignmentX(Component.CENTER_ALIGNMENT);
        emailField.setHorizontalAlignment(SwingConstants.LEFT);
        emailField.setPreferredSize(new Dimension(600, 50));
        emailField.setMaximumSize(new Dimension(600, 50));
        emailField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(29, 49, 106), 2),
            BorderFactory.createEmptyBorder(0, 10, 0, 10) // top, left, bottom, right
        ));
        
        // Password field
        passwordField = createPasswordField("Password");
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordField.setHorizontalAlignment(SwingConstants.LEFT);
        passwordField.setPreferredSize(new Dimension(600, 50));
        passwordField.setMaximumSize(new Dimension(600, 50));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(29, 49, 106), 2),
            BorderFactory.createEmptyBorder(0, 10, 0, 10)
        ));

        // Add fields to the form panel
        formPanel.add(emailField);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(passwordField);
        formPanel.add(Box.createVerticalStrut(50));
        
        // === Login Button ===
        JButton loginButton = new JButton("Login");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setBackground(new Color(29, 49, 106));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(FontLoader.loadFont("/fonts/Poppins-Bold.ttf", 20f));
        loginButton.setFocusPainted(false);
        loginButton.setMaximumSize(new Dimension(600, 50));

        loginButton.addActionListener(e -> {
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            if (email.equals("") || password.equals("") || email.equals("Email") || password.equals("Password")) {
                JOptionPane.showMessageDialog(this, "Please enter both email and password.", "Missing Fields", JOptionPane.WARNING_MESSAGE);
            } else {
                try {
                    String username = UserAuth.validateAccount(email, password);

                    if (username != null) {
                        UserAuth.setSession(username);
                        clearFields();
                        mainFrame.onLoginSuccess(username);
                    } else {
                        JOptionPane.showMessageDialog(this, "Invalid email or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                    }

                } catch (SQLException e1) {
                    e1.printStackTrace(); // Optional: you can show a dialog instead
                    JOptionPane.showMessageDialog(this, "Database error occurred.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        formPanel.add(loginButton);

        // === Sign-Up Link ===
        JLabel label4 = new JLabel("Don't have an account?");
        label4.setForeground(new Color(29, 49, 106));
        label4.setFont(FontLoader.loadFont("/fonts/Poppins-Regular.ttf", 20f));

        JLabel signupLabel = new JLabel(" Sign up");
        signupLabel.setForeground(new Color(29, 49, 106));
        signupLabel.setFont(FontLoader.loadFont("/fonts/Poppins-Bold.ttf", 20f));
        signupLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        signupLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                mainFrame.showPage("signup", "");
            }
        });

        JPanel signupPanel = new JPanel();
        signupPanel.setOpaque(false);
        signupPanel.setLayout(new BoxLayout(signupPanel, BoxLayout.X_AXIS));
        signupPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        signupPanel.add(label4);
        signupPanel.add(signupLabel);

        formPanel.add(Box.createVerticalStrut(30));
        formPanel.add(signupPanel);

        // === Center Wrapper: Holds banner + form ===
        JPanel centerWrapper = new JPanel();
        centerWrapper.setOpaque(false);
        centerWrapper.setLayout(new BoxLayout(centerWrapper, BoxLayout.Y_AXIS));
               
        centerWrapper.add(Box.createVerticalStrut(50));
        centerWrapper.add(bannerPanel);
        centerWrapper.add(Box.createVerticalStrut(10));
        centerWrapper.add(formPanel);
        centerWrapper.add(Box.createVerticalGlue());

        // Add components to background panel
        backgroundPanel.add(headerPanel, BorderLayout.NORTH);
        backgroundPanel.add(centerWrapper, BorderLayout.CENTER);
        
        // Add background to main panel
        add(backgroundPanel, BorderLayout.CENTER);
    }
    
    // Reset all input fields to their default placeholder text and appearance
    private void clearFields() {
        emailField.setText("Email");
        emailField.setForeground(Color.GRAY);

        passwordField.setText("Password");
        passwordField.setForeground(Color.GRAY);
        passwordField.setEchoChar((char) 0); // Show plain text placeholder
    }

    // Create text field with placeholder
    private JTextField createTextField(String placeholder) {
        JTextField textField = new JTextField(placeholder);
        textField.setForeground(Color.GRAY);
        textField.setFont(FontLoader.loadFont("/fonts/Poppins-Regular.ttf", 16f));
        textField.setBorder(BorderFactory.createLineBorder(new Color(29, 49, 106), 2));
        textField.setMaximumSize(new Dimension(600, 50));

        textField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK);
                }
            }

            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setText(placeholder);
                    textField.setForeground(Color.GRAY);
                }
            }
        });
        return textField;
    }

    // Create password field with placeholder
    private JPasswordField createPasswordField(String placeholder) {
        JPasswordField passwordField = new JPasswordField(placeholder);
        passwordField.setForeground(Color.GRAY);
        passwordField.setFont(FontLoader.loadFont("/fonts/Poppins-Regular.ttf", 16f));
        passwordField.setBorder(BorderFactory.createLineBorder(new Color(29, 49, 106), 2));
        passwordField.setMaximumSize(new Dimension(600, 50));
        passwordField.setEchoChar((char) 0);

        passwordField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                String pass = new String(passwordField.getPassword());
                if (pass.equals(placeholder)) {
                    passwordField.setText("");
                    passwordField.setForeground(Color.BLACK);
                    passwordField.setEchoChar('*');
                }
            }

            public void focusLost(FocusEvent e) {
                String pass = new String(passwordField.getPassword());
                if (pass.isEmpty()) {
                    passwordField.setText(placeholder);
                    passwordField.setForeground(Color.GRAY);
                    passwordField.setEchoChar((char) 0);
                }
            }
        });

        return passwordField;
    }

    // Main method to launch this panel inside MainFrame
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame());
    }
}