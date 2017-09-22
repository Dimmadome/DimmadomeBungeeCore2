package com.github.kraftykaleb.commands;

import net.alpenblock.bungeeperms.BungeePerms;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 * Created by Kraft on 9/11/2017.
 */
public class Reports extends Command {
    public Reports(String name) {
        super("reports");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        ProxiedPlayer p = (ProxiedPlayer) sender;

        if (BungeePerms.getInstance().getPermissionsChecker().hasPerm(sender.getName(), "soontm.staff")) {
            p.sendMessage(new TextComponent(ChatColor.YELLOW + "Listing reports... Open reports: " + ChatColor.RED + Report.reportList.size()));
            if (Report.reportList.size() != 0) {
                for (String message : Report.reportList.keySet()) {
                    TextComponent prefix = new TextComponent(ProxyServer.getInstance().getPlayer(message).getDisplayName() + ChatColor.YELLOW + " - " + Report.reportList.get(message));
                    prefix.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/openreport " + message));
                    p.sendMessage(prefix);
                }
            }
        }
    }
}
