import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;

public class CustomTools {
    public static JLabel loadImage(String resource){
        BufferedImage image;
        try{
            InputStream InputStream = CustomTools.class.getResourceAsStream(resource);
            image = ImageIO.read(InputStream);
            return new JLabel(new ImageIcon(image));
        } catch (Exception e){
            System.out.println("Error: " + e);
        }
        return null;
    }
}
