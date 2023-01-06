package com.komin.steganobot.botapi;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface InputMessageHandler {

    SendMessage handle(Message message);

    BotState getHandlerName();

    SendMessage getStateTip(Message message);
}
