package com.komin.steganobot.botapi.handlers;

import com.komin.steganobot.botapi.BotState;
import com.komin.steganobot.botapi.InputMessageHandler;
import com.komin.steganobot.botapi.options.BackToMainMenuOption;
import com.komin.steganobot.builder.ReplyKeyboardMarkupBuilder;
import com.komin.steganobot.cache.UserDataCache;
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
public class HideTextStringUploadHandler implements InputMessageHandler {

    private final UserDataCache userDataCache;
    private final ReplyMessageService messageService;
    private final LocaleMessageService localeMessageService;

    public HideTextStringUploadHandler(UserDataCache userDataCache, ReplyMessageService messageService, LocaleMessageService localeMessageService) {
        this.userDataCache = userDataCache;
        this.messageService = messageService;
        this.localeMessageService = localeMessageService;
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
        long user_id = inputMessage.getFrom().getId();
        long chat_id = inputMessage.getChatId();

        Optional<BackToMainMenuOption> hideTextStringUploadOptionOptional =
                Stream.of(BackToMainMenuOption.values())
                      .filter(option -> Objects.equals(localeMessageService.getMessage(option.getValue()),
                              inputMessage.getText()))
                      .findFirst();

        if (hideTextStringUploadOptionOptional.isEmpty()) {
            if (isStringValid(inputMessage.getText())) {
                saveUsersText();
                userDataCache.setUserCurrentBotState(user_id, BotState.HIDE_TEXT_RESULT_UPLOAD_STATE);
                return messageService
                        .getReplyMessage(String.valueOf(chat_id), "reply.text-was-uploaded-message");
            }
            return messageService
                    .getReplyMessage(String.valueOf(chat_id), "reply.text-has-too-many-chars-error-message");
        }
        BackToMainMenuOption backToMainMenuOption = hideTextStringUploadOptionOptional.get();
        userDataCache.setUserCurrentBotState(user_id, backToMainMenuOption.getBotState());

        return null;
    }

    private SendMessage generateTip(Message inputMessage, ReplyKeyboardMarkup replyKeyboardMarkup) {
        long chat_id = inputMessage.getChatId();
        SendMessage replyTip = new SendMessage(String.valueOf(chat_id),
                localeMessageService.getMessage("tip.hide-text-string-upload-state")
                        + " " + evaluatePossibleCharQuantity());
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

    private int evaluatePossibleCharQuantity() {
        return 10;
    }

    private boolean isStringValid(String text) {
        if (text == null) {
            return false;
        }
        return text.length() <= evaluatePossibleCharQuantity();
    }

    private void saveUsersText(){

    }
}
