package com.komin.steganobot.files_service;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
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

public class FilesService {

    public static String lastFileExtension;

    public static final String downloadedFilesPath = "src/downloaded_files/";

    private static final String botToken = "5906682132:AAGWl8OOTDWdTLa9v-gEd5LinU-p-PZhKH4";

    public static void downloadImage(String fileId, String chatId) throws IOException {
        URL url = new URL("https://api.telegram.org/bot" + botToken + "/getFile?file_id=" + fileId);
        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
        String getFileResponse = br.readLine();

        JSONObject jResult = new JSONObject(getFileResponse);
        JSONObject path = jResult.getJSONObject("result");
        String filePath = path.getString("file_path");
        File localFile = new File(downloadedFilesPath + chatId + "inputImage" + getFileExtension(filePath));
        InputStream is = new URL("https://api.telegram.org/file/bot" + botToken + "/" + filePath)
                .openStream();
        FileUtils.copyInputStreamToFile(is, localFile);

        br.close();
        is.close();
    }

    public static void saveUserString(String text, String chatId) {
        try {
            FileUtils.writeStringToFile(new File(downloadedFilesPath + chatId + "inputText.txt"),
                    "MSG" + text,
                    StandardCharsets.UTF_8);
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
        File file = new File(downloadedFilesPath
                + chatId + "resultImage" + lastFileExtension);
        if (file.delete()) {
            //log here
        }

        file = new File(downloadedFilesPath
                + chatId + "inputImage" + lastFileExtension);
        if (file.delete()) {
            //log here
        }

        file = new File(downloadedFilesPath
                + chatId + "inputText.txt");
        if (file.delete()) {
            //log here
        }
    }
}
