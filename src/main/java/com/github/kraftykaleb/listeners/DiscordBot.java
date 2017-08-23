package com.github.kraftykaleb.listeners;

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

    String botToken = "";

    public DiscordBot (String token) {
        DiscordAPI api = Javacord.getApi(token, true);
        botToken = token;
        api.connect(new FutureCallback<DiscordAPI>() {
            @Override
            public void onSuccess(DiscordAPI discordAPI) {
                api.registerListener(new MessageCreateListener() {
                    @Override
                    public void onMessageCreate(DiscordAPI discordAPI, Message message) {
                        if (message.getChannelReceiver().getName().equals("staffchat")) {
                            if (message.getAuthor().hasNickname(message.getChannelReceiver().getServer())) {
                                Main.sendStaffMessage(ChatColor.YELLOW + "[DISCORD] " + message.getAuthor().getNickname(message.getChannelReceiver().getServer()) + ChatColor.WHITE + ": " + message.getContent());
                            } else {
                                Main.sendStaffMessage(ChatColor.YELLOW + "[DISCORD] " + message.getAuthor().getName() + ChatColor.WHITE + ": " + message.getContent());
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

        MessageBuilder builder = new MessageBuilder();

        builder.append(name + ": " + message);
        String msg = builder.build();



        DiscordAPI api = Javacord.getApi("MzQ5MzkwOTU5MzQ5MzM0MDE2.DH4Unw.VoYKLJNM55eW9Uusklsb2Eas9qw", true);

        api.connect(new FutureCallback<DiscordAPI>() {
            @Override
            public void onSuccess(DiscordAPI discordAPI) {
                //api.getServerById("349290276646551563").getChannelById("349390716159655937").sendMessage(name + ": " + message);

                api.getYourself().updateNickname(api.getServerById("349290276646551563"), "StaffChat: " + name);
                final MessageReceiver receiver = api.getChannelById("349390716159655937");
                receiver.sendMessage(message);
                api.getYourself().updateNickname(api.getServerById("349290276646551563"), "StaffChat");

            }

            @Override
            public void onFailure(Throwable throwable) {

            }
        });

         //api.getServerById("349290276646551563").getChannelById("349390716159655937").sendMessage(name + ": " + message);
    }
}
