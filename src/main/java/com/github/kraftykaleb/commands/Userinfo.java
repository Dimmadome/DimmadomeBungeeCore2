package com.github.kraftykaleb.commands;

import com.github.kraftykaleb.Main;
import com.github.kraftykaleb.objects.Ban;
import net.alpenblock.bungeeperms.BungeePerms;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.protocol.packet.Chat;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Kraft on 5/31/2017.
 */
public class Userinfo extends Command{

    private Main plugin;

    public Userinfo(String name, Main instance) {
        super("userinfo");
        plugin = instance;
    }

    private String rank;
    private String guild;
    private boolean isBanned;
    private boolean isMuted;
    private int bans;
    private int mutes;
    private int kicks;
    private String bReason;
    private String bRemaining;
    private String mReason;
    private String mRemaining;

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (BungeePerms.getInstance().getPermissionsChecker().hasPerm(sender.getName(), "soontm.staff")) {
            if (args.length == 1) {
                if (plugin.playerDataContainsPlayer(args[0])) {
                    sender.sendMessage("-------------- Userinfo for " + args[0] +" --------------");
                    try {
                        PreparedStatement nameStatement = plugin.connection.prepareStatement("SELECT rank FROM `player_data` WHERE player_name=?;");
                        nameStatement.setString(1, args[0]);

                        ResultSet nameSet = nameStatement.executeQuery();
                        nameSet.next();
                        rank = nameSet.getString("rank");
                        sender.sendMessage(ChatColor.GOLD + "Rank: " + ChatColor.WHITE + rank);
                        nameStatement.close();
                        nameSet.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                        sender.sendMessage(ChatColor.GOLD + "Rank: " + ChatColor.RED +"NOT FOUND");
                    }

                    try {
                        PreparedStatement guildStatement = plugin.connection.prepareStatement("SELECT guild FROM `player_data` WHERE player_name=?;");
                        guildStatement.setString(1, args[0]);

                        ResultSet guildSet = guildStatement.executeQuery();
                        guildSet.next();
                        guild = guildSet.getString("guild");
                        sender.sendMessage(ChatColor.GOLD + "Guild: "+ ChatColor.WHITE  + guild);
                        guildStatement.close();
                        guildSet.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                        sender.sendMessage(ChatColor.GOLD + "Guild: " + ChatColor.RED +"NOT FOUND");
                    }

                    try {
                        PreparedStatement banStatement = plugin.connection.prepareStatement("SELECT banned FROM `player_data` WHERE player_name=?;");
                        banStatement.setString(1, args[0]);

                        ResultSet banSet = banStatement.executeQuery();
                        banSet.next();
                        isBanned = banSet.getBoolean("banned");
                        banStatement.close();
                        banSet.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        PreparedStatement mutedStatement = plugin.connection.prepareStatement("SELECT muted FROM `player_data` WHERE player_name=?;");
                        mutedStatement.setString(1, args[0]);

                        ResultSet mutedSet = mutedStatement.executeQuery();
                        mutedSet.next();
                        isMuted = mutedSet.getBoolean("muted");
                        mutedStatement.close();
                        mutedSet.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        PreparedStatement mutesStatement = plugin.connection.prepareStatement("SELECT mutes FROM `player_data` WHERE player_name=?;");
                        mutesStatement.setString(1, args[0]);

                        ResultSet mutesSet = mutesStatement.executeQuery();
                        mutesSet.next();
                        mutes = mutesSet.getInt("mutes");
                        mutesStatement.close();
                        mutesSet.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        PreparedStatement bansStatement = plugin.connection.prepareStatement("SELECT bans FROM `player_data` WHERE player_name=?;");
                        bansStatement.setString(1, args[0]);

                        ResultSet bansSet = bansStatement.executeQuery();
                        bansSet.next();
                        bans = bansSet.getInt("bans");
                        bansStatement.close();
                        bansSet.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        PreparedStatement kicksStatement = plugin.connection.prepareStatement("SELECT kicks FROM `player_data` WHERE player_name=?;");
                        kicksStatement.setString(1, args[0]);

                        ResultSet kicksSet = kicksStatement.executeQuery();
                        kicksSet.next();
                        kicks = kicksSet.getInt("kicks");
                        kicksStatement.close();
                        kicksSet.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (isMuted && isBanned) {

                        try {
                            PreparedStatement reasonStatement = plugin.connection.prepareStatement("SELECT * FROM `ban_table` WHERE player_name=?;");
                            reasonStatement.setString(1, args[0]);

                            ResultSet result = reasonStatement.executeQuery();
                            result.next();
                            bReason = result.getString("reason");
                            String dateAsString = result.getString("length");
                            Date dt = new Date();
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String currentTime = sdf.format(dt);

                            Date d1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateAsString);
                            Date d2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(currentTime);

                            long diff = Math.abs((d2.getTime() - d1.getTime()) / 1000);
                            bRemaining = Ban.calculateTime(diff);
                            reasonStatement.close();
                            result.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            PreparedStatement muteReasonStatement = plugin.connection.prepareStatement("SELECT * FROM `mute_table` WHERE player_name=?;");
                            muteReasonStatement.setString(1, args[0]);

                            ResultSet muteResult = muteReasonStatement.executeQuery();
                            muteResult.next();
                            mReason = muteResult.getString("reason");
                            String dateAsString = muteResult.getString("length");
                            Date dt = new Date();
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String currentTime = sdf.format(dt);

                            Date d1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateAsString);
                            Date d2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(currentTime);

                            long diff = Math.abs((d2.getTime() - d1.getTime()) / 1000);
                            mRemaining = Ban.calculateTime(diff);

                            muteReasonStatement.close();
                            muteResult.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        TextComponent punishments = new TextComponent(ChatColor.GOLD + "Punishments:");

                        TextComponent banreason = new TextComponent(ChatColor.RED + " " + ChatColor.BOLD + "Bans: " + ChatColor.WHITE + bans);
                        banreason.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Reason: " + bReason + "\nRemaining: " + bRemaining).create()));


                        TextComponent mutereason = new TextComponent(ChatColor.RED + " " + ChatColor.BOLD + "Mutes: " + ChatColor.WHITE + mutes);
                        mutereason.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Reason: " + bReason + "\nRemaining: " + bRemaining).create()));

                        TextComponent kicks2 = new TextComponent(ChatColor.GREEN + " " + ChatColor.BOLD + "Kicks " + ChatColor.WHITE + kicks);

                        punishments.addExtra(banreason);
                        punishments.addExtra(mutereason);
                        punishments.addExtra(kicks2);

                        sender.sendMessage(punishments);
                    } else if (isMuted) {
                        try {
                            PreparedStatement muteReasonStatement = plugin.connection.prepareStatement("SELECT * FROM `mute_table` WHERE player_name=?;");
                            muteReasonStatement.setString(1, args[0]);

                            ResultSet muteResult = muteReasonStatement.executeQuery();
                            muteResult.next();
                            mReason = muteResult.getString("reason");
                            String dateAsString = muteResult.getString("length");
                            Date dt = new Date();
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String currentTime = sdf.format(dt);

                            Date d1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateAsString);
                            Date d2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(currentTime);

                            long diff = Math.abs((d2.getTime() - d1.getTime()) / 1000);
                            mRemaining = Ban.calculateTime(diff);
                            muteReasonStatement.close();
                            muteResult.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        TextComponent punishments = new TextComponent(ChatColor.GOLD + "Punishments:");

                        TextComponent banreason = new TextComponent(ChatColor.GREEN + " " + ChatColor.BOLD + "Bans: " + ChatColor.WHITE + bans);
                        //banreason.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Reason: " + bReason + "\nRemaining: " + bRemaining).create()));


                        TextComponent mutereason = new TextComponent(ChatColor.RED + " " + ChatColor.BOLD + "Mutes: " + ChatColor.WHITE + mutes);
                        mutereason.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Reason: " + bReason + "\nRemaining: " + bRemaining).create()));

                        TextComponent kicks2 = new TextComponent(ChatColor.GREEN + " " + ChatColor.BOLD + "Kicks " + ChatColor.WHITE + kicks);


                        punishments.addExtra(banreason);
                        punishments.addExtra(mutereason);
                        punishments.addExtra(kicks2);

                        sender.sendMessage(punishments);
                    } else if (isBanned) {
                        try {
                            PreparedStatement reasonStatement = plugin.connection.prepareStatement("SELECT * FROM `ban_table` WHERE player_name=?;");
                            reasonStatement.setString(1, args[0]);

                            ResultSet result = reasonStatement.executeQuery();
                            result.next();
                            bReason = result.getString("reason");
                            String dateAsString = result.getString("length");
                            Date dt = new Date();
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String currentTime = sdf.format(dt);

                            Date d1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateAsString);
                            Date d2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(currentTime);

                            long diff = Math.abs((d2.getTime() - d1.getTime()) / 1000);
                            bRemaining = Ban.calculateTime(diff);
                            reasonStatement.close();
                            result.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        TextComponent punishments = new TextComponent(ChatColor.GOLD + "Punishments:");

                        TextComponent banreason = new TextComponent(ChatColor.RED + " " + ChatColor.BOLD + "Bans: " + ChatColor.WHITE + bans);
                        banreason.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Reason: " + bReason + "\nRemaining: " + bRemaining).create()));


                        TextComponent mutereason = new TextComponent(ChatColor.GREEN + " " + ChatColor.BOLD + "Mutes: " + ChatColor.WHITE + mutes);
                        //mutereason.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Reason: " + bReason + "\nRemaining: " + bRemaining).create()));

                        TextComponent kicks2 = new TextComponent(ChatColor.GREEN + " " + ChatColor.BOLD + "Kicks " + ChatColor.WHITE + kicks);

                        punishments.addExtra(banreason);
                        punishments.addExtra(mutereason);
                        punishments.addExtra(kicks2);

                        sender.sendMessage(punishments);
                    } else {
                        TextComponent punishments = new TextComponent(ChatColor.GOLD + "Punishments:");

                        TextComponent banreason = new TextComponent(ChatColor.GREEN + " " + ChatColor.BOLD + "Bans: " + ChatColor.WHITE + bans);
                        //banreason.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Reason: " + bReason + "\nRemaining: " + bRemaining).create()));


                        TextComponent mutereason = new TextComponent(ChatColor.GREEN + " " + ChatColor.BOLD + "Mutes: " + ChatColor.WHITE + mutes);
                        //mutereason.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Reason: " + bReason + "\nRemaining: " + bRemaining).create()));

                        TextComponent kicks2 = new TextComponent(ChatColor.GREEN + " " + ChatColor.BOLD + "Kicks " + ChatColor.WHITE + kicks);

                        punishments.addExtra(banreason);
                        punishments.addExtra(mutereason);
                        punishments.addExtra(kicks2);

                        sender.sendMessage(punishments);
                    }

                } else {
                    sender.sendMessage(new TextComponent(ChatColor.RED + "I could not find: " + args[0]));
                }
            }
        }
    }
}
