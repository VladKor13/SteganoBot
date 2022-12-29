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
public class MainMenuHandler implements InputMessageHandler {
    private final UserDataCache userDataCache;
    private final ReplyMessageService messageService;
    private final LocaleMessageService localeMessageService;

    public MainMenuHandler(UserDataCache userDataCache, ReplyMessageService messageService, LocaleMessageService localeMessageService){
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
        return BotState.MAIN_MENU_STATE;
    }

    @Override
    public String handleTip() {
        return localeMessageService.getMessage("tip.MainMenuState");
    }

    private SendMessage processUsersInput(Message inputMessage){
        long user_id = inputMessage.getFrom().getId();
        long chat_id = inputMessage.getChatId();
        SendMessage replyToUser = null;
        String valid_answer_option1 = localeMessageService.getMessage("option.MainMenuStateValidOption1");
        String valid_answer_option2 = localeMessageService.getMessage("option.MainMenuStateValidOption2");
        String valid_answer_option3 = localeMessageService.getMessage("option.MainMenuStateValidOption3");

        if (Objects.equals(inputMessage.getText(), valid_answer_option1)){
            userDataCache.setUserCurrentBotState(user_id, BotState.HIDE_TEXT_IMAGE_UPLOAD_STATE);

        } else if (Objects.equals(inputMessage.getText(), valid_answer_option2)) {
            userDataCache.setUserCurrentBotState(user_id, BotState.UNPACK_TEXT_IMAGE_UPLOAD_STATE);
        } else if (Objects.equals(inputMessage.getText(), valid_answer_option3)) {
            userDataCache.setUserCurrentBotState(user_id, BotState.ABOUT_INFO_STATE);
        } else {
            replyToUser = messageService
                    .getReplyMessage(String.valueOf(chat_id), "reply.NoSuchOptionErrorMessage");
        }

        return replyToUser;
    }

}
