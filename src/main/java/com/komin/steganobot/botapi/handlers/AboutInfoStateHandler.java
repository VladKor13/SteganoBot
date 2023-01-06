package com.komin.steganobot.botapi.handlers;

import com.komin.steganobot.botapi.BotState;
import com.komin.steganobot.botapi.InputMessageHandler;
import com.komin.steganobot.botapi.options.AboutMenuOption;
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
public class AboutInfoStateHandler implements InputMessageHandler {

    private final UserDataCache userDataCache;
    private final ReplyMessageService messageService;
    private final LocaleMessageService localeMessageService;

    public AboutInfoStateHandler(UserDataCache userDataCache, ReplyMessageService messageService, LocaleMessageService localeMessageService) {
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
        return BotState.ABOUT_INFO_STATE;
    }

    @Override
    public SendMessage getStateTip(Message message) {
        return generateTip(message, generateKeyboard());
    }

    private SendMessage processUsersInput(Message inputMessage) {
        Long user_id = inputMessage.getFrom().getId();
        long chat_id = inputMessage.getChatId();

        Optional<AboutMenuOption> aboutMenuOptionOptional =
                Stream.of(AboutMenuOption.values())
                      .filter(option -> Objects.equals(localeMessageService.getMessage(option.getValue()),
                              inputMessage.getText()))
                      .findFirst();

        if (aboutMenuOptionOptional.isEmpty()) {
            return messageService
                    .getReplyMessage(String.valueOf(chat_id), "reply.no-such-option-error-message");
        }
        AboutMenuOption aboutMenuOption = aboutMenuOptionOptional.get();
        userDataCache.setUserCurrentBotState(user_id, aboutMenuOption.getBotState());

        return null;
    }

    private SendMessage generateTip(Message inputMessage, ReplyKeyboardMarkup replyKeyboardMarkup) {
        long chat_id = inputMessage.getChatId();
        SendMessage replyTip = new SendMessage(String.valueOf(chat_id), localeMessageService.getMessage("tip.about-info-state"));
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
