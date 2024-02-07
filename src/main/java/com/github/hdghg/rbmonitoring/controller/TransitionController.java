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

    @GetMapping("/current")
    public List<Transition> current() {
        return transitionService.current();
    }

    @GetMapping("/test-message")
    public void testMessage() {
        String disMsg = "Версия 0.2.0: Добавлены команды для отслеживания бонусов: /bonus, /reg-bonus, /dereg-bonus\n" +
                "- /reg-bonus добавляет персонажа в систему\n" +
                "- /dereg-bonus удаляет персонажа из системы\n" +
                "- /bonus Выводит раскладку по бонусам, с возможностью регистрации факта взятия бонуса\n" +
                "- Все команды и ответы на них видны только тому кто выполняет команды, поэтому можно выполнять команды не боясь заспамить чат";
        jdaService.sendMessage(disMsg);
    }
}
