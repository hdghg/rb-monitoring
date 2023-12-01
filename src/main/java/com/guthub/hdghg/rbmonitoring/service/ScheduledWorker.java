package com.guthub.hdghg.rbmonitoring.service;

import com.guthub.hdghg.rbmonitoring.model.RbEntry;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.security.auth.login.LoginException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class ScheduledWorker {
    private static final Logger log = LoggerFactory.getLogger(ScheduledWorker.class);

    private final HtmlParser htmlParser;
    private final JDA jda;
    private final long channelId;
    private final RestTemplate restTemplate = new RestTemplate();

    private final Map<String, Boolean> statusMap = new HashMap<>();

    public ScheduledWorker(
            HtmlParser htmlParser,
            @Value("${discord.bot.token}") String botToken,
            @Value("${discord.channel.url}") String channelUrl) throws LoginException {
        this.htmlParser = htmlParser;
        this.jda = JDABuilder.createDefault(botToken).build();
        this.channelId = Long.parseLong(StringUtils.substringAfterLast(channelUrl, "/"));
    }

    @Scheduled(fixedDelay = 267, timeUnit = TimeUnit.SECONDS, initialDelay = 15)
    public void checkRb() throws IOException {
        log.info("Checking rb status...");

        byte[] bytes = restTemplate.getForObject("http://l2c4.ru/index.php?x=boss", byte[].class);
        List<RbEntry> entries = htmlParser.parse(new ByteArrayInputStream(bytes));

        for (RbEntry entry : entries) {
            Boolean putResult = statusMap.put(entry.getName(), entry.isAlive());
            if (putResult == null) {
                continue;
            }
            if (putResult && !entry.isAlive()) {
                String msg = "РБ (" + entry.getLevel() + ") " + entry.getName() + " умер!";
                log.info(msg);
                jda.getTextChannelById(channelId).sendMessage(msg).queue();

            }
            if (!putResult && entry.isAlive()) {
                String msg = "РБ (" + entry.getLevel() + ") " + entry.getName() + " воскрес!";
                log.info(msg);
                jda.getTextChannelById(channelId).sendMessage(msg).queue();
            }
        }
    }
}
