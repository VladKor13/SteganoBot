package com.komin.steganobot.botapi.options;

import com.komin.steganobot.botapi.BotState;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.komin.steganobot.botapi.BotState.ABOUT_INFO_STATE;
import static com.komin.steganobot.botapi.BotState.HIDE_TEXT_IMAGE_UPLOAD_STATE;
import static com.komin.steganobot.botapi.BotState.HIDE_TEXT_RESULT_UPLOAD_STATE;
import static com.komin.steganobot.botapi.BotState.HIDE_TEXT_STRING_UPLOAD_STATE;
import static com.komin.steganobot.botapi.BotState.INITIAL_STATE;
import static com.komin.steganobot.botapi.BotState.MAIN_MENU_STATE;
import static com.komin.steganobot.botapi.BotState.UNPACK_TEXT_IMAGE_UPLOAD_STATE;
import static com.komin.steganobot.botapi.BotState.UNPACK_TEXT_RESULT_UPLOAD_STATE;

@Getter
@RequiredArgsConstructor
public enum MenuOptions {

    START_OPTION("option.іnitial-state-valid-option", MAIN_MENU_STATE, new ArrayList<>(
            List.of(INITIAL_STATE))),

    BACK_TO_MAIN_MENU_OPTION("option.back-to-main-menu-valid-option", MAIN_MENU_STATE, new ArrayList<>(
            List.of(HIDE_TEXT_IMAGE_UPLOAD_STATE,
                    HIDE_TEXT_STRING_UPLOAD_STATE,
                    HIDE_TEXT_RESULT_UPLOAD_STATE,
                    UNPACK_TEXT_IMAGE_UPLOAD_STATE,
                    UNPACK_TEXT_RESULT_UPLOAD_STATE,
                    ABOUT_INFO_STATE))),

    HIDE_TEXT_OPTION("option.main-menu-state-hide-text-option", HIDE_TEXT_IMAGE_UPLOAD_STATE, new ArrayList<>(
            List.of(MAIN_MENU_STATE))),

    UNPACK_TEXT_OPTION("option.main-menu-state-unpack-text-option", UNPACK_TEXT_IMAGE_UPLOAD_STATE, new ArrayList<>(
            List.of(MAIN_MENU_STATE))),

    ABOUT_INFO_OPTION("option.main-menu-state-about-info-option", ABOUT_INFO_STATE, new ArrayList<>(
            List.of(MAIN_MENU_STATE)));

    private final String value;
    private final BotState followingBotState;
    private final List<BotState> optionAvailableStates;
}

