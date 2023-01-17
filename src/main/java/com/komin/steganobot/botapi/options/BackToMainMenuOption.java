package com.komin.steganobot.botapi.options;

import com.komin.steganobot.botapi.BotState;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BackToMainMenuOption {

    BACK_TO_MAIN_MENU_OPTION("option.back-to-main-menu-valid-option", BotState.MAIN_MENU_STATE);

    private final String value;
    private final BotState botState;
}
