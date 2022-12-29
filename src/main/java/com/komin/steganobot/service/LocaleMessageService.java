package com.komin.steganobot.service;

import org.jvnet.hk2.annotations.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class LocaleMessageService {
    private final Locale locale;
    private final MessageSource messageSource;

    public LocaleMessageService(@Value("ua-UA") String localeTag, MessageSource messageSource){
        this.messageSource = messageSource;
        this.locale = Locale.forLanguageTag(localeTag);
    }

    public String getMessage(String message){
        return messageSource.getMessage(message, null, locale);
    }

    public String getMessage(String message, Object ... args){
        return messageSource.getMessage(message, args, locale);
    }
}
