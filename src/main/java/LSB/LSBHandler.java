package LSB;

import com.komin.steganobot.files_service.FilesService;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.imageio.ImageIO;

@Slf4j
public class LSBHandler {

    public static void encode(Update update) throws IOException {
        String chatId = update.getMessage().getChatId().toString();
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
        } else {
            throw new IOException("Invalid !");
        }

        log.info("[{}] File with encoded text for User: {}, was created successfully",
                update.getMessage().getChatId(),
                update.getMessage().getFrom().getUserName());
    }

    public static String decode(String chatId) throws IOException {
        String filePath = FilesService.downloadedFilesPath + chatId + "inputImage" + FilesService.lastFileExtension;
        File outFile = new File(filePath);

        BufferedImage image = ImageIO.read(outFile);
        if (image == null) {
            //TODO Create custom exception
            throw new IOException();
        }
        String bitMessage = LSBDecoder.decodeMessage(image);
        return LSBDecoder.getMessage(bitMessage);
    }
}


