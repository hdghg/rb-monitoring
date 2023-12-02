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
        Transition transition = new Transition();
        transition.setName(name);
        transition.setAlive(aliveStatus);
        transition.setAt(Timestamp.from(Instant.now()));
        transitionRepository.insert(transition);
    }

    public List<Transition> fetchAll() {
        return transitionRepository.listAll();
    }
}
