package com.github.kraftykaleb.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.plugin.Command;

/**
 * Created by Kraft on 4/19/2017.
 */
public class LobbyCommand extends Command {

    public LobbyCommand() {
        super("lobby", "", "hub");
    }

    @Override
    public void execute(CommandSender sender, String[] strings) {
        sender.sendMessage(new ComponentBuilder(ChatColor.RED + " Command Unavailable.").create());
    }
}
