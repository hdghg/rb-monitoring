package com.github.hdghg.rbmonitoring.service;

import com.github.hdghg.rbmonitoring.model.Transition;
import com.github.hdghg.rbmonitoring.repository.TransitionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Service
public class TransitionService {

    @Autowired
    private TransitionRepository transitionRepository;

    public void toAliveStatus(String name, boolean aliveStatus) {
        toAliveStatus(name, aliveStatus, Instant.now());
    }

    public void toAliveStatus(String name, boolean aliveStatus, Instant when) {
        Transition transition = new Transition();
        transition.setName(name);
        transition.setAlive(aliveStatus);
        transition.setAt(Timestamp.from(when));
        transitionRepository.insert(transition);
    }


    public List<Transition> fetchAll() {
        return transitionRepository.listAll();
    }

    public List<Transition> current() {
        return transitionRepository.currentStatus();
    }
}
