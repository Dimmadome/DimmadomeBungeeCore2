package com.github.kraftykaleb.commands;

import com.github.kraftykaleb.Main;
import me.ramswaroop.jbot.core.slack.Bot;
import me.ramswaroop.jbot.core.slack.Controller;
import me.ramswaroop.jbot.core.slack.EventType;
import me.ramswaroop.jbot.core.slack.models.Event;
import net.md_5.bungee.api.ChatColor;
import org.springframework.web.socket.WebSocketSession;

/**
 * Created by Kraft on 6/1/2017.
 */
public class SlackBot extends Bot {
    @Override
    public String getSlackToken() {
        return "xoxb-191922248310-6xlopSsAqDP4D1fMVmsrhjHb";
    }

    @Override
    public Bot getSlackBot() {
        return this;
    }

    @Controller(events = EventType.MESSAGE)
    public static void onSlackMessageSent(WebSocketSession session, Event event) {
        if (event.getChannel().getName() == "guild-ideas") {
            Main.sendStaffMessage(ChatColor.YELLOW + "[SLACK] " + event.getUser().getName() + ChatColor.WHITE + event.getText());
            return;
        }

    }
}
