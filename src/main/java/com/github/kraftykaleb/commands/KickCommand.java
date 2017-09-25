package com.github.kraftykaleb.commands;

import com.github.kraftykaleb.BungeeCore;
import net.alpenblock.bungeeperms.BungeePerms;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.Arrays;

/**
 * Created by Kraft on 5/31/2017.
 */
public class KickCommand extends Command {

    public KickCommand() {
        super("kick");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(BungeePerms.getInstance().getPermissionsChecker().hasPerm(sender.getName(), "soontm.staff")) {
            if(args.length >= 2) {
                ProxiedPlayer p = ProxyServer.getInstance().getPlayer(args[0]);
                if(p != null) {
                    StringBuilder reason = new StringBuilder();
                    for(int i = 1; i < args.length; i++) {
                        reason.append(args[i]);
                        if(i < (args.length - 1)) {
                            reason.append(" ");
                        }
                    }
                    p.disconnect(new ComponentBuilder("You have been kicked from the network! \nReason:").color(ChatColor.WHITE)
                            .append(reason.toString()).color(ChatColor.RED).create());
                    BungeeCore.getInstance().sendStaffMessage(sender.getName() + " kicked " + p.getName() + " from the network. Reason: " + reason.toString());
                } else {
                    sender.sendMessage(new ComponentBuilder(args[0] + " is not online!").color(ChatColor.RED).create());
                }
            } else {
                sender.sendMessage(new ComponentBuilder("Usage: /kick <player> <reason>").color(ChatColor.RED).create());
            }
        } else {
            sender.sendMessage(new ComponentBuilder("You do not have permission for this command.").color(ChatColor.RED).create());
        }
    }
}
