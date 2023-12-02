package com.github.hdghg.rbmonitoring.controller;

import com.github.hdghg.rbmonitoring.model.Transition;
import com.github.hdghg.rbmonitoring.service.JdaService;
import com.github.hdghg.rbmonitoring.service.TransitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TransitionController {

    @Autowired
    private TransitionService transitionService;

    @Autowired
    private JdaService jdaService;

    @GetMapping("/transition")
    public void transition(
            @RequestParam(value = "name", defaultValue = "Test") String name,
            @RequestParam(value = "alive", defaultValue = "true") Boolean alive) {
        transitionService.toAliveStatus(name, alive);
    }

    @GetMapping("/print")
    public List<Transition> print() {
        return transitionService.fetchAll();
    }

    @GetMapping("/test-message")
    public void testMessage() {
        String disMsg = "Версия 0.0.5:\n- Воскрешения теперь помечаются кружком \uD83D\uDFE2, а смерти - кружком \uD83D\uDD34";
        jdaService.sendMessage(disMsg);
    }
}
