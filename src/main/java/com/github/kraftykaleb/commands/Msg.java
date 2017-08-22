package com.github.kraftykaleb.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.protocol.packet.Chat;

/**
 * Created by Kraft on 4/19/2017.
 */
public class Msg extends Command {
    public Msg(String name) {
        super("msg");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "/msg <player> <message>"));
            return;
        }

        ProxiedPlayer p = ProxyServer.getInstance().getPlayer(args[0]);

        if (p == null) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "Could not find " + args[0]));
            return;
        }

        StringBuilder msgBuilder = new StringBuilder();

        for (int i = 1; i < args.length; i++) {
            msgBuilder.append(args[i]).append(" ");
        }

        String msg = ChatColor.translateAlternateColorCodes('&', msgBuilder.toString().trim());

        ProxiedPlayer t = (ProxiedPlayer) sender;
        p.sendMessage(new TextComponent(ChatColor.LIGHT_PURPLE + "From " + t.getDisplayName() + ": " + ChatColor.GRAY + msg));
        sender.sendMessage(new TextComponent(ChatColor.LIGHT_PURPLE + "To " + p.getDisplayName() + ": " + ChatColor.GRAY + msg));
    }
}
