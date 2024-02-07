package com.github.hdghg.rbmonitoring.service.jdalistener;

import com.github.hdghg.rbmonitoring.model.CharacterBonus;
import com.github.hdghg.rbmonitoring.repository.BonusRepository;
import com.github.hdghg.rbmonitoring.repository.CharacterRepository;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    private void printButtons(SlashCommandEvent event, String party) {
        Pair<List<CharacterBonus>, List<CharacterBonus>> bonusStatus = bonusRepository.bonusStatus(party);
        List<CharacterBonus> last5 = bonusStatus.getLeft();
        List<CharacterBonus> next20 = bonusStatus.getRight();
        ReplyAction replyAction = event.reply("Раскладка по бонусам:").setEphemeral(true);
        if (!last5.isEmpty()) {
            List<Button> buttons = new ArrayList<>();
            for (int i = 0; i < last5.size(); i++) {
                buttons.add(Button.primary("1row" + i, last5.get(i).getNickname()));
            }
            replyAction.addActionRow(buttons);
        }
        if (!next20.isEmpty()) {
            List<List<CharacterBonus>> partitions = ListUtils.partition(next20, 5);
            for (int j = 0; j < partitions.size(); j++) {
                List<CharacterBonus> partition = partitions.get(j);
                List<Button> buttons = new ArrayList<>();
                for (int i = 0; i < partition.size(); i++) {
                    buttons.add(Button.primary(j + "row" + i, partition.get(i).getNickname()));
                }
                replyAction.addActionRow(buttons);
            }
        }
        replyAction.queue();
    }

    @Override
    public void onSlashCommand(SlashCommandEvent event) {
        List<OptionMapping> options = event.getOptions();
        String party = options.stream()
                .filter(o -> "party".equals(o.getName()))
                .map(OptionMapping::getAsString)
                .findFirst().orElse(null);
        if (event.getName().equalsIgnoreCase("reg-bonus")) {
            register(event, party);
        } else if (event.getName().equalsIgnoreCase("dereg-bonus")) {
            deregister(event);
        } else if (event.getName().equalsIgnoreCase("bonus")) {
            printButtons(event, party);

//            event.reply("bonbus")
//                    .setEphemeral(true)
//                    .addActionRow(
//                            Button.primary("1row1", "Sela (90min)"),
//                            Button.primary("1row2", "Vardas (80min)"),
//                            Button.primary("1row3", "IIaJIbMa (45min)"),
//                            Button.primary("1row4", "POSTOK (30min)"),
//                            Button.primary("1row5", "DejaVu(11min)")
//                    )
//                    .addActionRow(
//                            Button.danger("2row1", "ptahaPP (44h)"),
//                            Button.danger("2row2", "ptahaEE (44h)"),
//                            Button.success("2row3", "ptahaDD (25h)"),
//                            Button.success("2row4", "Anel (24h)"),
//                            Button.success("2row5", "Yavanna (23h)")
//                    ).addActionRow(
//                            Button.success("3row1", "Nessa (22h)"),
//                            Button.success("3row2", "Xpom (21h)"),
//                            Button.success("3row3", "KAJIbCOH (20h)"),
//                            Button.secondary("3row4", "TMC (19h)"),
//                            Button.secondary("3row5", "RyanGosling (17h)")
//                    ).addActionRow(
//                            Button.secondary("4row1", "witchy (16h)"),
//                            Button.secondary("4row2", "abc (16h)"),
//                            Button.secondary("4row3", "asdasd (15h)"),
//                            Button.secondary("4row4", "sddhgff (13h)"),
//                            Button.secondary("4row5", "dfdfg (11h)")
//                    )
//                    .addActionRow(
//                            Button.secondary("5row1", "dsdfsdfs (9h)"),
//                            Button.secondary("5row2", "abcd (2h)"),
//                            Button.secondary("5row3", "Sela (90min)"),
//                            Button.secondary("5row4", "Vardas (80min)"),
//                            Button.secondary("5row5", "IIaJIbMa (45min)")
//                    )
//                    .queue();


        }
    }
}
