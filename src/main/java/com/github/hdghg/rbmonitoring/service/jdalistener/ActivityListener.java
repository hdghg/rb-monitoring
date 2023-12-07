package com.github.hdghg.rbmonitoring.service.jdalistener;

import com.github.hdghg.rbmonitoring.repository.ActivityRepository;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.user.GenericUserEvent;
import net.dv8tion.jda.api.events.user.UserTypingEvent;
import net.dv8tion.jda.api.events.user.update.GenericUserPresenceEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateOnlineStatusEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ActivityListener extends ListenerAdapter {

    @Autowired
    private ActivityRepository repository;

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        String author = event.getMessage().getAuthor().toString();
        String channel = event.getMessage().getChannel().toString();
        String text = event.getMessage().getContentRaw();
        repository.persistActivity("MESSAGE_RECEIVED", author, channel, text);
        System.out.println(event);
        super.onMessageReceived(event);
    }

    @Override
    public void onUserTyping(@NotNull UserTypingEvent event) {
        repository.persistActivity("USER_TYPING", event.getUser().toString(), null, null);
        super.onUserTyping(event);
    }

    @Override
    public void onGenericUser(@NotNull GenericUserEvent event) {
        repository.persistActivity("GENERIC_USER", event.getUser().toString(), null, null);
        super.onGenericUser(event);
    }

    @Override
    public void onGenericUserPresence(@NotNull GenericUserPresenceEvent event) {
        repository.persistActivity("GENERIC_USER_PRESENCE", event.getMember().toString(), null, null);
        super.onGenericUserPresence(event);
    }

    @Override
    public void onUserUpdateOnlineStatus(@NotNull UserUpdateOnlineStatusEvent event) {
        repository.persistActivity("USER_UPDATE_ONLINE_STATUS", event.getMember().toString(), null, null);
        super.onUserUpdateOnlineStatus(event);
    }

}
