package com.komin.steganobot.botapi;

import com.komin.steganobot.cache.UserDataCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@Slf4j
public class TelegramFacade {

    private final BotStateContext botStateContext;
    private final UserDataCache userDataCache;

    public TelegramFacade(BotStateContext botStateContext, UserDataCache userDataCache) {
        this.botStateContext = botStateContext;
        this.userDataCache = userDataCache;
    }

    public SendMessage handleUpdate(Update update) {
        SendMessage replyMessage = null;

        Message message = update.getMessage();
        if (message != null) {
            log.info("New message from User: {}, chatId: {}, with text: {}",
                    message.getFrom().getUserName(), message.getChatId(), message.getText());
            replyMessage = handleInputMessage(message);
        }
        return replyMessage;
    }

    private SendMessage handleInputMessage(Message message) {
        long userId = message.getFrom().getId();
        BotState botState;
        SendMessage replyMessage;

        botState = userDataCache.getUserCurrentBotState(userId);
        replyMessage = botStateContext.processInputMessage(botState, message);
        return replyMessage;
    }

    public SendMessage handleTip(Message message) {
        if (userDataCache.isNewStateWasSet()) {
            userDataCache.setNewStateWasSet(false);
            return botStateContext.processTipMessage(
                    userDataCache.getUserCurrentBotState(message.getFrom().getId()), message);
        }
        return null;
    }

}
