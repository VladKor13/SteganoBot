package LSB;

import com.komin.steganobot.files_service.FilesService;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.imageio.ImageIO;

public class LSBHandler {

    public static void encode(String chatId) throws IOException {
        String filename = FilesService.downloadedFilesPath
                + chatId + "inputImage" + FilesService.lastFileExtension;
        File initFile = new File(filename);

        BufferedImage initImage = ImageIO.read(initFile);

        String resFileName = chatId + "resultImage" + FilesService.lastFileExtension;
        String messageToEncode = FilesService.readTxt(FilesService.downloadedFilesPath
                + chatId + "inputText.txt", StandardCharsets.UTF_8);

        String bitMsg = LSBEncoder.encodeMessage(messageToEncode);
        BufferedImage newImage = LSBEncoder.encodeImage(bitMsg, initImage);

        File dir = new File(FilesService.downloadedFilesPath);

        if (dir.exists() && dir.isDirectory() && dir.canWrite()) {
            File finalImage = new File(dir, resFileName);
            ImageIO.write(newImage, FilesService.lastFileExtension.substring(1), finalImage);
            System.out.println("New image saved!");
        } else {
            throw new IOException("Invalid !");
        }
    }

    public static String decode(String chatId) throws IOException {
        String filePath = FilesService.downloadedFilesPath + chatId + "inputImage" + FilesService.lastFileExtension;
        File outFile = new File(filePath);

        BufferedImage image = ImageIO.read(outFile);
        if (image == null){
            //TODO Create custom exception
            throw new IOException();
        }
        String bitMessage = LSBDecoder.decodeMessage(image);
        return LSBDecoder.getMessage(bitMessage);
    }
}


