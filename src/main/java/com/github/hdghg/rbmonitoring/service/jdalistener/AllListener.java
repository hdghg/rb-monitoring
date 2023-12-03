package com.github.hdghg.rbmonitoring.service.jdalistener;

import com.github.hdghg.rbmonitoring.model.Transition;
import com.github.hdghg.rbmonitoring.service.TransitionService;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AllListener extends ListenerAdapter {

    @Autowired
    private TransitionService transitionService;

    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        if (!event.getName().equals("all")) {
            return;
        }
        List<Transition> transitions = transitionService.current();

        int len = transitions.stream()
                .mapToInt(t -> t.getName().length())
                .max().orElse(0);

        StringBuilder sb = new StringBuilder("```ml\n");
        for (Transition t : transitions) {
            sb.append(StringUtils.rightPad(StringUtils.lowerCase(t.getName()), len + 2));
            sb.append(t.isAlive() ? "\"Alive\"" : "Dead").append("\n");
        }
        sb.append("```");
        event.reply(sb.toString()).setEphemeral(true).queue();
    }

}
