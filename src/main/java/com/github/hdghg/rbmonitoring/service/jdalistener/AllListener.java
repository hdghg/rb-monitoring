package com.github.hdghg.rbmonitoring.service.jdalistener;

import com.github.hdghg.rbmonitoring.model.Transition;
import com.github.hdghg.rbmonitoring.repository.TransitionRepository;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

@Component
public class AllListener extends ListenerAdapter {

    @Autowired
    private TransitionRepository repository;

    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        if (event.getName().equals("last30")) {
            List<Pair<Transition, Integer>> transitions = repository.currentStatusWithLevels().stream()
                    .limit(30).collect(Collectors.toList());

            int len = transitions.stream()
                    .mapToInt(t -> t.getKey().getName().length())
                    .max().orElse(0);

            StringBuilder sb = new StringBuilder("```ml\n");
            FastDateFormat format = FastDateFormat.getInstance("yy-MM-dd HH:mm:ss", TimeZone.getTimeZone("Europe/Moscow"));

            for (Pair<Transition, Integer> t : transitions) {
                sb.append(t.getRight()).append(" ");
                sb.append(StringUtils.rightPad(StringUtils.lowerCase(t.getLeft().getName()), len + 1));
                sb.append(StringUtils.rightPad(t.getLeft().isAlive() ? "\"Alive\"" : "Dead", 8));

                if (t.getLeft().getAt().getTime() == 0) {
                    sb.append("<unknown>").append("\n");
                } else {
                    sb.append(format.format(t.getLeft().getAt())).append("\n");
                }
            }
            sb.append("```");
            event.reply(sb.toString()).setEphemeral(true).queue();
        }
    }

}
