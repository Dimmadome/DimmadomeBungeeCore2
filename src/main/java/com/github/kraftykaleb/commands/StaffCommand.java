package com.github.kraftykaleb.commands;

import net.alpenblock.bungeeperms.BungeePerms;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 * Created by Kraft on 4/20/2017.
 */
public class StaffCommand extends Command {

    public StaffCommand() {
        super("staff");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (BungeePerms.getInstance().getPermissionsChecker().hasPerm(sender.getName(), "soontm.staff")) {
            sender.sendMessage(new TextComponent(ChatColor.YELLOW + "-------------- Online staff --------------"));
            for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                if (!BungeePerms.getInstance().getPermissionsChecker().hasPerm(player.getName(), "soontm.staff")) return;

                sender.sendMessage(new TextComponent(player.getDisplayName() + ChatColor.YELLOW + " - " + player.getServer().getInfo().getName()));
            }
            return;
        }
    }
}
