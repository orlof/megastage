import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class Test {
    public static void main(String[] args) throws Exception {
        BufferedImage img = new BufferedImage(128, 112, BufferedImage.TYPE_INT_ARGB);
        int[] pixels = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();
        System.out.println("pixels.length = " + pixels.length);
    }
}

