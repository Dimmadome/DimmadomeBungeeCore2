package com.github.kraftykaleb.commands;

import net.alpenblock.bungeeperms.BungeePerms;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 * Created by Kraft on 4/19/2017.
 */
public class RankCommand extends Command {
    public RankCommand() {
        super("rank", "soontm.master");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            if (!BungeePerms.getInstance().getPermissionsChecker().hasPerm(sender.getName(), "soontm.master")) {
                sender.sendMessage(new TextComponent(ChatColor.RED + "You do not have permission!"));
                return;
            }
        }
        if (args.length < 1) {

            sender.sendMessage(new TextComponent(ChatColor.RED + "/rank help for more information"));
            return;
        }

        if (args[0].equals("help")) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "Subcommands: help, set, add, remove"));
            return;
        }

        if (args[0].equals("set")) {
            if (args.length < 3) {
                sender.sendMessage(new TextComponent(ChatColor.RED + "/rank set <name> <rank>"));
                return;
            }

            if (ProxyServer.getInstance().getPlayer(args[1]) == null) {
                sender.sendMessage(new TextComponent(ChatColor.RED + "That player was not found!"));
                return;
            }

            ProxiedPlayer t = ProxyServer.getInstance().getPlayer(args[1]);

            if (args[2].equals("officer")) {
                BungeePerms.getInstance().getPermissionsManager().addUserPerm(BungeePerms.getInstance().getPermissionsManager().getUser(t.getName()), "soontm.staff");
                BungeePerms.getInstance().getPermissionsManager().removeUserPerm(BungeePerms.getInstance().getPermissionsManager().getUser(t.getName()), "soontm.master");

                //TODO Add Officer perms
                t.sendMessage(new TextComponent(ChatColor.GREEN + "Successfully updated " + t.getDisplayName() + "'s" + ChatColor.GREEN + " permissions to be in group" + args[2].toUpperCase()));
                for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
                    if (BungeePerms.getInstance().getPermissionsChecker().hasPerm(p.getName(), "soontm.staff")) {
                        p.sendMessage(new TextComponent(ChatColor.DARK_GREEN + "[STAFF] " + ChatColor.WHITE + sender.getName() + " set " + t.getDisplayName() + ChatColor.WHITE + "'s rank to " + args[2]));
                    }
                }
                return;
            }

            if (args[2].equals("master")) {
                BungeePerms.getInstance().getPermissionsManager().addUserPerm(BungeePerms.getInstance().getPermissionsManager().getUser(t.getName()), "soontm.staff");
                BungeePerms.getInstance().getPermissionsManager().addUserPerm(BungeePerms.getInstance().getPermissionsManager().getUser(t.getName()), "soontm.master");
                //TODO Add master perms
                t.sendMessage(new TextComponent(ChatColor.GREEN + "Successfully updated " + t.getDisplayName() + "'s" + ChatColor.GREEN + " permissions to be in group" + args[2].toUpperCase()));
                for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
                    if (BungeePerms.getInstance().getPermissionsChecker().hasPerm(p.getName(), "soontm.staff")) {
                        p.sendMessage(new TextComponent(ChatColor.DARK_GREEN + "[STAFF] " + ChatColor.WHITE + sender.getName() + " set " + t.getDisplayName() + ChatColor.WHITE + "'s rank to " + args[2]));
                    }
                }
                return;
            }

            if (args[2].equals("member")) {
                BungeePerms.getInstance().getPermissionsManager().addUserPerm(BungeePerms.getInstance().getPermissionsManager().getUser(t.getName()), "soontmcore.default");
                BungeePerms.getInstance().getPermissionsManager().removeUserPerm(BungeePerms.getInstance().getPermissionsManager().getUser(t.getName()), "soontm.master");
                BungeePerms.getInstance().getPermissionsManager().removeUserPerm(BungeePerms.getInstance().getPermissionsManager().getUser(t.getName()), "soontm.staff");
                //TODO Add default perms
                t.sendMessage(new TextComponent(ChatColor.GREEN + "Successfully updated " + t.getDisplayName() + "'s" + ChatColor.GREEN + " permissions to be in group" + args[2].toUpperCase()));
                for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
                    if (BungeePerms.getInstance().getPermissionsChecker().hasPerm(p.getName(), "soontm.staff")) {
                        p.sendMessage(new TextComponent(ChatColor.DARK_GREEN + "[STAFF] " + ChatColor.WHITE + sender.getName() + " set " + t.getDisplayName() + ChatColor.WHITE + "'s rank to " + args[2]));
                    }
                }
                return;
            }
        }
        if (args[0].equals("add")) {
            if (args.length < 3) {
                sender.sendMessage(new TextComponent(ChatColor.RED + "/rank add <name> <permission>"));
                return;
            }

            if (ProxyServer.getInstance().getPlayer(args[1]) == null) {
                sender.sendMessage(new TextComponent(ChatColor.RED + "That player was not found!"));
                return;
            }

            ProxiedPlayer t = ProxyServer.getInstance().getPlayer(args[1]);
            t.setPermission(args[2], true);

            t.sendMessage(new TextComponent(ChatColor.GREEN + "Successfully updated " + t.getDisplayName() + "'s" + ChatColor.GREEN + " permissions to have to have" + args[2].toUpperCase()));
            return;
        }
        if (args[0].equals("add")) {
            if (args.length < 3) {
                sender.sendMessage(new TextComponent(ChatColor.RED + "/rank remove <name> <permission>"));
                return;
            }

            if (ProxyServer.getInstance().getPlayer(args[1]) == null) {
                sender.sendMessage(new TextComponent(ChatColor.RED + "That player was not found!"));
                return;
            }

            ProxiedPlayer t = ProxyServer.getInstance().getPlayer(args[1]);
            t.setPermission(args[2], true);
            t.sendMessage(new TextComponent(ChatColor.GREEN + "Successfully updated " + t.getDisplayName() + "'s" + ChatColor.GREEN + " permissions to have to have" + args[2].toUpperCase()));
            return;
        }
    }
}
