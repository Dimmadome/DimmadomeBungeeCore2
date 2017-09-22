package com.github.kraftykaleb.commands;

import com.github.kraftykaleb.Main;
import com.github.kraftykaleb.objects.Ban;
import net.alpenblock.bungeeperms.BungeePerms;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.plugin.Command;

/**
 * Created by Kraft on 5/31/2017.
 */
public class BanCommand extends Command {
    private Main plugin;
    public BanCommand(String name, Main instance) {
        super("ban");
        plugin = instance;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (BungeePerms.getInstance().getPermissionsChecker().hasPerm(sender.getName(), "soontm.staff")) {
            if (args.length < 3) {
                sender.sendMessage(new TextComponent(ChatColor.RED + "/ban <player> <length> <reason>"));
                return;
            }

            StringBuilder msgBuilder = new StringBuilder();

            for (int i = 2; i < args.length; i++) {
                msgBuilder.append(args[i]).append(" ");
            }

            String msg = msgBuilder.toString().trim();

            Integer seconds;

            if (args[1].toLowerCase().equals("perm")) {
                new Ban(plugin, args[0], sender, true, msg);

                if (ProxyServer.getInstance().getPlayer(args[0]) != null) {
                    ProxiedPlayer player = ProxyServer.getInstance().getPlayer(args[0]);

                    player.disconnect(new TextComponent(ChatColor.RED + "You have been permanently banned from this server!" + ChatColor.GRAY + "Reason: " + ChatColor.WHITE + msg));
                    for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
                        if (p.getServer().getInfo().getName().equals(player.getServer().getInfo().getName())) {
                            p.sendMessage(new TextComponent(ChatColor.WHITE + "[REPORT] " + ChatColor.RED + "" + ChatColor.BOLD + " A user was removed from your server for     §chacking or abuse. Thank you for reporting it!".replace(" ", " §c")));
                        }
                    }
                }

            } else if (args[1].toLowerCase().endsWith("s")) {
                seconds = Integer.parseInt(args[1].replace("s", ""));
                new Ban(plugin, args[0], sender, seconds, msg);

                if (ProxyServer.getInstance().getPlayer(args[0]) != null) {
                    ProxiedPlayer player = ProxyServer.getInstance().getPlayer(args[0]);

                    player.disconnect(new TextComponent(ChatColor.RED + "You have been temporarily banned for " + ChatColor.WHITE + args[1] + ChatColor.RED + " from this server!\n \n" + ChatColor.GRAY + "Reason: " + ChatColor.WHITE + msg));
                    for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
                        if (p.getServer().getInfo().getName().equals(player.getServer().getInfo().getName())) {
                            p.sendMessage(new TextComponent(ChatColor.WHITE + "[REPORT] " + ChatColor.RED + "" + ChatColor.BOLD + " A user was removed from your server for     §chacking or abuse. Thank you for reporting it!".replace(" ", " §c")));
                        }
                    }
                }

            } else if (args[1].toLowerCase().endsWith("m")) {
                seconds = Integer.parseInt(args[1].replace("s", ""));
                new Ban(plugin, args[0], sender, seconds, msg);

                if (ProxyServer.getInstance().getPlayer(args[0]) != null) {
                    ProxiedPlayer player = ProxyServer.getInstance().getPlayer(args[0]);

                    player.disconnect(new TextComponent(ChatColor.RED + "You have been temporarily banned for " + ChatColor.WHITE + args[1] + " 0s" + ChatColor.RED + " from this server!\n \n" + ChatColor.GRAY + "Reason: " + ChatColor.WHITE + msg));
                    for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
                        if (p.getServer().getInfo().getName().equals(player.getServer().getInfo().getName())) {
                            p.sendMessage(new TextComponent(ChatColor.WHITE + "[REPORT] " + ChatColor.RED + "" + ChatColor.BOLD + " A user was removed from your server for     §chacking or abuse. Thank you for reporting it!".replace(" ", " §c")));
                        }
                    }
                }
            } else if (args[1].toLowerCase().endsWith("h")) {
                seconds = Integer.parseInt(args[1].replace("s", ""));
                new Ban(plugin, args[0], sender, seconds, msg);

                if (ProxyServer.getInstance().getPlayer(args[0]) != null) {
                    ProxiedPlayer player = ProxyServer.getInstance().getPlayer(args[0]);

                    player.disconnect(new TextComponent(ChatColor.RED + "You have been temporarily banned for " + ChatColor.WHITE + args[1] + " 0m 0s" + ChatColor.RED + " from this server!\n \n" + ChatColor.GRAY + "Reason: " + ChatColor.WHITE + msg));
                    for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
                        if (p.getServer().getInfo().getName().equals(player.getServer().getInfo().getName())) {
                            p.sendMessage(new TextComponent(ChatColor.WHITE + "[REPORT] " + ChatColor.RED + "" + ChatColor.BOLD + " A user was removed from your server for     §chacking or abuse. Thank you for reporting it!".replace(" ", " §c")));
                        }
                    }
                }
            } else if (args[1].toLowerCase().endsWith("d")) {
                seconds = Integer.parseInt(args[1].replace("d", ""));
                new Ban(plugin, args[0], sender, seconds, msg);

                if (ProxyServer.getInstance().getPlayer(args[0]) != null) {
                    ProxiedPlayer player = ProxyServer.getInstance().getPlayer(args[0]);

                    player.disconnect(new TextComponent(ChatColor.RED + "You have been temporarily banned for " + ChatColor.WHITE + args[1] + " 0h 0m 0s" + ChatColor.RED + " from this server!\n \n" + ChatColor.GRAY + "Reason: " + ChatColor.WHITE + msg));
                    for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
                        if (p.getServer().getInfo().getName().equals(player.getServer().getInfo().getName())) {
                            p.sendMessage(new TextComponent(ChatColor.WHITE + "[REPORT] " + ChatColor.RED + "" + ChatColor.BOLD + " A user was removed from your server for     §chacking or abuse. Thank you for reporting it!".replace(" ", " §c")));
                        }
                    }
                }
            }
        }
    }
}
