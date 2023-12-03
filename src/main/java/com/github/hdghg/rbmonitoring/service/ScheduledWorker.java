package com.github.hdghg.rbmonitoring.service;

import com.github.hdghg.rbmonitoring.model.RbEntry;
import com.github.hdghg.rbmonitoring.model.Transition;
import com.github.hdghg.rbmonitoring.repository.RbInfoRepository;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.Instant;
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
    private RbInfoRepository rbInfoRepository;

    @Autowired
    private JdaService jdaService;

    private final RestTemplate restTemplate = new RestTemplate();

    public ScheduledWorker(HtmlParser htmlParser, TransitionService transitionService) {
        this.htmlParser = htmlParser;
        this.transitionService = transitionService;
    }

    private void ensureLevels(List<RbEntry> rbEntries) {
        Map<String, Integer> levelByName = new HashMap<>();
        MapUtils.populateMap(levelByName, rbEntries, RbEntry::getName, RbEntry::getLevel);
        rbInfoRepository.insertOrIgnore(levelByName);
    }

    @Scheduled(fixedDelayString = "${interval.check-rb}", timeUnit = TimeUnit.SECONDS, initialDelay = 15)
    public void checkRb() throws IOException {
        log.info("Checking rb status...");

        List<Transition> currentStatus = transitionService.current();
        Map<String, Transition> statusByName = new HashMap<>();
        MapUtils.populateMap(statusByName, currentStatus, Transition::getName);
        byte[] bytes = restTemplate.getForObject("http://l2c4.ru/index.php?x=boss", byte[].class);
        List<RbEntry> newStatus = htmlParser.parse(new ByteArrayInputStream(bytes));
        ensureLevels(newStatus);

        for (RbEntry entry : newStatus) {
            Transition oldTransition = statusByName.get(entry.getName());
            if (oldTransition == null) {
                transitionService.toAliveStatus(entry.getName(), entry.isAlive(), Instant.EPOCH);
                continue;
            }
            if (oldTransition.isAlive() && !entry.isAlive()) {
                log.info("[dead] RB ({}) {} died!", entry.getLevel(), entry.getName());
                transitionService.toAliveStatus(entry.getName(), entry.isAlive());
                if ("Raid Boss Von Helman".equals(entry.getName())) {
                    continue;
                }
                String disMsg = "\uD83D\uDD34 РБ (" + entry.getLevel() + ") " + entry.getName() + " умер!";
                jdaService.sendMessage(disMsg);
            }

            if (!oldTransition.isAlive() && entry.isAlive()) {
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
