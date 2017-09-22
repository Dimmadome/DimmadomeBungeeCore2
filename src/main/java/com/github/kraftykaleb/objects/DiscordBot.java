package com.github.kraftykaleb.objects;

import com.github.kraftykaleb.Main;
import com.google.common.util.concurrent.FutureCallback;
import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.ImplDiscordAPI;
import de.btobastian.javacord.Javacord;
import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.entities.message.MessageBuilder;
import de.btobastian.javacord.entities.message.MessageReceiver;
import de.btobastian.javacord.listener.message.MessageCreateListener;
import de.btobastian.javacord.utils.handler.message.MessageCreateHandler;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.concurrent.Future;

/**
 * Created by Scout on 8/22/2017.
 */
public class DiscordBot implements Listener {

    private Main plugin;

    String botToken = "";

    public DiscordBot (String token, Main instance) {
        plugin = instance;
        DiscordAPI api = Javacord.getApi(token, true);
        botToken = token;
        api.connect(new FutureCallback<DiscordAPI>() {
            @Override
            public void onSuccess(DiscordAPI discordAPI) {
                api.registerListener((MessageCreateListener) (discordAPI1, message) -> {
                    if (message.getChannelReceiver().getId().equals("349390716159655937")) {
                        if (!message.getAuthor().getId().equals("349390959349334016")) {
                            if (message.getAuthor().hasNickname(message.getChannelReceiver().getServer())) {
                                plugin.sendStaffMessage(ChatColor.YELLOW + "[DISCORD] " + message.getAuthor().getNickname(message.getChannelReceiver().getServer()) + ChatColor.WHITE + ": " + message.getContent());
                            } else {
                                plugin.sendStaffMessage(ChatColor.YELLOW + "[DISCORD] " + message.getAuthor().getName() + ChatColor.WHITE + ": " + message.getContent());
                            }
                        }
                    }
                });

            }

            @Override
            public void onFailure(Throwable throwable) {

            }
        });
    }

    public void sendDiscordMessage(String name, String message) {

        DiscordAPI api = Javacord.getApi("MzQ5MzkwOTU5MzQ5MzM0MDE2.DJnM1Q.QbGSSzS_BZUpCSLB41wvFaF-Fv8", true);

        api.connect(new FutureCallback<DiscordAPI>() {
            @Override
            public void onSuccess(DiscordAPI discordAPI) {

                api.getYourself().updateNickname(api.getServerById("349290276646551563"), "StaffChat: " + name);
                final MessageReceiver receiver = api.getChannelById("349390716159655937");
                receiver.sendMessage(name + ": " + message);
                //api.getYourself().updateNickname(api.getServerById("349290276646551563"), "StaffChat");

            }

            @Override
            public void onFailure(Throwable throwable) {

            }
        });
    }
}
