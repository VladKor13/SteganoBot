package com.komin.steganobot.botapi.handlers;

import LSB.LSBEncoder;
import com.komin.steganobot.botapi.BotState;
import com.komin.steganobot.botapi.InputMessageHandler;
import com.komin.steganobot.botapi.options.BackToMainMenuOption;
import com.komin.steganobot.builder.ReplyKeyboardMarkupBuilder;
import com.komin.steganobot.cache.UserDataCache;
import com.komin.steganobot.files_service.FilesService;
import com.komin.steganobot.service.LocaleMessageService;
import com.komin.steganobot.service.ReplyMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Component
public class HideTextStringUploadHandler extends StateHandler implements InputMessageHandler {

    public HideTextStringUploadHandler(UserDataCache userDataCache, ReplyMessageService messageService, LocaleMessageService localeMessageService) {
        super(userDataCache, messageService, localeMessageService);
    }

    @Override
    public SendMessage handle(Message message) {
        return processUsersInput(message);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.HIDE_TEXT_STRING_UPLOAD_STATE;
    }

    @Override
    public SendMessage getStateTip(Message message) {
        return generateTip(message, generateKeyboard());
    }

    private SendMessage processUsersInput(Message inputMessage) {
        return checkMessageForRightOption(inputMessage);
    }

    @Override
    SendMessage checkMessageForRightOption(Message inputMessage) {
        long userID = inputMessage.getFrom().getId();
        String chatID = inputMessage.getChatId().toString();

        Optional<BackToMainMenuOption> hideTextStringUploadOptionOptional =
                Stream.of(BackToMainMenuOption.values())
                      .filter(option -> Objects.equals(localeMessageService.getMessage(option.getValue()),
                              inputMessage.getText()))
                      .findFirst();

        if (hideTextStringUploadOptionOptional.isEmpty()) {
            if (isStringValid(inputMessage.getText(), chatID)) {
                FilesService.saveUserString(inputMessage.getText(), chatID);
                userDataCache.setUserCurrentBotState(userID, BotState.HIDE_TEXT_RESULT_UPLOAD_STATE);
                return messageService
                        .getReplyMessage(chatID, "reply.text-was-uploaded-message");
            }
            return messageService
                    .getReplyMessage(chatID, "reply.text-has-too-many-chars-error-message");
        }
        BackToMainMenuOption backToMainMenuOption = hideTextStringUploadOptionOptional.get();
        userDataCache.setUserCurrentBotState(userID, backToMainMenuOption.getFollowingBotState());

        return null;
    }

    private SendMessage generateTip(Message inputMessage, ReplyKeyboardMarkup replyKeyboardMarkup) {
        String chat_id = inputMessage.getChatId().toString();
        SendMessage replyTip = new SendMessage(chat_id,
                localeMessageService.getMessage("tip.hide-text-string-upload-state")
                        + " " + LSBEncoder.evaluatePossibleCharQuantity(chat_id));
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

    private boolean isStringValid(String text, String chatId) {
        if (text == null) {
            return false;
        }
        return text.length() <= LSBEncoder.evaluatePossibleCharQuantity(chatId);
    }
}
