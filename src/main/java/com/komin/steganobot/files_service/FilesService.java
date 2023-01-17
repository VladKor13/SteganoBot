package com.komin.steganobot.files_service;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

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

public class FilesService {

    private static final String botToken = "5906682132:AAGWl8OOTDWdTLa9v-gEd5LinU-p-PZhKH4";

    public static void downloadImage(String fileId, String chatId) throws IOException {
        URL url = new URL("https://api.telegram.org/bot" + botToken + "/getFile?file_id=" + fileId);
        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
        String getFileResponse = br.readLine();

        JSONObject jResult = new JSONObject(getFileResponse);
        JSONObject path = jResult.getJSONObject("result");
        String filePath = path.getString("file_path");

        File localFile = new File("src/downloaded_files/" + chatId + "inputImage.png");
        InputStream is = new URL("https://api.telegram.org/file/bot" + botToken + "/" + filePath)
                .openStream();
        FileUtils.copyInputStreamToFile(is, localFile);

        br.close();
        is.close();
    }

    public static void saveUserString(String text, String chatId) {
        try {
            FileUtils.writeStringToFile(new File("src/downloaded_files/" + chatId + "inputText.txt"), text, StandardCharsets.UTF_8);
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

}
