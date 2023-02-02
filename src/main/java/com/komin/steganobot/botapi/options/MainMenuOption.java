package com.komin.steganobot.botapi.options;

import com.komin.steganobot.botapi.BotState;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static com.komin.steganobot.botapi.BotState.ABOUT_INFO_STATE;
import static com.komin.steganobot.botapi.BotState.HIDE_TEXT_IMAGE_UPLOAD_STATE;
import static com.komin.steganobot.botapi.BotState.UNPACK_TEXT_IMAGE_UPLOAD_STATE;

@Getter
@RequiredArgsConstructor
public enum MainMenuOption {

    HIDE_TEXT_OPTION("option.main-menu-state-hide-text-option", HIDE_TEXT_IMAGE_UPLOAD_STATE),
    UNPACK_TEXT_OPTION("option.main-menu-state-unpack-text-option", UNPACK_TEXT_IMAGE_UPLOAD_STATE),
    ABOUT_INFO_OPTION("option.main-menu-state-about-info-option", ABOUT_INFO_STATE);

    private final String value;
    private final BotState followingBotState;

}

