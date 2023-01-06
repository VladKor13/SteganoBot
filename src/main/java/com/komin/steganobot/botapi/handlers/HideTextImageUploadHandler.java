package com.komin.steganobot.botapi.handlers;

import com.komin.steganobot.botapi.BotState;
import com.komin.steganobot.botapi.InputMessageHandler;
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

@Slf4j
@Component
public class HideTextImageUploadHandler implements InputMessageHandler {

    private final UserDataCache userDataCache;
    private final ReplyMessageService messageService;
    private final LocaleMessageService localeMessageService;

    public HideTextImageUploadHandler(UserDataCache userDataCache, ReplyMessageService messageService, LocaleMessageService localeMessageService) {
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
        return BotState.HIDE_TEXT_IMAGE_UPLOAD_STATE;
    }

    @Override
    public SendMessage getStateTip(Message message) {
        return generateTip(message, generateKeyboard());
    }

    private SendMessage processUsersInput(Message inputMessage) {
        long user_id = inputMessage.getFrom().getId();
        long chat_id = inputMessage.getChatId();
        SendMessage replyToUser = null;
        String valid_answer_option1 = localeMessageService.getMessage("option.back-to-main-menu-valid-option");
        String valid_answer_option2 = localeMessageService.getMessage("option.hide-text-image-upload-state-valid-option");
        if (Objects.equals(inputMessage.getText(), valid_answer_option1)) {
            userDataCache.setUserCurrentBotState(user_id, BotState.MAIN_MENU_STATE);
        } else if (Objects.equals(inputMessage.getText(), valid_answer_option2)) {
            userDataCache.setUserCurrentBotState(user_id, BotState.HIDE_TEXT_STRING_UPLOAD_STATE);
        } else {
            replyToUser = messageService
                    .getReplyMessage(String.valueOf(chat_id), "reply.no-such-option-error-message");
        }

        return replyToUser;
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
}