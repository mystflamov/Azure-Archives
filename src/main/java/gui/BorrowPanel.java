package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import db.BorrowedBook;
import db.BorrowedBooksManager;
import db.InquiryManager;
import db.UserAuth;

public class BorrowPanel extends JPanel{
    public String username = UserAuth.getUsername();
    public List<BorrowedBook> borrowedBooks = new ArrayList<>();
    public List<BorrowedBook> historyBorrowedBooks = new ArrayList<>();
    public MainFrame mainFrame;
    
    // Constructor to display BorrowPanel
    public BorrowPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
         try {
            borrowedBooks = BorrowedBooksManager.fetchCurrentBorrowedBooks(username);
        } catch (Exception e) {
            System.out.println("Borrow Panel: currentBorrowedBooks not found");
            System.out.println(e);
        }

        setBackground(new Color(245, 247, 253));
        setLayout(new BorderLayout());

        // Main content wrapper with padding and vertical layout
        JPanel mainContent = new JPanel();
        mainContent.setBackground(new Color(245, 247, 253));
        mainContent.setBorder(BorderFactory.createEmptyBorder(80, 80, 80, 80));
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));

        // Central panel to group elements
        JPanel centerContent = new JPanel();
        centerContent.setLayout(new BoxLayout(centerContent, BoxLayout.Y_AXIS));
        
        // === Currently Borrowed Section ===
        JLabel currentLabel = new JLabel("Currently Borrowed");
        currentLabel.setFont(FontLoader.loadFont("/fonts/Poppins-Bold.ttf", 35f));
        currentLabel.setForeground(new Color(29, 49, 106));
        currentLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainContent.add(currentLabel);
        mainContent.add(Box.createRigidArea(new Dimension(0, 20)));

        // Add horizontally aligned book cards
        JPanel currentBooks = new JPanel();
        currentBooks.setLayout(new GridLayout(1, 3, 50, 50));
        currentBooks.setBackground(new Color(245, 247, 253));
        currentBooks.setAlignmentX(Component.LEFT_ALIGNMENT);
        currentBooks.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        // Add sample borrowed books
        currentBooks = displayCurrentBooks(currentBooks, borrowedBooks);
        
        mainContent.add(currentBooks);
        mainContent.add(Box.createRigidArea(new Dimension(0, 50)));

        // === Borrowing History Section ===
        JLabel historyLabel = new JLabel("Borrowing History");
        historyLabel.setFont(FontLoader.loadFont("/fonts/Poppins-Bold.ttf", 35f));
        historyLabel.setForeground(new Color(29, 49, 106));
        historyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainContent.add(historyLabel);
        mainContent.add(Box.createRigidArea(new Dimension(0, 10)));

        URL iconURL = getClass().getResource("/icons/search.png");
        ImageIcon icon = new ImageIcon(new ImageIcon(iconURL).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
        
        try {
            // Make sample data for history
            historyBorrowedBooks = BorrowedBooksManager.fetchBorrowedBooks(username);
        } catch (SQLException ex) {
        }

        JPanel historyPanel = new JPanel();
        historyPanel.setLayout(new BoxLayout(historyPanel, BoxLayout.Y_AXIS));
        historyPanel.setBackground(new Color(245, 247, 253));
        historyPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Create and add each history row
        for (BorrowedBook book : historyBorrowedBooks) {
            historyPanel.add(createHistoryRow(book));
            
            historyPanel.add(Box.createRigidArea(new Dimension(0, 15))); // spacing between rows
        }

        mainContent.add(historyPanel);

        // Scroll pane to make content scrollable
        JScrollPane scrollPane = new JScrollPane(mainContent);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Smooth scrolling
        add(scrollPane, BorderLayout.CENTER);

        setVisible(true);
    }
    
    private ImageIcon getCroppedImageIcon(ImageIcon originalIcon, int targetWidth, int targetHeight) {
        Image originalImage = originalIcon.getImage();
        int imgWidth = originalImage.getWidth(null);
        int imgHeight = originalImage.getHeight(null);

        double targetRatio = (double) targetWidth / targetHeight;
        double imgRatio = (double) imgWidth / imgHeight;

        int cropWidth = imgWidth;
        int cropHeight = imgHeight;

        if (imgRatio > targetRatio) {
            cropWidth = (int) (imgHeight * targetRatio);
        } else {
            cropHeight = (int) (imgWidth / targetRatio);
        }

        int x = (imgWidth - cropWidth) / 2;
        int y = (imgHeight - cropHeight) / 2;

        BufferedImage cropped = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = cropped.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(originalImage, 0, 0, targetWidth, targetHeight, x, y, x + cropWidth, y + cropHeight, null);
        g2d.dispose();

        return new ImageIcon(cropped);
    }
    
    // Helper method to load image icons from the resource folder
    private ImageIcon getImageIconFromResource(String path) {
        URL resource = getClass().getResource(path);
        if (resource == null) {
            System.err.println("Image not found at: " + path);
            return new ImageIcon();
        }
        return new ImageIcon(resource);
    }

    private JPanel displayCurrentBooks(JPanel currentBooks, List<BorrowedBook> borrowedBooks){
        for (BorrowedBook book : borrowedBooks) {
            String imagePath =  "/images/" + book.getTitle().toLowerCase().replace(" ", "_").replace(":", "").replace(",", "") + ".jpg";
            ImageIcon imageIcon = getImageIconFromResource(imagePath);
            currentBooks.add(createBookCard(
                book.getBorrowedBooksId(),
                book.getBookId(),
                book.getTitle(),
                book.getAuthor(),
                book.getDateBorrowed().toString(),
                book.getDueDate().toString(),
                imageIcon
            ));
        }

        return currentBooks;
    }

    // Helper method to create a card component for a currently borrowed book
    private JPanel createBookCard(int borrowedBooksId, int bookId, String title, String author, String borrowed, String due, ImageIcon imageIcon) {
        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(220, 250));
        card.setMaximumSize(new Dimension(220, 250));
        card.setLayout(new BorderLayout(0, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        // Book cover image
        ImageIcon icon = getCroppedImageIcon(imageIcon, 350, 350);
        JLabel imageLabel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (icon != null) {
                    Image img = icon.getImage();
                    g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
                }
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(180, 180); // Fixed size to fill space vertically
            }
        };

        // Book details
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setAlignmentX(Component.LEFT_ALIGNMENT);
        info.setBackground(Color.WHITE);

        JPanel infoContent = new JPanel();
        infoContent.setLayout(new BoxLayout(infoContent, BoxLayout.Y_AXIS));
        infoContent.setBackground(Color.WHITE);
        infoContent.setBorder(new EmptyBorder(15, 15, 15, 15));
        infoContent.setAlignmentX(Component.LEFT_ALIGNMENT);
               
        // Title label
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(FontLoader.loadFont("/fonts/Poppins-Bold.ttf", 20f));
        titleLabel.setForeground(new Color(29, 49, 106));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Author label
        JLabel authorLabel = new JLabel(author);
        authorLabel.setFont(FontLoader.loadFont("/fonts/Poppins-Regular.ttf", 15f));
        authorLabel.setForeground(new Color(164, 175, 204));
        authorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Borrowed date
        JPanel borrowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        borrowPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        borrowPanel.setOpaque(false);

        JLabel borrowEmoji = new JLabel("📅");
        borrowEmoji.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 10));

        JLabel borrowText = new JLabel("Borrowed: " + borrowed);
        borrowText.setFont(FontLoader.loadFont("/fonts/Poppins-Regular.ttf", 15f));
        borrowText.setForeground(new Color(164, 175, 204));

        borrowPanel.add(borrowEmoji);
        borrowPanel.add(borrowText);

        // Due date
        JPanel duePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        duePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        duePanel.setOpaque(false); // <-- Fix: was borrowPanel.setOpaque(false)

        JLabel dueEmoji = new JLabel("⏰");
        dueEmoji.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 10));

        JLabel dueText = new JLabel("Due: " + due);
        dueText.setFont(FontLoader.loadFont("/fonts/Poppins-Regular.ttf", 15f));
        dueText.setForeground(new Color(164, 175, 204));

        duePanel.add(dueEmoji);
        duePanel.add(dueText);

        // Add everything to info panel
        infoContent.add(titleLabel);
        infoContent.add(authorLabel);
        infoContent.add(Box.createRigidArea(new Dimension(0, 10)));
        infoContent.add(borrowPanel);
        infoContent.add(duePanel);
        infoContent.add(Box.createVerticalGlue());
        
        // Create a wrapper panel for the return button with no padding and max width
        JPanel buttonWrapper = new JPanel(new BorderLayout());
        buttonWrapper.setBackground(Color.WHITE); // match background
        buttonWrapper.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        buttonWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        
        // Return button
        JButton returnButton = new JButton("Return");
        returnButton.setBackground(new Color(201, 210, 242));
        returnButton.setForeground(new Color(30, 42, 103));
        returnButton.setFont(FontLoader.loadFont("/fonts/Poppins-Bold.ttf", 14f));
        returnButton.setFocusPainted(false);

        returnButton.addActionListener(e -> {
            borrowedBooks = InquiryManager.returnBook(borrowedBooks, username, bookId, borrowedBooksId);
            JOptionPane.showMessageDialog(this, "Book returned.", "borrowedBooksId: " + borrowedBooksId, JOptionPane.INFORMATION_MESSAGE);
            mainFrame.showPage("borrow", "");
        });

        // Make button fill horizontally inside wrapper
        buttonWrapper.add(returnButton, BorderLayout.CENTER);
        buttonWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        // Add button wrapper (no padding) to info panel
        info.add(infoContent);
        info.add(Box.createVerticalStrut(27));
        info.add(buttonWrapper);

        // Assemble final card
        card.add(imageLabel, BorderLayout.WEST);
        card.add(info, BorderLayout.CENTER);

        return card;
    }

    // Helper method to create a single row for a borrowed book in history
    private JPanel createHistoryRow(BorrowedBook borrowedBook) {
        JPanel row = new JPanel();
        row.setLayout(new BorderLayout(20, 0));
        row.setBackground(Color.WHITE);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        row.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Book title and author
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(Color.WHITE);
        titlePanel.setPreferredSize(new Dimension(450, 50));

        JLabel title = new JLabel(borrowedBook.getTitle());
        title.setFont(FontLoader.loadFont("/fonts/Poppins-Bold.ttf", 20f));
        title.setForeground(new Color(29, 49, 106));

        JLabel author = new JLabel(borrowedBook.getAuthor());
        author.setFont(FontLoader.loadFont("/fonts/Poppins-Regular.ttf", 15f));
        author.setForeground(new Color(164, 175, 204));

        titlePanel.add(title);
        titlePanel.add(author);

        // Borrowed and due dates
        JPanel datesPanel = new JPanel();
        datesPanel.setLayout(new BoxLayout(datesPanel, BoxLayout.Y_AXIS));
        datesPanel.setBackground(Color.WHITE);
        datesPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
        datesPanel.setPreferredSize(new Dimension(240, 60)); // <- consistent width

        JPanel borrowedLine = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        borrowedLine.setBackground(Color.WHITE);
        JLabel borrowedEmoji = new JLabel("📅");
        borrowedEmoji.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        JLabel borrowedText = new JLabel("Borrowed on " + borrowedBook.getDateBorrowed());
        borrowedText.setFont(FontLoader.loadFont("/fonts/Poppins-Regular.ttf", 14f));
        borrowedText.setForeground(new Color(164, 175, 204));
        borrowedLine.add(borrowedEmoji);
        borrowedLine.add(borrowedText);

        JPanel dueLine = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        dueLine.setBackground(Color.WHITE);
        JLabel dueEmoji = new JLabel("⏰");
        dueEmoji.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        JLabel dueText = new JLabel("Due by " + borrowedBook.getDueDate());
        dueText.setFont(FontLoader.loadFont("/fonts/Poppins-Regular.ttf", 14f));
        dueText.setForeground(new Color(164, 175, 204));
        dueLine.add(dueEmoji);
        dueLine.add(dueText);

        datesPanel.add(borrowedLine);
        datesPanel.add(dueLine);

        // Status badge (Returned / Overdue)
        boolean isReturned = borrowedBook.isReturned();
        JLabel status = new JLabel();
        status.setOpaque(true);
        status.setBorder(new EmptyBorder(5, 12, 5, 12));
        status.setFont(FontLoader.loadFont("/fonts/Poppins-Bold.ttf", 14f));
        status.setHorizontalAlignment(SwingConstants.CENTER);
        status.setPreferredSize(new Dimension(100, 50));
        if (isReturned) {
            status.setText("Returned");
            status.setBackground(new Color(177, 232, 181));
            status.setForeground(new Color(90, 154, 95));
        } else {
            status.setText("Not Returned");
            status.setBackground(new Color(242, 195, 195));
            status.setForeground(new Color(150, 76, 76));
        }

        // Assemble final row
        row.add(titlePanel, BorderLayout.WEST);
        row.add(datesPanel, BorderLayout.CENTER);
        row.add(status, BorderLayout.EAST);

        return row;
    }

    // Main method to launch this panel inside MainFrame
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame());
    }
}