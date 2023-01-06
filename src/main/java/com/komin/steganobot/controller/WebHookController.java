package com.komin.steganobot.controller;

import com.komin.steganobot.MySteganoBot;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

@RestController
public class WebHookController {

    private final MySteganoBot mySteganoBot;

    public WebHookController(MySteganoBot mySteganoBot) {
        this.mySteganoBot = mySteganoBot;
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public BotApiMethod<?> onUpdateReceived(@RequestBody Update update) {
        return mySteganoBot.onWebhookUpdateReceived(update);
    }
}
