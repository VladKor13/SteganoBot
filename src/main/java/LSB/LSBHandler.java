package LSB;

import com.komin.steganobot.files_service.FilesService;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.imageio.ImageIO;

public class LSBHandler {

    public static void encode(String chatId) throws IOException {
        String filename = "src/downloaded_files/" + chatId + "inputImage.png";
        File initFile = new File(filename);

        BufferedImage initImage = ImageIO.read(initFile);

        String pathToSave = "src/downloaded_files";
        String resFileName = chatId + "resultImage.png";
        String messageToEncode = FilesService.readTxt("src/downloaded_files/" + chatId + "inputText.txt", StandardCharsets.UTF_8);

        String bitMsg = LSBEncoder.encodeMessage(messageToEncode);
        BufferedImage newImage = LSBEncoder.encodeImage(bitMsg, initImage);

        File dir = new File(pathToSave);

        if (dir.exists() && dir.isDirectory() && dir.canWrite()) {
            File finalImage = new File(dir, resFileName);
            ImageIO.write(newImage, "png", finalImage);
            System.out.println("New image saved!");
        } else {
            throw new IOException("invalid");
        }
    }

    public static String decode(String chatId) throws IOException {
        String filePath = "src/downloaded_files/" + chatId + "inputImage.png";
        File outFile = new File(filePath);
        BufferedImage image = ImageIO.read(outFile);
        String bitMessage = LSBDecoder.decodeMessage(image);
        return  LSBDecoder.getMessage(bitMessage);
    }
}


