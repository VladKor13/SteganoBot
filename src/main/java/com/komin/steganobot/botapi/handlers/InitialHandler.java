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

@Slf4j
@Component
public class InitialHandler extends StateHandler implements InputMessageHandler {

    public InitialHandler(UserDataCache userDataCache, ReplyMessageService messageService, LocaleMessageService localeMessageService) {
        super(userDataCache, messageService, localeMessageService);
    }

    @Override
    public SendMessage handle(Message message) {
        return processUsersInput(message);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.INITIAL_STATE;
    }

    @Override
    public SendMessage getStateTip(Message message) {
        return generateTip(message);
    }

    private SendMessage processUsersInput(Message inputMessage) {
        return checkMessageForRightOption(inputMessage);
    }

    private SendMessage generateTip(Message inputMessage) {
        long chat_id = inputMessage.getChatId();
        return new SendMessage(String.valueOf(chat_id), localeMessageService.getMessage("tip.initial-state"));
    }
}
