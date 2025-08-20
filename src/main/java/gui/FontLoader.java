package gui;

import java.awt.Font;
import java.io.InputStream;

public class FontLoader {
    public static Font loadFont(String path, float size) {
        try {
            // Load the font resource as an InputStream
            InputStream is = FontLoader.class.getResourceAsStream(path);
            
            // Create the font using the InputStream
            Font font = Font.createFont(Font.TRUETYPE_FONT, is);
            
            // Return the font derived to the specified size
            return font.deriveFont(size);
            
        } catch (Exception e) {
            
            // Print the error and return a fallback font if loading fails
            e.printStackTrace();
            return new Font("SansSerif", Font.PLAIN, (int) size); // fallback font
        }
    }
}