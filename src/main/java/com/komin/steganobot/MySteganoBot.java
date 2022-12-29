package com.komin.steganobot;

import com.komin.steganobot.botapi.TelegramFacade;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class MySteganoBot extends TelegramWebhookBot {

    private String webHookPath;
    private String botUserName;
    private String botToken;

    private final TelegramFacade telegramFacade;

    public MySteganoBot(DefaultBotOptions botOptions, TelegramFacade telegramFacade){
        super(botOptions);
        this.telegramFacade = telegramFacade;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public String getBotUsername() {
        return botUserName;
    }

    @Override
    public String getBotPath() {
        return webHookPath;
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        sendMessage(update);
        sendTip(update);
        return null;
    }

    private void sendMessage(Update update){
        SendMessage replyMessageToUser = telegramFacade.handleUpdate(update);
        try{
            execute(replyMessageToUser);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendTip(Update update){
        Long user_Id = update.getMessage().getFrom().getId();
        String tipMessageToUser = telegramFacade.handleTip(user_Id);
        if (!tipMessageToUser.isEmpty()){
            try{
                execute(new SendMessage(user_Id.toString(), tipMessageToUser));
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    public void setWebHookPath(String webHookPath) {
        this.webHookPath = webHookPath;
    }

    public void setBotUserName(String botUserName) {
        this.botUserName = botUserName;
    }

    public void setBotToken(String botToken) {
        this.botToken = botToken;
    }
}
