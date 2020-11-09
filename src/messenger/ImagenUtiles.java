package messenger;

import javax.swing.*;
import java.awt.*;
import java.net.URLConnection;

public class ImagenUtiles {
    /*
    Comprueba que el fichero sea de imagen
    */
    public static boolean esImagen(String fileName) {
        String mimetype = URLConnection.guessContentTypeFromName(fileName);
        if (mimetype != null) {
            String type = mimetype.split("/")[0];
            if (type.equals("image"))
                return true;
        }
        return false;
    }

    /*
    Escala una imagen manteniendo las proporciones
    */
    public static ImageIcon escalarImagen(ImageIcon img) {
        int width = img.getIconWidth();
        int height = img.getIconHeight();
        int newWidth = width;
        int newHeight = height;

        if (width > height && width > 200) {
            newWidth = 200;
            float ratio = width / newWidth;
            newHeight = (int) (height / ratio);
        } else if (height > 200) {
            newHeight = 200;
            float ratio = height / newHeight;
            newWidth = (int) (width / ratio);
        }

        Image raw = img.getImage();
        Image newimg = raw.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        return new ImageIcon(newimg);
    }
}
