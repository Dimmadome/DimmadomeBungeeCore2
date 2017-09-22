package com.github.kraftykaleb.commands;

import com.github.kraftykaleb.Main;
import com.github.kraftykaleb.objects.Ban;
import com.github.kraftykaleb.objects.Mute;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 * Created by Kraft on 9/12/2017.
 */
public class MuteCommand extends Command {

    private Main plugin;
    public MuteCommand(Main instance, String name) {
        super("mute");
        plugin = instance;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "/mute <player> <length> <reason>"));
            return;
        }

        StringBuilder msgBuilder = new StringBuilder();

        for (int i = 2; i < args.length; i++) {
            msgBuilder.append(args[i]).append(" ");
        }

        String msg = msgBuilder.toString().trim();

        Integer seconds;

        if (args[1].toLowerCase().equals("perm")) {
            new Mute(plugin, args[0], sender, true, msg);

        } else if (args[1].toLowerCase().endsWith("s")) {
            seconds = Integer.parseInt(args[1].replace("s",""));
            new Mute(plugin, args[0], sender, seconds, msg);

        } else if (args[1].toLowerCase().endsWith("m")) {
            seconds = Integer.parseInt(args[1].replace("m",""));
            new Mute(plugin, args[0], sender, seconds, msg);

        } else if (args[1].toLowerCase().endsWith("h")) {
            seconds = Integer.parseInt(args[1].replace("h",""));
            new Mute(plugin, args[0], sender, seconds, msg);

        } else if (args[1].toLowerCase().endsWith("d")) {
            seconds = Integer.parseInt(args[1].replace("d",""));
            new Mute(plugin, args[0], sender, seconds, msg);
        }
    }
}
