package com.github.kraftykaleb.commands;

import com.github.kraftykaleb.BungeeCore;
import net.alpenblock.bungeeperms.BungeePerms;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Created by Kraft on 9/15/2017.
 */
public class UnmuteCommand extends Command{

    private BungeeCore plugin;

    public UnmuteCommand(BungeeCore instance) {
        super("unmute");
        plugin = instance;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (BungeePerms.getInstance().getPermissionsChecker().hasPerm(sender.getName(), "soontm.staff")) {
            if (args.length < 2) {
                sender.sendMessage(new TextComponent(ChatColor.RED + "/unmute <player> <reason>"));
                return;
            }

            StringBuilder msgBuilder = new StringBuilder();

            for (int i = 1; i < args.length; i++) {
                msgBuilder.append(args[i]).append(" ");
            }

            String msg = msgBuilder.toString().trim();

            if (plugin.playerDataContainsPlayer(args[0])) {
                try {
                    PreparedStatement sql = plugin.connection.prepareStatement("SELECT player FROM `player_data` WHERE player_name=?;");
                    sql.setString(1, args[0]);

                    ResultSet resultSet = sql.executeQuery();
                    resultSet.next();
                    String bannedUUID = (resultSet.getString("player"));
                    resultSet.close();
                    sql.close();

                    PreparedStatement checkstatement = plugin.connection.prepareStatement("SELECT length FROM `mute_table` WHERE uuid=?;");
                    checkstatement.setString(1, bannedUUID);

                    ResultSet checkset = checkstatement.executeQuery();
                    checkset.next();
                    if (checkset.getString("length") != null) {
                        PreparedStatement removeBanStatement = plugin.connection.prepareStatement("DELETE FROM `mute_table` WHERE uuid=?;");
                        removeBanStatement.setString(1, bannedUUID);
                        removeBanStatement.execute();
                        removeBanStatement.close();

                        plugin.sendStaffMessage(sender.getName() + " unmuted " + args[0] + ". Reason:" + msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
