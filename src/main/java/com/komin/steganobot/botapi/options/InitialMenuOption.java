package com.komin.steganobot.botapi.options;

import com.komin.steganobot.botapi.BotState;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InitialMenuOption {

    START_OPTION("option.іnitial-state-valid-option", BotState.MAIN_MENU_STATE);

    private final String value;
    private final BotState followingBotState;
}
