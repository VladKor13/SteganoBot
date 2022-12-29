package com.komin.steganobot.cache;

import com.komin.steganobot.botapi.BotState;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class UserDataCache implements DataCache{
    // Add communication with DB here
    private final Map<Long, BotState> usersBotStates = new HashMap<>();
    private boolean newStateWasSet = false;

    @Override
    public void setUserCurrentBotState(long userId, BotState botState) {
        usersBotStates.put(userId, botState);
        //Add tip
        setNewStateWasSet(true);
        //Add inline keyboard
    }

    @Override
    public BotState getUserCurrentBotState(long userId) {
        BotState botState = usersBotStates.get(userId);
        if (botState == null){
            botState = BotState.INITIAL_STATE;
        }
        return botState;
    }

    public boolean isNewStateWasSet() {
        return newStateWasSet;
    }

    public void setNewStateWasSet(boolean newStateWasSet) {
        this.newStateWasSet = newStateWasSet;
    }
}
