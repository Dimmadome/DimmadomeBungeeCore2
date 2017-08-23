package com.github.kraftykaleb.commands;

import com.github.kraftykaleb.Main;
import com.github.kraftykaleb.listeners.DiscordBot;
import net.alpenblock.bungeeperms.BungeePerms;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.io.IOException;

/**
 * Created by Kraft on 4/20/2017.
 */
public class Sc extends Command {
    private Main plugin;
    public Sc(Main instance, String name) {
        super("sc");
        plugin = instance;
    }


    @Override
    public void execute(CommandSender sender, String[] args) {


        if (BungeePerms.getInstance().getPermissionsChecker().hasPerm(sender.getName(), "soontm.staff")) {
            if (sender instanceof ProxiedPlayer) {
                if (args.length == 0) {
                    sender.sendMessage(new TextComponent(ChatColor.RED + "You need a message, silly!"));
                    return;
                } else if (args.length == 1) {
                    plugin.sendStaffMessage(((ProxiedPlayer) sender).getDisplayName() + ChatColor.WHITE + ": " + args[0]);
                    plugin.discordBot.sendDiscordMessage(sender.getName(), args[0]);
                    //plugin.session.sendMessage(channel, (sender.getName() + ": " + args[0]));
                    return;
                }
                StringBuilder builder = new StringBuilder();
                for(int i = 0; i < args.length; i++) {
                    builder.append(args[i] + " ");
                }
                String msg = builder.toString();
                plugin.sendStaffMessage(((ProxiedPlayer) sender).getDisplayName() + ChatColor.WHITE + ": " + msg);
                plugin.discordBot.sendDiscordMessage(sender.getName(), msg);
                //plugin.session.sendMessage(channel, (sender.getName() + ": " + msg));
                return;
            }
        }
    }
}
