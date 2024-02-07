package com.github.hdghg.rbmonitoring.service.jdalistener;

import com.github.hdghg.rbmonitoring.model.CharacterBonus;
import com.github.hdghg.rbmonitoring.repository.BonusRepository;
import com.github.hdghg.rbmonitoring.repository.CharacterRepository;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.ButtonStyle;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
public class BonusCommandListener extends ListenerAdapter {

    @Autowired
    private CharacterRepository characterRepository;

    @Autowired
    private BonusRepository bonusRepository;

    private void register(SlashCommandEvent event, String party) {
        String character = event.getOptions().stream()
                .filter(o -> "character".equals(o.getName()))
                .map(OptionMapping::getAsString)
                .findFirst().orElseThrow(() -> new RuntimeException("character"));
        String reply;
        if (characterRepository.insertOrIgnore(character, party)) {
            reply = "Персонаж с ником " + character +
                    " зарегистрирован " +
                    (party == null ? "без пати" : "в пати " + party);
        } else {
            reply = "Персонаж с ником " + character +
                    " уже зарегистрирован";
        }
        event.reply(reply).setEphemeral(true).queue();
    }

    private void deregister(SlashCommandEvent event) {
        String character = event.getOptions().stream()
                .filter(o -> "character".equals(o.getName()))
                .map(OptionMapping::getAsString)
                .findFirst().orElseThrow(() -> new RuntimeException("character"));
        String reply;
        if (characterRepository.delete(character)) {
            reply = "Персонаж с ником " + character +
                    " удален из системы";
        } else {
            reply = "Персонаж с ником " + character +
                    " не был найден";
        }
        event.reply(reply).setEphemeral(true).queue();
    }

    private Duration duration(Timestamp timestamp) {
        Instant start = timestamp == null ? new Timestamp(0).toInstant() : timestamp.toInstant();
        return Duration.between(start, Instant.now());
    }

    private String prettifyDuration(Duration duration) {
        if (duration.compareTo(Duration.ofHours(72)) > 0) {
            return ">3д";
        } else if (duration.compareTo(Duration.ofHours(3)) > 0) {
            return duration.toHours() + "ч";
        } else if (duration.compareTo(Duration.ofMinutes(2)) > 0) {
            return duration.toMinutes() + "мин";
        } else {
            return duration.toMillis() / 1000 + "сек";
        }
    }

    private ButtonStyle buttonStyle(Duration duration) {
        if (duration.compareTo(Duration.ofHours(32)) > 0) {
            return ButtonStyle.DANGER;
        } else if (duration.compareTo(Duration.ofHours(20)) > 0) {
            return ButtonStyle.SUCCESS;
        } else {
            return ButtonStyle.SECONDARY;
        }
    }

    private Message bonusMessage(String party) {
        Pair<List<CharacterBonus>, List<CharacterBonus>> bonusStatus = bonusRepository.bonusStatus(party);
        String partyStr = StringUtils.defaultString(party);
        List<CharacterBonus> last5 = bonusStatus.getLeft();
        List<CharacterBonus> next20 = bonusStatus.getRight();
        List<ActionRow> actionRows = new ArrayList<>();
        if (!last5.isEmpty()) {
            List<Button> buttons = new ArrayList<>();
            for (int i = 0; i < last5.size(); i++) {
                CharacterBonus cb = last5.get(i);
                Duration duration = duration(cb.getAt());
                String locDuration = " (" + prettifyDuration(duration) + ")";
                buttons.add(Button.primary("past" + cb.getId() + ":" + partyStr, cb.getNickname() + locDuration).asDisabled());
            }
            actionRows.add(ActionRow.of(buttons));
        }
        if (!next20.isEmpty()) {
            List<List<CharacterBonus>> partitions = ListUtils.partition(next20, 5);
            for (int j = 0; j < partitions.size(); j++) {
                List<CharacterBonus> partition = partitions.get(j);
                List<Button> buttons = new ArrayList<>();
                for (int i = 0; i < partition.size(); i++) {
                    CharacterBonus cb = partition.get(i);
                    Duration duration = duration(cb.getAt());
                    String locDuration = " (" + prettifyDuration(duration) + ")";
                    buttons.add(Button.of(buttonStyle(duration), "" + cb.getId() + ":" + partyStr, cb.getNickname() + locDuration));
                }
                actionRows.add(ActionRow.of(buttons));
            }
        }
        return new MessageBuilder("**Раскладка по бонусам.**\n" +
                "Возьмите бонусы для персонажей в красных и зеленых прямоугольниках, после этого нажмите на соответствующий прямоугольник.")
                .setActionRows(actionRows)
                .build();
    }

    private void printButtons(SlashCommandEvent event, String party) {
        event.reply(bonusMessage(party)).setEphemeral(true).queue();
    }

    @Override
    public void onSlashCommand(SlashCommandEvent event) {
        List<OptionMapping> options = event.getOptions();
        String party = options.stream()
                .filter(o -> "party".equals(o.getName()))
                .map(OptionMapping::getAsString)
                .findFirst().orElse(null);
        if (party != null && party.contains(":")) {
            event.reply("Ошибка. Имя party не может содержать двоеточий").setEphemeral(true).queue();
        }
        if (party != null && party.length() > 12) {
            event.reply("Ошибка. Имя party не может быть длиннее 12 символов").setEphemeral(true).queue();
        }
        if (event.getName().equalsIgnoreCase("reg-bonus")) {
            register(event, party);
        } else if (event.getName().equalsIgnoreCase("dereg-bonus")) {
            deregister(event);
        } else if (event.getName().equalsIgnoreCase("bonus")) {
            printButtons(event, party);
        }
    }

    @Override
    public void onButtonClick(@NotNull ButtonClickEvent event) {
        Button button = event.getButton();
        if (button == null) {
            event.reply("error - no button").setEphemeral(true).queue();
            return;
        }
        String idParty = StringUtils.removeStart(button.getId(), "past");
        if (idParty == null) {
            event.reply("error - empty id").setEphemeral(true).queue();
            return;
        }
        String[] split = idParty.split(":");
        int id = Integer.parseInt(split[0]);
        String party = split.length > 1 ? split[1] : null;
        bonusRepository.registerBonusTaken(id);

        event.getInteraction().editMessage(bonusMessage(party)).queue();
    }
}
