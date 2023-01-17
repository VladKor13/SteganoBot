package com.komin.steganobot;

import LSB.LSBHandler;
import com.komin.steganobot.botapi.TelegramFacade;
import org.springframework.util.ResourceUtils;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.FileNotFoundException;
import java.io.IOException;

public class MySteganoBot extends TelegramWebhookBot {

    private String webHookPath;
    private String botUserName;
    private String botToken;

    private final TelegramFacade telegramFacade;

    public MySteganoBot(DefaultBotOptions botOptions, TelegramFacade telegramFacade) {
        super(botOptions);
        this.telegramFacade = telegramFacade;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public String getBotUsername() {
        return botUserName;
    }

    @Override
    public String getBotPath() {
        return webHookPath;
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        sendMessage(update);
        sendTip(update);
        encodeIfNeeded(update);
        decodeIfNeeded(update);
        return null;
    }

    private void sendMessage(Update update) {
        SendMessage replyMessageToUser = telegramFacade.handleUpdate(update);
        if (replyMessageToUser == null) {
            return;
        }
        try {
            execute(replyMessageToUser);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendTip(Update update) {
        SendMessage tipMessageToUser = telegramFacade.handleTip(update.getMessage());
        if (tipMessageToUser == null) {
            return;
        }
        try {
            execute(tipMessageToUser);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void encodeIfNeeded(Update update) {
        long chadId = update.getMessage().getChatId();
        if (telegramFacade.isFilesReadyToEncode(chadId)) {
            try {
                LSBHandler.encode(String.valueOf(chadId));
                sendImageAsDocument(chadId,"");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void decodeIfNeeded(Update update) {
        long chadId = update.getMessage().getChatId();
        if (telegramFacade.isFilesReadyToDecode(chadId)) {
            try {
                String decodedText = LSBHandler.decode(String.valueOf(chadId));
                execute(new SendMessage(String.valueOf(chadId),decodedText));
                //Текст, що вдалося вилучити із файла
            } catch (IOException | TelegramApiException e) {
                e.printStackTrace();
            }
        }

    }

    private void sendImageAsDocument(long chatId, String caption) {
        try {
            InputFile image = new InputFile(ResourceUtils.getFile("src/downloaded_files/"
                    + chatId + "resultImage.png"));
            SendDocument sendDocument = new SendDocument();
            sendDocument.setDocument(image);
            sendDocument.setChatId(chatId);
            sendDocument.setCaption(caption);
            execute(sendDocument);
        } catch (TelegramApiException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void setWebHookPath(String webHookPath) {
        this.webHookPath = webHookPath;
    }

    public void setBotUserName(String botUserName) {
        this.botUserName = botUserName;
    }

    public void setBotToken(String botToken) {
        this.botToken = botToken;
    }
}
