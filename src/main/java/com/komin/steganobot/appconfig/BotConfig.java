package com.komin.steganobot.appconfig;

import com.komin.steganobot.MySteganoBot;
import com.komin.steganobot.botapi.TelegramFacade;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.telegram.telegrambots.bots.DefaultBotOptions;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "telegrambot")
public class BotConfig {

    private String webHookPath;
    private String botUserName;
    private String botToken;

    @Bean
    public MySteganoBot mySteganoBot(TelegramFacade telegramFacade) {
        MySteganoBot mySteganoBot = new MySteganoBot(new DefaultBotOptions(), telegramFacade);
        mySteganoBot.setBotUserName(botUserName);
        mySteganoBot.setBotToken(botToken);
        mySteganoBot.setWebHookPath(webHookPath);
        return mySteganoBot;
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource
                = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}
