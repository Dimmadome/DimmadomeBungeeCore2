package com.github.kraftykaleb.commands;

import com.github.kraftykaleb.Main;
import net.alpenblock.bungeeperms.BungeePerms;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.plugin.Command;

import java.sql.PreparedStatement;

/**
 * Created by Kraft on 9/18/2017.
 */
public class StatWipe extends Command {

    private Main plugin;

    public StatWipe(String name, Main instance) {
        super(name);
        plugin = instance;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (BungeePerms.getInstance().getPermissionsChecker().hasPerm(sender.getName(), "soontm.staff")) {
            if (args.length == 1) {
                if (plugin.playerDataContainsPlayer(args[0])) {
                    try {
                        PreparedStatement removeBanStatement = plugin.connection.prepareStatement("DELETE FROM `player_data` WHERE player_name=?;");
                        removeBanStatement.setString(1, args[0]);
                        removeBanStatement.execute();
                        removeBanStatement.close();

                        sender.sendMessage(ChatColor.GREEN + "You just statwiped: " + args[0]);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "I could not find: " + args[0]);
                }
            }
        }
    }
}
