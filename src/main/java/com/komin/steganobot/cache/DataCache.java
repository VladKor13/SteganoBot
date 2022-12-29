package com.komin.steganobot.cache;

import com.komin.steganobot.botapi.BotState;


public interface DataCache {
    void setUserCurrentBotState(long userId, BotState botState);

    BotState getUserCurrentBotState(long userId);

}
