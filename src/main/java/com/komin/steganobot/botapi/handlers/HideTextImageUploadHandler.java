package com.komin.steganobot.botapi.handlers;

import com.komin.steganobot.botapi.BotState;
import com.komin.steganobot.botapi.InputMessageHandler;
import com.komin.steganobot.builder.ReplyKeyboardMarkupBuilder;
import com.komin.steganobot.cache.UserDataCache;
import com.komin.steganobot.files_service.FilesService;
import com.komin.steganobot.service.LocaleMessageService;
import com.komin.steganobot.service.ReplyMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class HideTextImageUploadHandler extends StateHandler implements InputMessageHandler {

    public HideTextImageUploadHandler(UserDataCache userDataCache, ReplyMessageService messageService, LocaleMessageService localeMessageService) {
        super(userDataCache, messageService, localeMessageService);
    }

    @Override
    public SendMessage handle(Message message) {
        return processUsersInput(message);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.HIDE_TEXT_IMAGE_UPLOAD_STATE;
    }

    @Override
    public SendMessage getStateTip(Message message) {
        return generateTip(message, generateKeyboard());
    }

    private SendMessage processUsersInput(Message inputMessage) {
        long userID = inputMessage.getFrom().getId();
        long chatID = inputMessage.getChatId();

        final Document document = inputMessage.getDocument();
        if (document != null) {
            final String fileId = document.getFileId();
            final String fileName = document.getFileName();
            if (isFileExtensionValid(fileName)) {
                try {
                    FilesService.downloadImage(inputMessage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                userDataCache.setUserCurrentBotState(userID, BotState.HIDE_TEXT_STRING_UPLOAD_STATE);
                return messageService
                        .getReplyMessage(String.valueOf(chatID), "reply.photo-was-uploaded-successfully-message");
            } else {
                return messageService
                        .getReplyMessage(String.valueOf(chatID), "reply.wrong-file-extension-error-message");
            }
        }
        return checkMessageForRightOption(inputMessage);
    }

    private SendMessage generateTip(Message inputMessage, ReplyKeyboardMarkup replyKeyboardMarkup) {
        long chat_id = inputMessage.getChatId();
        SendMessage replyTip = new SendMessage(String.valueOf(chat_id), localeMessageService.getMessage("tip.hide-text-image-upload-state"));
        if (replyKeyboardMarkup != null) {
            replyTip.enableMarkdown(true);
            replyTip.setReplyMarkup(replyKeyboardMarkup);
        }
        return replyTip;
    }

    private ReplyKeyboardMarkup generateKeyboard() {
        String backToMainMenu = localeMessageService.getMessage("option.back-to-main-menu-valid-option");

        return ReplyKeyboardMarkupBuilder.build(backToMainMenu);
    }

    private boolean isFileExtensionValid(String fileName) {
        //Add necessary extensions here
        ArrayList<String> validExtensions = new ArrayList<>(
                List.of(".png")
        );
        return validExtensions.stream().anyMatch(extension -> fileName.toLowerCase().endsWith(extension));
    }
}