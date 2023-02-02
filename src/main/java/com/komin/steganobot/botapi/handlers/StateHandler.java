package com.komin.steganobot.botapi.handlers;

import com.komin.steganobot.botapi.BotState;
import com.komin.steganobot.botapi.options.MenuOptions;
import com.komin.steganobot.cache.UserDataCache;
import com.komin.steganobot.service.LocaleMessageService;
import com.komin.steganobot.service.ReplyMessageService;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class StateHandler {

    final UserDataCache userDataCache;
    final ReplyMessageService messageService;
    final LocaleMessageService localeMessageService;

    public StateHandler(UserDataCache userDataCache, ReplyMessageService messageService, LocaleMessageService localeMessageService) {
        this.userDataCache = userDataCache;
        this.messageService = messageService;
        this.localeMessageService = localeMessageService;
    }

    SendMessage checkMessageForRightOption(Message inputMessage) {
        long chatID = inputMessage.getChatId();
        long userID = inputMessage.getFrom().getId();
        BotState currentBotState = userDataCache.getUserCurrentBotState(userID);

        Optional<MenuOptions> stateMenuOptionOptional =
                Stream.of(MenuOptions.values())
                      .filter(option -> Objects.equals(localeMessageService.getMessage(option.getValue()),
                              inputMessage.getText()))
                      .filter(option -> option.getOptionAvailableStates().contains(currentBotState))
                      .findFirst();

        if (stateMenuOptionOptional.isEmpty()) {
            if (inputMessage.getPhoto() != null && inputMessage.getText() == null) {
                return messageService
                        .getReplyMessage(String.valueOf(chatID), "reply.upload-photo-as-file-error-message");
            }
            return messageService
                    .getReplyMessage(String.valueOf(chatID), "reply.no-such-option-error-message");
        }

        MenuOptions chosenOption = stateMenuOptionOptional.get();
        userDataCache.setUserCurrentBotState(userID, chosenOption.getFollowingBotState());

        return null;
    }

}
