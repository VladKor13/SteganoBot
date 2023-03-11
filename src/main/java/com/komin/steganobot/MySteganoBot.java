package com.komin.steganobot;

import LSB.LSBHandler;
import com.komin.steganobot.botapi.BotState;
import com.komin.steganobot.botapi.TelegramFacade;
import com.komin.steganobot.files_service.FilesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ResourceUtils;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

@Slf4j
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
        if (update != null) {
            sendMessage(update);
            sendTip(update);
            encodeIfNeeded(update);
            decodeIfNeeded(update);
        }
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
            sendTipImage(update);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void encodeIfNeeded(Update update) {
        if (update.getMessage() != null) {
            long chadId = update.getMessage().getChatId();
            if (telegramFacade.isFilesReadyToEncode(chadId)) {
                log.info("[{}] Files for User: {} are ready to be encoded",
                        update.getMessage().getChatId(),
                        update.getMessage().getFrom().getUserName());
                try {
                    LSBHandler.encode(update);
                    sendImageAsDocument(update, "");
                    FilesService.deleteUserCache(update);
                } catch (IOException e) {
                    e.printStackTrace();
                    //TODO CATCH
                }
            }
        }
    }

    private void decodeIfNeeded(Update update) {
        if (update.getMessage() != null) {
            long chatId = update.getMessage().getChatId();
            if (telegramFacade.isFilesReadyToDecode(chatId)) {
                log.info("[{}] Files for User: {} are ready to be decoded",
                        update.getMessage().getChatId(),
                        update.getMessage().getFrom().getUserName());
                try {
                    String decodedText = LSBHandler.decode(String.valueOf(chatId));
                    log.info("[{}] File inputImage for User: {} was decoded successfully",
                            update.getMessage().getChatId(),
                            update.getMessage().getFrom().getUserName());

                    if (decodedText == null) {
                        execute(new SendMessage(String.valueOf(chatId),
                                "Цей контейнер не містить приховане повідомлення."));

                        log.info("[{}] Reply for User: {}, with text: {}",
                                update.getMessage().getChatId(),
                                update.getMessage().getFrom().getUserName(),
                                "Цей контейнер не містить приховане повідомлення.");
                        //TODO Refactor MessageServices
                    } else {
                        sendLongMessage(chatId, decodedText);

                        log.info("[{}] Reply for User: {}, with decoded text",
                                update.getMessage().getChatId(),
                                update.getMessage().getFrom().getUserName());
                    }

                    FilesService.deleteUserCache(update);
                } catch (IOException | TelegramApiException e) {
                    e.printStackTrace();
                    //TODO CATCH
                }
            }
        }
    }

    private void sendTipImage(Update update) {
        long chatID = update.getMessage().getChatId();
        if (telegramFacade.getUserDataCache().getUserCurrentBotState(chatID).equals(BotState.ABOUT_INFO_STATE)) {
            try {
                String imagePath = "src" + File.separator + "source_images" + File.separator + "example.jpg";
                InputFile image = new InputFile(ResourceUtils.getFile(imagePath));
                SendPhoto sendPhoto = new SendPhoto();
                sendPhoto.setPhoto(image);
                sendPhoto.setChatId(chatID);
                execute(sendPhoto);
            } catch (TelegramApiException | FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendLongMessage(long chatId, String text) throws TelegramApiException {
        execute(new SendMessage(String.valueOf(chatId), "Повідомлення, що було приховане:"));
        while (text.length() > 4096) {
            execute(new SendMessage(String.valueOf(chatId), text.substring(0, 4095)));
            text = text.substring(4096);
        }
        execute(new SendMessage(String.valueOf(chatId), text));
    }

    private void sendImageAsDocument(Update update, String caption) {
        long chatId = update.getMessage().getChatId();
        try {
            InputFile image = new InputFile(ResourceUtils.getFile(FilesService.downloadedFilesPath
                    + chatId + "resultImage" + FilesService.lastFileExtension));
            SendDocument sendDocument = new SendDocument();
            sendDocument.setDocument(image);
            sendDocument.setChatId(chatId);
            sendDocument.setCaption(caption);
            execute(sendDocument);
        } catch (TelegramApiException | FileNotFoundException e) {
            e.printStackTrace();
            //TODO CATCH
        }
        log.info("[{}] File resultImage for User: {} was sent successfully",
                update.getMessage().getChatId(),
                update.getMessage().getFrom().getUserName());
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
