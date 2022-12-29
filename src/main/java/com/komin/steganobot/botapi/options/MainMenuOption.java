package com.komin.steganobot.botapi.options;

import com.komin.steganobot.botapi.BotState;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.komin.steganobot.botapi.BotState.ABOUT_INFO_STATE;
import static com.komin.steganobot.botapi.BotState.HIDE_TEXT_IMAGE_UPLOAD_STATE;
import static com.komin.steganobot.botapi.BotState.UNPACK_TEXT_IMAGE_UPLOAD_STATE;

@Getter
@RequiredArgsConstructor
public enum MainMenuOption {

    HIDE_TEXT_OPTION("option.MainMenuStateValidOption1", HIDE_TEXT_IMAGE_UPLOAD_STATE),
    UNPACK_TEXT_OPTION("option.MainMenuStateValidOption2", UNPACK_TEXT_IMAGE_UPLOAD_STATE),
    ABOUT_INFO_OPTION("option.MainMenuStateValidOption3", ABOUT_INFO_STATE);

    private final String value;
    private final BotState botState;

}

