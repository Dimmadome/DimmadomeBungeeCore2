package com.github.kraftykaleb.commands;

import net.alpenblock.bungeeperms.BungeePerms;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.HashMap;

/**
 * Created by Kraft on 9/11/2017.
 */
public class ReportCommand extends Command {

    public static HashMap<String, String> reportList = new HashMap<>();

    public ReportCommand() {
        super("report");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "/report <player> <reason>"));
            return;
        }

        StringBuilder msgBuilder = new StringBuilder();

        for (int i = 1; i < args.length; i++) {
            msgBuilder.append(args[i]).append(" ");
        }

        String msg = msgBuilder.toString().trim();
        if (!reportList.containsKey(args[0].toLowerCase())) {
            if (ProxyServer.getInstance().getPlayer(args[0]) != null) {
                sender.sendMessage(new TextComponent(ChatColor.WHITE + "[REPORT] " + ChatColor.GREEN + "Successfully created a report for'" + args[0] + "'! A staff member should look into it shortly!"));
                reportList.put(args[0].toLowerCase(), msg);

                for (ProxiedPlayer player1 : ProxyServer.getInstance().getPlayers()) {
                    if (BungeePerms.getInstance().getPermissionsManager().getUser(player1.getName()).hasPerm("soontm.staff")) {
                        player1.sendMessage(new TextComponent(ChatColor.DARK_GREEN + "[STAFF] " + ChatColor.RED + "[BOT] SoonTM: " + ChatColor.WHITE + "A report was created for " + ProxyServer.getInstance().getPlayer(args[0]).getName()));
                    }
                }

                return;
            } else {
                sender.sendMessage(new TextComponent(ChatColor.WHITE + "[REPORT] " + ChatColor.RED + "I could not find '" + args[0] + "' they might be offline!"));
                return;
            }

        } else {
            sender.sendMessage(new TextComponent(ChatColor.WHITE + "[REPORT] " + ChatColor.RED + "There's already a report for '" + args[0] + "' open, thanks trying it!"));
            return;
        }

    }
}
