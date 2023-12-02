package com.github.hdghg.rbmonitoring.service;

import com.github.hdghg.rbmonitoring.model.RbEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private HtmlParser htmlParser;

    @Autowired
    private TransitionService transitionService;

    @Autowired
    private JdaService jdaService;

    private final RestTemplate restTemplate = new RestTemplate();

    private final Map<String, Boolean> statusMap = new HashMap<>();

    public ScheduledWorker(
            HtmlParser htmlParser, TransitionService transitionService) throws LoginException {
        this.htmlParser = htmlParser;
        this.transitionService = transitionService;
    }

    @Scheduled(fixedDelayString = "${interval.check-rb}", timeUnit = TimeUnit.SECONDS, initialDelay = 15)
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
                log.info("[dead] RB ({}) {} died!", entry.getLevel(), entry.getName());
                transitionService.toAliveStatus(entry.getName(), entry.isAlive());
                if ("Raid Boss Von Helman".equals(entry.getName())) {
                    continue;
                }
                String disMsg = "\uD83D\uDD34 РБ (" + entry.getLevel() + ") " + entry.getName() + " умер!";
                jdaService.sendMessage(disMsg);

            }
            if (!putResult && entry.isAlive()) {
                log.info("[live] RB ({}) {} alive!", entry.getLevel(), entry.getName());
                transitionService.toAliveStatus(entry.getName(), entry.isAlive());
                if ("Raid Boss Von Helman".equals(entry.getName())) {
                    continue;
                }
                String disMsg = "\uD83D\uDFE2 РБ (" + entry.getLevel() + ") " + entry.getName() + " воскрес!";
                jdaService.sendMessage(disMsg);
            }
        }
    }
}
