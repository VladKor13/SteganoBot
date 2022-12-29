package com.komin.steganobot.botapi.handlers;

import com.komin.steganobot.botapi.BotState;
import com.komin.steganobot.botapi.InputMessageHandler;
import com.komin.steganobot.botapi.options.MainMenuOption;
import com.komin.steganobot.cache.UserDataCache;
import com.komin.steganobot.service.LocaleMessageService;
import com.komin.steganobot.service.ReplyMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Component
@RequiredArgsConstructor
public class MainMenuHandler implements InputMessageHandler {

    private final UserDataCache userDataCache;
    private final ReplyMessageService messageService;
    private final LocaleMessageService localeMessageService;

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

    private SendMessage processUsersInput(Message inputMessage) {
        long user_id = inputMessage.getFrom().getId();
        long chat_id = inputMessage.getChatId();

        Optional<MainMenuOption> mainMenuOptionOptional =
                Stream.of(MainMenuOption.values())
                      .filter(option -> Objects.equals(localeMessageService.getMessage(option.getValue()),
                              inputMessage.getText()))
                      .findFirst();

        if (mainMenuOptionOptional.isEmpty()) {
            return messageService
                    .getReplyMessage(String.valueOf(chat_id), "reply.NoSuchOptionErrorMessage");
        }
        MainMenuOption mainMenuOption = mainMenuOptionOptional.get();
        userDataCache.setUserCurrentBotState(user_id, mainMenuOption.getBotState());

        return null;
    }

}
