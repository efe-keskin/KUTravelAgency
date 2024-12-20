package resources;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;

public class CustomTools {
    public static JLabel loadImage(String resource) {
        BufferedImage image;
        try {
            // Load the resource from the classpath
            InputStream inputStream = CustomTools.class.getResourceAsStream(resource);
            if (inputStream == null) {
                throw new IllegalArgumentException("Resource not found in classpath: " + resource);
            }
            image = ImageIO.read(inputStream);
            return new JLabel(new ImageIcon(image));
        } catch (Exception e) {
            System.out.println("Error loading image: " + e.getMessage());
        }
        return null;
    }
}
