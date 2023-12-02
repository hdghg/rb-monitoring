package com.github.hdghg.rbmonitoring.service;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.security.auth.login.LoginException;

@Service
public class JdaService {

    private final JDA jda;
    private final long channelId;

    public JdaService(
            @Value("${discord.bot.token}") String botToken,
            @Value("${discord.channel.url}") String channelUrl) throws LoginException {
        this.jda = JDABuilder.createDefault(botToken).build();
        this.channelId = Long.parseLong(StringUtils.substringAfterLast(channelUrl, "/"));
    }

    public void sendMessage(String text) {
        jda.getTextChannelById(channelId).sendMessage(text).queue();
    }

}
