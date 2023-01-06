package com.komin.steganobot.service;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Component
public class ReplyMessageService {

    private final LocaleMessageService localeMessageService;

    public ReplyMessageService(LocaleMessageService localeMessageService) {
        this.localeMessageService = localeMessageService;
    }

    public SendMessage getReplyMessage(String chat_id, String replyMessage) {
        return new SendMessage(chat_id, localeMessageService.getMessage(replyMessage));
    }

    public SendMessage getReplyMessage(String chat_id, String replyMessage, Object... args) {
        return new SendMessage(chat_id, localeMessageService.getMessage(replyMessage, args));
    }
}
