package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.image.BufferedImage;
import java.net.URL;
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
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import db.App;
import db.Book;
import db.BorrowedBook;
import db.BorrowedBooksManager;
import db.InquiryManager;
import db.UserAuth;

public class BooksPanel extends JPanel {
    public List<Book> books = new ArrayList<>();

    public String username = UserAuth.getUsername();
    public List<BorrowedBook> borrowedBooks = new ArrayList<>();
    public MainFrame mainFrame;

    public BooksPanel(MainFrame mainFrame, String searchedBook) {
        if (searchedBook.equals("") || searchedBook.equals("Search any books...")) {
            books = App.displayBooks();
        } else {
            books = App.searchedBooks(searchedBook);
        }
        this.mainFrame = mainFrame;
        try {
            borrowedBooks = BorrowedBooksManager.fetchCurrentBorrowedBooks(username);
        } catch (Exception e) {
            System.out.println("BooksPanel: borrowedBooks not working");
            System.out.println(e);
        }

        setBackground(new Color(245, 247, 253));
        setLayout(new BorderLayout());

        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(new Color(245, 247, 253));
        mainContent.setBorder(BorderFactory.createEmptyBorder(80, 80, 80, 80));
        add(mainContent, BorderLayout.CENTER);

        JTextField searchField = new JTextField("Search any books...");
        searchField.setFont(FontLoader.loadFont("/fonts/Poppins-Regular.ttf", 14f));
        searchField.setForeground(Color.GRAY);
        searchField.setPreferredSize(new Dimension(0, 40));
        searchField.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("Search any books...")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setForeground(Color.GRAY);
                    searchField.setText("Search any books...");
                }
            }
        });

        URL iconURL = getClass().getResource("/icons/search.png");
        ImageIcon icon = new ImageIcon(new ImageIcon(iconURL).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));

        JButton searchButton = new JButton(icon);
        searchButton.setPreferredSize(new Dimension(60, 40));
        searchButton.setBackground(new Color(211, 219, 245));
        searchButton.setBorder(BorderFactory.createMatteBorder(0, 2, 0, 0, new Color(29, 49, 106)));
        searchButton.setFocusPainted(false);
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.addActionListener(e -> {
            String title = searchField.getText();
            mainFrame.showPage("books", title);
        });

        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBackground(new Color(245, 247, 253));
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(29, 49, 106), 2, true),
                BorderFactory.createEmptyBorder()
        ));
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);

        JPanel searchWrapper = new JPanel(new BorderLayout());
        searchWrapper.setBackground(new Color(245, 247, 253));
        searchWrapper.add(searchPanel, BorderLayout.CENTER);

        JPanel searchAndSpacing = new JPanel();
        searchAndSpacing.setLayout(new BoxLayout(searchAndSpacing, BoxLayout.Y_AXIS));
        searchAndSpacing.setBackground(new Color(245, 247, 253));
        searchAndSpacing.add(searchWrapper);
        searchAndSpacing.add(Box.createVerticalStrut(50));

        final int maxContentWidth = 1000; // change as needed

        // --- New center wrapper with scroll-friendly layout ---
        JPanel centeredPanel = new JPanel(new BorderLayout());
        centeredPanel.setOpaque(false);

        JPanel innerPanel = new JPanel();
        innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.Y_AXIS));
        innerPanel.setOpaque(false);
        innerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 40, 0));

        // Adjust width of the components to maxContentWidth
        searchAndSpacing.setMaximumSize(new Dimension(maxContentWidth, Integer.MAX_VALUE));
        searchAndSpacing.setPreferredSize(new Dimension(maxContentWidth, searchAndSpacing.getPreferredSize().height));
        innerPanel.add(searchAndSpacing);
        innerPanel.add(Box.createVerticalStrut(20));

        // Book grid
        JPanel bookGrid = new JPanel(new GridBagLayout());
        bookGrid.setOpaque(false);
        bookGrid.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 55));

        Insets cardInsets = new Insets(0, 0, 40, 40);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = cardInsets;

        int cols = 4;
        for (int i = 0; i < books.size(); i++) {
            Book book = books.get(i);
            JPanel card = createBookCard(
                book.bookId,
                book.bookTitle,
                book.bookAuthor,
                "",
                book.isAvailable,
                getImageIconFromResource("/images/" + book.bookTitle.toLowerCase().replace(" ", "_").replace(":", "").replace(",", "") + ".jpg")
            );

            int row = i / cols;
            int col = i % cols;
            boolean isLastRow = row == (books.size() - 1) / cols;

            gbc.gridx = col;
            gbc.gridy = row;
            gbc.insets = new Insets(0, 0, isLastRow ? 0 : 40, (col == cols - 1) ? 0 : 40);

            bookGrid.add(card, gbc);
        }

        // Just the book grid in scrollable container
        JScrollPane scrollPane = new JScrollPane(bookGrid);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setPreferredSize(new Dimension(maxContentWidth, 600));
        scrollPane.setMaximumSize(new Dimension(maxContentWidth, Integer.MAX_VALUE));

        // Put search above the scrollPane, not inside it
        centeredPanel.add(searchAndSpacing, BorderLayout.NORTH);
        centeredPanel.add(scrollPane, BorderLayout.CENTER);

        mainContent.add(centeredPanel, BorderLayout.CENTER);

        mainContent.revalidate();
        mainContent.repaint();
    }

    private JPanel createBookCard(int bookId, String title, String author, String genre, boolean available, ImageIcon imageIcon) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        card.setPreferredSize(new Dimension(220, 290));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);

        ImageIcon croppedIcon = getCroppedImageIcon(imageIcon, 350, 350);
        JLabel imgLabel = new JLabel(croppedIcon);
        imgLabel.setHorizontalAlignment(JLabel.CENTER);
        imgLabel.setVerticalAlignment(JLabel.CENTER);
        imgLabel.setPreferredSize(new Dimension(200, 180));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(FontLoader.loadFont("/fonts/Poppins-Bold.ttf", 16f));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel authorLabel = new JLabel(author);
        authorLabel.setFont(FontLoader.loadFont("/fonts/Poppins-Regular.ttf", 14f));
        authorLabel.setForeground(Color.GRAY);
        authorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel genreLabel = new JLabel(genre);
        genreLabel.setOpaque(true);
        genreLabel.setBackground(new Color(30, 42, 103));
        genreLabel.setForeground(Color.WHITE);
        genreLabel.setFont(FontLoader.loadFont("/fonts/Poppins-Regular.ttf", 12f));
        genreLabel.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
        genreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton actionButton = new JButton(available ? "Borrow" : "Unavailable");
        actionButton.setEnabled(available);
        actionButton.setBackground(new Color(201, 210, 242));
        actionButton.setForeground(available ? new Color(30, 42, 103) : Color.GRAY);
        actionButton.setFont(FontLoader.loadFont("/fonts/Poppins-Bold.ttf", 14f));
        actionButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        actionButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        actionButton.setPreferredSize(new Dimension(Integer.MAX_VALUE, 50));
        actionButton.setFocusPainted(false);

        actionButton.addActionListener(e -> {
            long currentBorrowedCount = borrowedBooks.stream()
                    .filter(book -> !book.isReturned())
                    .count();

            if (currentBorrowedCount >= 3) {
                JOptionPane.showMessageDialog(this, "You can only borrow a maximum of 3 books at a time.\nPlease return a book first.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                borrowedBooks = InquiryManager.borrowBook(borrowedBooks, username, bookId);

                JOptionPane.showMessageDialog(this, "Book borrowed.", "Book ID: " + bookId, JOptionPane.INFORMATION_MESSAGE);
                mainFrame.showPage("books", "");
            }
        });

        contentPanel.add(Box.createVerticalStrut(4));
        contentPanel.add(titleLabel);
        contentPanel.add(authorLabel);
        contentPanel.add(Box.createVerticalStrut(3));
        contentPanel.add(genreLabel);
        contentPanel.add(Box.createVerticalStrut(6));
        contentPanel.add(actionButton);

        card.add(imgLabel, BorderLayout.NORTH);
        card.add(contentPanel, BorderLayout.CENTER);

        return card;
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

    private ImageIcon getImageIconFromResource(String path) {
        URL resource = getClass().getResource(path);
        if (resource == null) {
            System.err.println("Image not found at: " + path);
            return new ImageIcon();
        }
        return new ImageIcon(resource);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame());
    }
}