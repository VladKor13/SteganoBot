package com.komin.steganobot.botapi.handlers;

import com.komin.steganobot.botapi.BotState;
import com.komin.steganobot.botapi.InputMessageHandler;
import com.komin.steganobot.cache.UserDataCache;
import com.komin.steganobot.service.LocaleMessageService;
import com.komin.steganobot.service.ReplyMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Objects;

@Slf4j
@Component
public class AboutInfoStateHandler implements InputMessageHandler {
    private final UserDataCache userDataCache;
    private final ReplyMessageService messageService;
    private final LocaleMessageService localeMessageService;

    public AboutInfoStateHandler(UserDataCache userDataCache, ReplyMessageService messageService, LocaleMessageService localeMessageService){
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
    public String handleTip() {
        return localeMessageService.getMessage("tip.AboutInfoState");
    }

    private SendMessage processUsersInput(Message inputMessage){
        Long user_id = inputMessage.getFrom().getId();
        long chat_id = inputMessage.getChatId();
        SendMessage replyToUser = null;
        String valid_answer_option = localeMessageService.getMessage("option.BackToMainMenuValidOption");

        if (Objects.equals(inputMessage.getText(), valid_answer_option)){
            userDataCache.setUserCurrentBotState(user_id, BotState.MAIN_MENU_STATE);
        } else {
            replyToUser = messageService
                    .getReplyMessage(String.valueOf(chat_id), "reply.NoSuchOptionErrorMessage");
        }

        return replyToUser;
    }
}
