package com.komin.steganobot.files_service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

@Slf4j
public class FilesService {

    public static String lastFileExtension;

    //TODO
    public static final String downloadedFilesPath = "/src/downloaded_files/";

    //TODO Get token from properties
    private static final String botToken = "5906682132:AAGWl8OOTDWdTLa9v-gEd5LinU-p-PZhKH4";

    public static boolean downloadImage(Message inputMessage) throws IOException {
        String fileId = inputMessage.getDocument().getFileId();
        String chatId = inputMessage.getChatId().toString();
        String userName = inputMessage.getFrom().getUserName();

        URL url = new URL("https://api.telegram.org/bot" + botToken + "/getFile?file_id=" + fileId);
        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
        String getFileResponse = br.readLine();
        //        System.out.println(url);

        JSONObject jResult = new JSONObject(getFileResponse);
        boolean isOk = jResult.getBoolean("ok");
        if (!isOk) {
            return false;
        }
        JSONObject path = jResult.getJSONObject("result");
        String filePath = path.getString("file_path");
        int fileSize = path.getInt("file_size");
        if (fileSize > 5e6) {
            return false;
        }
        File localFile = new File(downloadedFilesPath + chatId + "inputImage" + getFileExtension(filePath));
        InputStream is = new URL("https://api.telegram.org/file/bot" + botToken + "/" + filePath)
                .openStream();
        FileUtils.copyInputStreamToFile(is, localFile);

        br.close();
        is.close();

        log.info("[{}] File {} from User: {} was downloaded successfully",
                chatId,
                "inputImage",
                userName);
        return true;
    }

    public static void saveUserString(Message inputMessage) {
        String text = inputMessage.getText();
        String chatId = inputMessage.getChatId().toString();
        try {
            FileUtils.writeStringToFile(new File(downloadedFilesPath + chatId + "inputText.txt"),
                    "MSG" + text,
                    StandardCharsets.UTF_8);

            log.info("[{}] Text from User: {} was saved as inputText.txt",
                    chatId,
                    inputMessage.getFrom().getUserName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readTxt(String path, Charset encoding) {
        byte[] encoded = new byte[0];
        try {
            encoded = Files.readAllBytes(Paths.get(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String(encoded, encoding);
    }

    private static String getFileExtension(String filePath) {
        StringBuilder result = new StringBuilder();
        int charIdx = filePath.length() - 1;
        while (filePath.charAt(charIdx) != '.') {
            result.append(filePath.charAt(charIdx));
            charIdx--;
        }
        result.append('.');
        result.reverse();
        lastFileExtension = result.toString();
        return result.toString();
    }

    public static String getInputImageNameByChatId(String chatID) {
        File directoryPath = new File(downloadedFilesPath);
        List<File> files = List.of(Objects.requireNonNull(directoryPath.listFiles()));

        return files.stream()
                    .filter(file -> file.getName()
                                        .startsWith(chatID + "inputImage"))
                    .findFirst().get().toString().replaceAll("\\\\", "/");
    }

    public static void deleteUserCache(Update update) {
        String chatId = update.getMessage().getChatId().toString();
        log.info("[{}] Cache deleting for User: {}, was STARTED",
                chatId,
                update.getMessage().getFrom().getUserName());

        File file = new File(downloadedFilesPath
                + chatId + "resultImage" + lastFileExtension);
        logFileDeletingStatus(update, "resultImage", file.delete());

        file = new File(downloadedFilesPath
                + chatId + "inputImage" + lastFileExtension);
        logFileDeletingStatus(update, "inputImage", file.delete());

        file = new File(downloadedFilesPath
                + chatId + "inputText.txt");
        logFileDeletingStatus(update, "inputText.txt", file.delete());

        log.info("[{}] Cache deleting for User: {}, was ENDED",
                chatId,
                update.getMessage().getFrom().getUserName());

    }

    private static void logFileDeletingStatus(Update update, String fileName, boolean status) {
        log.info("[{}] File {} from User: {}, was deleted: {}",
                update.getMessage().getChatId(),
                fileName,
                update.getMessage().getFrom().getUserName(),
                status);
    }

    public static boolean hasUserCacheForEncoding(long chatID) {
        File directoryPath = new File(downloadedFilesPath);
        List<File> files = List.of(Objects.requireNonNull(directoryPath.listFiles()));
        return files.stream()
                    .filter(file -> file.getName().startsWith(String.valueOf(chatID)))
                    .toList().size() == 2;
    }

    public static boolean hasUserCacheForDecoding(long chatID) {
        File directoryPath = new File(downloadedFilesPath);
        List<File> files = List.of(Objects.requireNonNull(directoryPath.listFiles()));
        return files.stream()
                    .filter(file -> file.getName().startsWith(String.valueOf(chatID)))
                    .toList().size() == 1;
    }
}
