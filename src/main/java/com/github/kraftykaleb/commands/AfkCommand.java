package com.github.kraftykaleb.commands;

import com.github.kraftykaleb.BungeeCore;
import net.alpenblock.bungeeperms.BungeePerms;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 * Created by Kraft on 9/11/2017.
 */
public class AfkCommand extends Command {

    private BungeeCore plugin;
    public AfkCommand(BungeeCore instance) {
        super("afk");
        plugin = instance;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        // /afk
        if(BungeePerms.getInstance().getPermissionsChecker().hasPerm(sender.getName(), "soontm.staff")) {
            if (sender instanceof ProxiedPlayer) {
                ProxiedPlayer p = (ProxiedPlayer) sender;
                if (args.length == 1) {
                    if (ProxyServer.getInstance().getPlayer(args[0]) != null) {
                        ServerInfo afkServer = ProxyServer.getInstance().getServerInfo("neverland");
                        ProxyServer.getInstance().getPlayer(args[0]).connect(afkServer);

                        for (ProxiedPlayer player1 : ProxyServer.getInstance().getPlayers()) {
                            if (BungeePerms.getInstance().getPermissionsManager().getUser(player1.getName()).hasPerm("soontm.staff")) {
                                player1.sendMessage(new TextComponent(ChatColor.DARK_GREEN + "[STAFF] " + ChatColor.WHITE + p.getName() + " afk'd " + args[0]));
                            }
                        }
                    } else {
                        sender.sendMessage(new TextComponent(ChatColor.RED + "I could not find that player, they are probably offline!"));
                    }
                }
            }
        } else {
            sender.sendMessage(new TextComponent(ChatColor.RED + "You do not have permission"));
        }
    }
}
