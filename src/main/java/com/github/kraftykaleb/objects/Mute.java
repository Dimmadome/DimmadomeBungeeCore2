package com.github.kraftykaleb.objects;

import com.github.kraftykaleb.BungeeCore;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by Kraft on 5/31/2017.
 */
public class Mute {

    private BungeeCore plugin;

    public Mute (BungeeCore instance, String mutedName, CommandSender issuer, int durationInSeconds, String reason) {
        plugin = instance;

        String mutedUUID = null;
        String issuedTime;
        String expirationTime;

        Date dt = new Date();
        String time = calculateTime(durationInSeconds);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        issuedTime = sdf.format(dt);

        plugin.sendStaffMessage(issuer.getName() + " muted " + mutedName + " for " + time + ". Reason: " + reason);

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.SECOND, durationInSeconds);
        dt = cal.getTime();
        expirationTime = sdf.format(dt);

        if (ProxyServer.getInstance().getPlayer(mutedName) == null) {
            if (plugin.playerDataContainsPlayer(mutedName)) {
                try {
                    PreparedStatement sql = plugin.connection.prepareStatement("SELECT player FROM `player_data` WHERE player_name=?;");
                    sql.setString(1, mutedName);

                    ResultSet resultSet = sql.executeQuery();
                    resultSet.next();
                    mutedUUID = (resultSet.getString("player"));
                    resultSet.close();
                    sql.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                issuer.sendMessage(new TextComponent(ChatColor.WHITE + "[MUTES] " + ChatColor.RED + mutedName + " has never joined the server!"));
                return;
            }
        } else {
            mutedUUID = ProxyServer.getInstance().getPlayer(mutedName).getUniqueId().toString();
        }
        try {
            PreparedStatement getBans = plugin.connection.prepareStatement("SELECT mutes FROM `player_data` WHERE player=?");
            getBans.setString(1, mutedUUID);

            ResultSet bansResult = getBans.executeQuery();
            bansResult.next();
            int bans = bansResult.getInt("bans");

            PreparedStatement updateBans = plugin.connection.prepareStatement("UPDATE `player_data` SET mutes=? WHERE player=?;");
            updateBans.setInt(1, bans+1);
            updateBans.setString(2, mutedUUID);
            updateBans.executeUpdate();

            PreparedStatement newPlayer = plugin.connection.prepareStatement("INSERT INTO `mute_table` values(?,?,?,0,?,?,?);");
            newPlayer.setString(1, mutedUUID);
            newPlayer.setString(2, mutedName);
            newPlayer.setString(3, expirationTime);
            newPlayer.setString(4, reason);
            newPlayer.setString(5, issuedTime);
            newPlayer.setString(6, issuer.getName());
            newPlayer.execute();

            bansResult.close();
            getBans.close();
            updateBans.close();
            newPlayer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public Mute (BungeeCore instance, String mutedName, CommandSender issuer, boolean isPerm, String reason) {
        plugin = instance;
        String mutedUUID = null;
        String issuedTime;
        String expirationTime;

        Date dt = new Date();

        plugin.sendStaffMessage(issuer.getName() + " permanently muted " + mutedName + ". Reason: " + reason);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        issuedTime = sdf.format(dt);

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.YEAR, 99);
        dt = cal.getTime();
        expirationTime = sdf.format(dt);

        if (ProxyServer.getInstance().getPlayer(mutedName) == null) {
            if (plugin.playerDataContainsPlayer(mutedName)) {
                try {
                    PreparedStatement sql = plugin.connection.prepareStatement("SELECT player FROM `player_data` WHERE player_name=?;");
                    sql.setString(1, mutedName);

                    ResultSet resultSet = sql.executeQuery();
                    resultSet.next();
                    mutedUUID = (resultSet.getString("player"));
                    resultSet.close();
                    sql.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                issuer.sendMessage(new TextComponent(ChatColor.WHITE + "[MUTES] " + ChatColor.RED + mutedName + " has never joined the server!"));
                return;
            }
        } else {
            mutedUUID = ProxyServer.getInstance().getPlayer(mutedName).getUniqueId().toString();
        }
        try {
            PreparedStatement getBans = plugin.connection.prepareStatement("SELECT mutes FROM `player_data` WHERE player=?");
            getBans.setString(1, mutedUUID);

            ResultSet bansResult = getBans.executeQuery();
            bansResult.next();
            int bans = bansResult.getInt("bans");

            PreparedStatement updateBans = plugin.connection.prepareStatement("UPDATE `player_data` SET mutes=? WHERE player=?;");
            updateBans.setInt(1, bans+1);
            updateBans.setString(2, mutedUUID);
            updateBans.executeUpdate();

            PreparedStatement newPlayer = plugin.connection.prepareStatement("INSERT INTO `mute_table` values(?,?,?,0,?,?,?);");
            newPlayer.setString(1, mutedUUID);
            newPlayer.setString(2, mutedName);
            newPlayer.setString(3, expirationTime);
            newPlayer.setString(4, reason);
            newPlayer.setString(5, issuedTime);
            newPlayer.setString(6, issuer.getName());
            newPlayer.execute();

            bansResult.close();
            getBans.close();
            updateBans.close();
            newPlayer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String calculateTime(long seconds) {
        int day = (int) TimeUnit.SECONDS.toDays(seconds);
        long hours = TimeUnit.SECONDS.toHours(seconds) -
                TimeUnit.DAYS.toHours(day);
        long minute = TimeUnit.SECONDS.toMinutes(seconds) -
                TimeUnit.DAYS.toMinutes(day) -
                TimeUnit.HOURS.toMinutes(hours);
        long second = TimeUnit.SECONDS.toSeconds(seconds) -
                TimeUnit.DAYS.toSeconds(day) -
                TimeUnit.HOURS.toSeconds(hours) -
                TimeUnit.MINUTES.toSeconds(minute);

        return (day + "d" + hours + "h" + minute + "m" + second + "s");

    }
}
