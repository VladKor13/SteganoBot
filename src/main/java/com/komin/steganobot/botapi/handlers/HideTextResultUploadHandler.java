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
public class HideTextResultUploadHandler implements InputMessageHandler {

    private final UserDataCache userDataCache;
    private final ReplyMessageService messageService;
    private final LocaleMessageService localeMessageService;

    public HideTextResultUploadHandler(UserDataCache userDataCache, ReplyMessageService messageService, LocaleMessageService localeMessageService) {
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
        return BotState.HIDE_TEXT_RESULT_UPLOAD_STATE;
    }

    @Override
    public SendMessage getStateTip(Message message) {
        return generateTip(message, generateKeyboard());
    }

    private SendMessage processUsersInput(Message inputMessage) {
        Long user_id = inputMessage.getFrom().getId();
        String chat_id = inputMessage.getChatId().toString();

        Optional<BackToMainMenuOption> hideTextResultUploadOptionOptional =
                Stream.of(BackToMainMenuOption.values())
                      .filter(option -> Objects.equals(localeMessageService.getMessage(option.getValue()),
                              inputMessage.getText()))
                      .findFirst();

        if (hideTextResultUploadOptionOptional.isEmpty()) {
            return messageService
                    .getReplyMessage(chat_id, "reply.no-such-option-error-message");
        }
        BackToMainMenuOption backToMainMenuOption = hideTextResultUploadOptionOptional.get();
        userDataCache.setUserCurrentBotState(user_id, backToMainMenuOption.getBotState());

        return null;
    }

    private SendMessage generateTip(Message inputMessage, ReplyKeyboardMarkup replyKeyboardMarkup) {
        String chat_id = inputMessage.getChatId().toString();
        SendMessage replyTip = new SendMessage(chat_id, localeMessageService.getMessage("tip.hide-text-result-upload-state"));
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
