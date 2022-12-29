package com.komin.steganobot.service;

import org.jvnet.hk2.annotations.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Component
public class ReplyMessageService {

    private LocaleMessageService localeMessageService;

    public ReplyMessageService(LocaleMessageService localeMessageService){
        this.localeMessageService = localeMessageService;
    }

    public SendMessage getReplyMessage(String chat_id, String replyMessage){
        return new SendMessage(chat_id, localeMessageService.getMessage(replyMessage));
    }

    public SendMessage getReplyMessage(String chat_id, String replyMessage, Object ... args){
        return new SendMessage(chat_id, localeMessageService.getMessage(replyMessage, args));
    }
}
