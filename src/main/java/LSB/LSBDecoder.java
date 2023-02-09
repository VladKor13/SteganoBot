package LSB;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class LSBDecoder {

    static String getMessage(String encoded) {
        int count = encoded.length() - 1;
        StringBuilder message = new StringBuilder();
        int values = encoded.length() / 8;
        byte[] ba = new byte[values];
        int arrayCount = values - 1;
        while (arrayCount > 0) {
            StringBuilder bits = new StringBuilder();
            for (int i = 0; i < 8; i++) {
                bits.insert(0, encoded.charAt(count - i));
            }
            byte b = (byte) Integer.parseInt(bits.toString(), 2);
            int x = Byte.toUnsignedInt(b);
            ba[arrayCount] = (byte) x;
            char c = (char) x;
            message.insert(0, c);

            count = count - 8;
            arrayCount--;

        }
        String result = new String(clearAllZeroes(ba));

        if (result.startsWith("MSG")) {
            return result.substring(3);
        } else {
            return null;
        }

    }

    private static byte[] clearAllZeroes(byte[] initialByteArray) {
        ArrayList<Byte> tmpByteList = new ArrayList<>();
        for (byte element : initialByteArray) {
            if (element != 0) {
                tmpByteList.add(element);
            }
        }

        byte[] result = new byte[tmpByteList.size()];
        for (int i = 0; i < tmpByteList.size(); i++) {
            result[i] = tmpByteList.get(i);
        }
        return result;
    }

    static String decodeMessage(BufferedImage image) {
        StringBuilder sb = new StringBuilder();

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                Color c = new Color(image.getRGB(x, y)); //color of pixel
                byte r = (byte) c.getRed(); //split into red
                byte g = (byte) c.getGreen(); //split into green
                byte b = (byte) c.getBlue(); //split into blue
                byte[] RGB = {r, g, b};

                for (int i = 0; i < 3; i++) {
                    if ((RGB[i] & 1) == 1) { //LSB is a 1
                        sb.append("1");
                    } else { //else it is a 0
                        sb.append("0");
                    }
                }
            }
        }

        return sb.toString();
    }
}
