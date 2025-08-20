package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import db.BorrowedBook;
import db.BorrowedBooksManager;
import db.UserAuth;

public class HomePanel extends JPanel {
    public List<BorrowedBook> borrowedBooks = new ArrayList<>();
    public List<BorrowedBook> historyBorrowedBooks = new ArrayList<>();
   
    // Constructor to display HomePanel
    public HomePanel(MainFrame mainFrame, String username, String fullName) {
        try {
            borrowedBooks = BorrowedBooksManager.fetchCurrentBorrowedBooks(username);
            historyBorrowedBooks = BorrowedBooksManager.fetchBorrowedBooks(username);
        } catch (Exception e) {
            System.out.println("HomePanel: borrowed and history not working");
            System.out.println(e);
        }
        
        setBackground(new Color(245, 247, 253)); // Light blue-gray background
        setLayout(new BorderLayout());

        // Main content wrapper with padding and vertical layout
        JPanel mainContent = new JPanel();
        mainContent.setBackground(new Color(245, 247, 253));
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setBorder(BorderFactory.createEmptyBorder(80, 80, 80, 80));
        add(mainContent, BorderLayout.CENTER);

        // === Welcome Section ===
        JPanel welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.setBackground(Color.WHITE);
        welcomePanel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 300));
        welcomePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
        welcomePanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 0, 50));

        // Greeting label with custom font
        JLabel heading = new JLabel("Hello, " + fullName + "!");
        heading.setFont(FontLoader.loadFont("/fonts/Poppins-Bold.ttf", 35f));
        heading.setForeground(new Color(29, 49, 106));
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Description below greeting
        JTextArea subtext = new JTextArea("At Azure Archives, knowledge meets accessibility — your library, anytime, anywhere. Whether you’re here to study, research, or just browse, we provide a seamless experience to find and borrow books with ease.");
        subtext.setFont(FontLoader.loadFont("/fonts/Poppins-Regular.ttf", 20f));
        subtext.setForeground(new Color(29, 49, 106));
        subtext.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtext.setOpaque(false); // Transparent like JLabel
        subtext.setEditable(false); // Prevent typing
        subtext.setFocusable(false); // Ignore tab focus
        subtext.setLineWrap(true);
        subtext.setWrapStyleWord(true);
        
        // Container for heading and subtext
        JPanel welcomeText = new JPanel();
        welcomeText.setLayout(new BoxLayout(welcomeText, BoxLayout.Y_AXIS));
        welcomeText.setOpaque(false); // So it inherits background from welcomePanel
        welcomeText.add(heading);
        welcomeText.add(Box.createVerticalStrut(5));
        welcomeText.add(subtext);
        
        welcomePanel.add(welcomeText, BorderLayout.CENTER);

        // Load and scale the chart image
        JLabel chartLabel = new JLabel();
        ImageIcon chartIcon = new ImageIcon(getClass().getResource("/images/chart.png"));
        Image scaledImage = chartIcon.getImage().getScaledInstance(320, 250, Image.SCALE_SMOOTH);
        chartLabel.setIcon(new ImageIcon(scaledImage));

        // Container for chart image
        JPanel chartBox = new JPanel(new BorderLayout());
        chartBox.setPreferredSize(new Dimension(320, 250));  // Important!
        chartBox.setOpaque(false);  // Transparent background
        chartBox.add(chartLabel, BorderLayout.CENTER);
        
        welcomePanel.add(chartBox, BorderLayout.EAST);

        // Add welcome panel to main content
        mainContent.add(welcomePanel);
        mainContent.add(Box.createVerticalStrut(50));

        // === Stats Section (Currently, Total, Overdue) ===
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        statsPanel.setOpaque(false);
        statsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        // Add stat cards to statsPanel
        statsPanel.add(createStatCard("Currently Borrowed", borrowedBooks.size() + " books", new Color(239, 180, 147), "/icons/currently-borrowed.png"));
        statsPanel.add(createStatCard("Total Borrowed", historyBorrowedBooks.size() + " books", new Color(242, 220, 142), "/icons/total-borrowed.png"));
        statsPanel.add(createStatCard("Overdue", "0 book", new Color(227, 191, 225), "/icons/overdue.png"));

        mainContent.add(statsPanel);
        
        mainContent.add(Box.createVerticalStrut(50));

        // === Authors Section ===
        JPanel authorPanel = new JPanel();
        authorPanel.setLayout(new BoxLayout(authorPanel, BoxLayout.Y_AXIS));
        authorPanel.setOpaque(true);
        authorPanel.setBackground(Color.WHITE);
        authorPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // Title with quote icons
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);

        ImageIcon leftQuoteIcon = new ImageIcon(getClass().getResource("/images/quote-left.png"));
        ImageIcon rightQuoteIcon = new ImageIcon(getClass().getResource("/images/quote-right.png"));
        Image leftImg = leftQuoteIcon.getImage().getScaledInstance(40, 25, Image.SCALE_SMOOTH);
        Image rightImg = rightQuoteIcon.getImage().getScaledInstance(40, 25, Image.SCALE_SMOOTH);

        JLabel leftQuoteLabel = new JLabel(new ImageIcon(leftImg));
        JLabel rightQuoteLabel = new JLabel(new ImageIcon(rightImg));

        JLabel authorTitle = new JLabel("Authors This Month", SwingConstants.CENTER);
        authorTitle.setFont(FontLoader.loadFont("/fonts/Poppins-Bold.ttf", 25f));
        authorTitle.setForeground(new Color(29, 49, 106));

        titlePanel.add(leftQuoteLabel, BorderLayout.WEST);
        titlePanel.add(authorTitle, BorderLayout.CENTER);
        titlePanel.add(rightQuoteLabel, BorderLayout.EAST);

        authorPanel.add(titlePanel);
        authorPanel.add(Box.createVerticalStrut(10));  // bigger spacing

        // Horizontal row for author cards
        JPanel authorsRowContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        authorsRowContainer.setOpaque(false);
        
        JPanel authorsRow = new JPanel();
        authorsRow.setLayout(new BoxLayout(authorsRow, BoxLayout.X_AXIS));
        authorsRow.setOpaque(false);
        authorsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80)); // fix height
        authorsRow.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add author cards and separators
        authorsRow.add(createAuthorCard("Emily Henry", "22 books"));
        authorsRow.add(Box.createRigidArea(new Dimension(50, 0))); // 15px horizontal space
        authorsRow.add(createVerticalSeparator());
        authorsRow.add(Box.createRigidArea(new Dimension(50, 0)));

        authorsRow.add(createAuthorCard("Rebecca Yarros", "31 books"));
        authorsRow.add(Box.createRigidArea(new Dimension(50, 0)));
        authorsRow.add(createVerticalSeparator());
        authorsRow.add(Box.createRigidArea(new Dimension(50, 0)));

        authorsRow.add(createAuthorCard("Freida Mcfadden", "27 books"));
        authorsRow.add(Box.createRigidArea(new Dimension(50, 0)));
        authorsRow.add(createVerticalSeparator());
        authorsRow.add(Box.createRigidArea(new Dimension(50, 0)));

        authorsRow.add(createAuthorCard("Kristine Hannah", "34 books"));

        authorsRowContainer.add(authorsRow);
        authorPanel.add(authorsRowContainer);

        mainContent.add(authorPanel);
    }

    // Helper method to create stat cards (e.g., Currently Borrowed)
    private JPanel createStatCard(String label, String value, Color color, String iconPath) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 10, 0, 0, color),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Load and scale the icon image
        ImageIcon rawIcon = new ImageIcon(getClass().getResource(iconPath));
        Image scaled = rawIcon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
        JLabel iconLabel = new JLabel(new ImageIcon(scaled));
        iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10)); // space between icon and text

        // Text area (label + value)
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = new JLabel(label);
        title.setFont(FontLoader.loadFont("/fonts/Poppins-Regular.ttf", 15f));
        title.setForeground(Color.GRAY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel stat = new JLabel(value);
        stat.setFont(FontLoader.loadFont("/fonts/Poppins-Bold.ttf", 20f));
        stat.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        textPanel.add(title);
        textPanel.add(stat);

        // Wrap icon + text into one row
        JPanel contentWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        contentWrapper.setOpaque(false);
        contentWrapper.add(iconLabel);
        contentWrapper.add(textPanel);
        
        card.add(contentWrapper, BorderLayout.CENTER);

        return card;
    }

    // Helper method to create author info cards
    private JPanel createAuthorCard(String name, String bookCount) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);

        JLabel authorName = new JLabel(name);
        authorName.setFont(FontLoader.loadFont("/fonts/Poppins-Regular.ttf", 15f));
        authorName.setForeground(new Color(29, 49, 106));
        authorName.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel count = new JLabel(bookCount);
        count.setFont(FontLoader.loadFont("/fonts/Poppins-Regular.ttf", 12f));
        count.setForeground(new Color(29, 49, 106));
        count.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(authorName);
        card.add(count);

        return card;
    }
    
    // Helper method to create vertical separator line between author cards
    private Component createVerticalSeparator() {
        JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
        separator.setPreferredSize(new Dimension(2, 60));  // thickness and height
        separator.setMaximumSize(new Dimension(2, 60));
        separator.setForeground(new Color(200, 200, 200));  // light gray line
        return separator;
    }

    // Main method to launch this panel inside MainFrame
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame());
    }
}