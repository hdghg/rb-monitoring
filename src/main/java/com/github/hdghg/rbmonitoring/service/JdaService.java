package com.github.hdghg.rbmonitoring.service;

import com.github.hdghg.rbmonitoring.service.jdalistener.AllListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.security.auth.login.LoginException;
import java.util.Collections;

@Service
public class JdaService {

    private final JDA jda;
    private final long channelId;

    public JdaService(
            @Value("${discord.bot.token}") String botToken,
            @Value("${discord.channel.url}") String channelUrl,
            AllListener allListener) throws LoginException {
        this.jda = JDABuilder.createDefault(botToken)
                .enableIntents(Collections.singletonList(GatewayIntent.DIRECT_MESSAGES))
                .addEventListeners(allListener)
                .build();
        jda.upsertCommand("all", "List all rb").queue();

        this.channelId = Long.parseLong(StringUtils.substringAfterLast(channelUrl, "/"));
    }

    public void sendMessage(String text) {
        jda.getTextChannelById(channelId).sendMessage(text).queue();
    }

}
