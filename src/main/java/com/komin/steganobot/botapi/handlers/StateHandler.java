package com.komin.steganobot.botapi.handlers;

import LSB.LSBEncoder;
import com.komin.steganobot.botapi.BotState;
import com.komin.steganobot.botapi.options.MenuOptions;
import com.komin.steganobot.cache.UserDataCache;
import com.komin.steganobot.files_service.FilesService;
import com.komin.steganobot.service.LocaleMessageService;
import com.komin.steganobot.service.ReplyMessageService;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
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
        String chatID = String.valueOf(inputMessage.getChatId());
        long userID = inputMessage.getFrom().getId();
        BotState currentBotState = userDataCache.getUserCurrentBotState(userID);

        Optional<MenuOptions> stateMenuOptionOptional =
                Stream.of(MenuOptions.values())
                      .filter(option -> Objects.equals(localeMessageService.getMessage(option.getValue()),
                              inputMessage.getText()))
                      .filter(option -> option.getOptionAvailableStates().contains(currentBotState))
                      .findFirst();

        if (stateMenuOptionOptional.isEmpty()) {
            if (userDataCache.getUserCurrentBotState(userID).equals(BotState.INITIAL_STATE)) {
                logReplyMessage(inputMessage, "tip.initial-state");
                return messageService.getReplyMessage(chatID, "tip.initial-state");
            }

            if (userDataCache.getUserCurrentBotState(userID).equals(BotState.HIDE_TEXT_IMAGE_UPLOAD_STATE)
                    || userDataCache.getUserCurrentBotState(userID).equals(BotState.UNPACK_TEXT_IMAGE_UPLOAD_STATE)) {
                if (inputMessage.getPhoto() != null && inputMessage.getText() == null) {
                    logReplyMessage(inputMessage, "reply.upload-photo-as-file-error-message");
                    return messageService
                            .getReplyMessage(chatID, "reply.upload-photo-as-file-error-message");
                }

                logReplyMessage(inputMessage, "tip.hide-text-image-upload-state");
                return messageService.getReplyMessage(chatID, "tip.hide-text-image-upload-state");
            }

            if (userDataCache.getUserCurrentBotState(userID).equals(BotState.HIDE_TEXT_STRING_UPLOAD_STATE)) {
                if (isStringValid(inputMessage.getText(), chatID)) {
                    FilesService.saveUserString(inputMessage);

                    userDataCache.setUserCurrentBotState(userID, BotState.HIDE_TEXT_RESULT_UPLOAD_STATE);
                    logBotStateChange(inputMessage, BotState.HIDE_TEXT_RESULT_UPLOAD_STATE);
                    logReplyMessage(inputMessage, "reply.text-was-uploaded-message");
                    return messageService.getReplyMessage(chatID, "reply.text-was-uploaded-message");
                } else {
                    logReplyMessage(inputMessage, "reply.invalid-text-error-message");
                    return messageService
                            .getReplyMessage(chatID, "reply.invalid-text-error-message");
                }
            }

            logReplyMessage(inputMessage, "reply.no-such-option-error-message");
            return messageService
                    .getReplyMessage(chatID, "reply.no-such-option-error-message");
        }

        MenuOptions chosenOption = stateMenuOptionOptional.get();
        userDataCache.setUserCurrentBotState(userID, chosenOption.getFollowingBotState());
        logBotStateChange(inputMessage, chosenOption.getFollowingBotState());

        return null;
    }

    void logReplyMessage(Message inputMessage, String reply) {
        log.info("[{}] Reply for User: {}, with text: {}",
                inputMessage.getChatId(),
                inputMessage.getFrom().getUserName(),
                reply);
    }

    void logBotStateChange(Message inputMessage, BotState botState) {
        log.info("[{}] BotState for User: {}, was changed to: {}",
                inputMessage.getChatId(),
                inputMessage.getFrom().getUserName(),
                botState);
    }

    private boolean isStringValid(String text, String chatId) {
        if (text == null) {
            return false;
        }
        return text.length() <= LSBEncoder.evaluatePossibleCharQuantity(chatId);
    }
}
