package com.github.kraftykaleb.listeners;

import com.github.kraftykaleb.Main;
import net.alpenblock.bungeeperms.BungeePerms;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * Created by Kraft on 4/19/2017.
 */
public class onJoin implements Listener {

    private Main plugin;
    public onJoin(Main ins) { plugin = ins; }
    private ProxiedPlayer p;
    private ProxiedPlayer pl;


    @EventHandler
    public void onJoin(PostLoginEvent e) {
        plugin.openConnection();
        p = e.getPlayer();
        plugin.findHypixelPlayer(p);
        plugin.findHypixelGuild(p);

        try {
            if (plugin.playerDataContainsPlayer(e.getPlayer())) {
                PreparedStatement sql = plugin.connection.prepareStatement("SELECT banned FROM `player_data` WHERE player=?;");
                sql.setString(1, e.getPlayer().getUniqueId().toString());

                ResultSet resultSet = sql.executeQuery();
                resultSet.next();
                if (resultSet.getBoolean("banned")) {
                    BungeePerms.getInstance().getPermissionsManager().deleteUser(BungeePerms.getInstance().getPermissionsManager().getUser(p.getName()));
                    p.disconnect(new TextComponent("You are permanently banned from the server for\n" + ChatColor.RED + /*REASON HERE*/"Banned from guild [A]"));
                    return;
                }
            } else {

                ProxyServer.getInstance().getScheduler().schedule(plugin, new Runnable() {
                    @Override
                    public void run() {
                        try {
                            long time = System.currentTimeMillis();
                            PreparedStatement newPlayer = plugin.connection.prepareStatement("INSERT INTO `player_data` values(?,?,?,0,1000,0," + time + ",?,?,0,0,0,0,0,?);");
                            newPlayer.setString(1, e.getPlayer().getUniqueId().toString());
                            newPlayer.setString(2, e.getPlayer().getName());
                            if (plugin.assignedguilds.containsKey(p.getName())) {
                                if (plugin.assignedguilds.get(p.getName()) != null) {
                                    newPlayer.setString(3, plugin.assignedguilds.get(p.getName()));
                                    newPlayer.setString(4, plugin.hypixelranks.get(p.getName()));
                                    newPlayer.setString(5, plugin.guildranks.get(p.getName()));
                                } else {
                                    newPlayer.setString(3, "GUILDLESS");
                                    newPlayer.setString(4, plugin.hypixelranks.get(p.getName()));
                                    newPlayer.setString(5, "NORANK");
                                }
                            } else {

                                newPlayer.setString(3, "GUILDLESS");
                                newPlayer.setString(4, plugin.hypixelranks.get(p.getName()));
                                newPlayer.setString(5, "NORANK");
                            }
                            newPlayer.setString(6, "NONE");
                            newPlayer.execute();
                            newPlayer.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, 1, TimeUnit.SECONDS);
                ProxyServer.getInstance().broadcast(new TextComponent(ChatColor.RED + "[BOT] SoonTM: " + ChatColor.YELLOW + "Please welcome " + p.getDisplayName() + ChatColor.YELLOW + " to the server!"));
                ProxyServer.getInstance().getLogger().log(Level.INFO, "Created a new player on the database!");
            }
        } catch (Exception error) {
            error.printStackTrace();
        }

        if (plugin.config.getString(p.getUniqueId().toString() + ".ip") == null) {
            if (!(p.getAddress().getAddress().getHostAddress().toString().equals(plugin.config.getString(p.getUniqueId().toString() + ".ip")))) {
                if (BungeePerms.getInstance().getPermissionsChecker().hasPerm(p.getName(), "soontm.staff")) {
                    p.disconnect(new TextComponent(ChatColor.RED + "You have been permanently banned from this server. \nYour account might be compromised, since this is a staff account. \nPlease contact a guild admin to be unbanned."));
                    return;
                }
            }
        }

            //FIRST JOIN OF THE SERVER
        if (plugin.config.get(p.getUniqueId().toString() + ".ip") == null) {
            ArrayList<String> names = new ArrayList<>();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date date = new Date();
            //System.out.println(dateFormat.format(date));

            names.add(p.getName());

            plugin.config.set(p.getUniqueId().toString() + ".aliases", names);
            plugin.config.set(p.getUniqueId().toString() + ".ip", p.getAddress().getAddress().getHostAddress());
            plugin.config.set(p.getUniqueId().toString() + ".lastlogin", dateFormat.format(date));
            plugin.saveFile(plugin, plugin.config, "config.yml");
        }

        if (!(plugin.config.getList(p.getUniqueId() + ".aliases").contains(p.getName()))) {

            List<String> configList = (List<String>) plugin.config.getList(p.getUniqueId() + ".aliases");
            configList.add(p.getName());
            plugin.config.set(p.getUniqueId() + ".aliases", configList);
            plugin.saveFile(plugin, plugin.config, "config.yml");
        } else {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date date = new Date();
            plugin.findHypixelGuild(p);
            plugin.config.set(p.getUniqueId().toString() + ".ip", p.getAddress().getAddress().getHostAddress());
            plugin.config.set(p.getUniqueId().toString() + ".lastlogin", dateFormat.format(date));
            plugin.saveFile(plugin, plugin.config, "config.yml");
        }
        ProxyServer.getInstance().getScheduler().schedule(plugin, new Runnable() {
            public void run() {
                staffJoin(p);


                try {
                    if (plugin.playerDataContainsPlayer(e.getPlayer())) {
                        PreparedStatement sql = plugin.connection.prepareStatement("SELECT skyflag_wins FROM `player_data` WHERE player=?;");
                        sql.setString(1, e.getPlayer().getUniqueId().toString());

                        ResultSet resultSet = sql.executeQuery();
                        resultSet.next();
                        plugin.skyflagwins.put(p.getName(), resultSet.getInt("skyflag_wins"));

                        if (plugin.assignedguilds.containsKey(p.getName())) {
                            if (plugin.assignedguilds.get(p.getName()) != null) {
                                PreparedStatement rankUpdate = plugin.connection.prepareStatement("UPDATE `player_data` SET rank=? WHERE player=?;");
                                rankUpdate.setString(1, plugin.hypixelranks.get(p.getName()));
                                rankUpdate.setString(2, e.getPlayer().getUniqueId().toString());
                                rankUpdate.executeUpdate();

                                PreparedStatement guildUpdate = plugin.connection.prepareStatement("UPDATE `player_data` SET guild=? WHERE player=?;");
                                guildUpdate.setString(1, plugin.assignedguilds.get(p.getName()));
                                guildUpdate.setString(2, e.getPlayer().getUniqueId().toString());
                                guildUpdate.executeUpdate();

                                PreparedStatement guildRankUpdate = plugin.connection.prepareStatement("UPDATE `player_data` SET rank_guild=? WHERE player=?;");
                                guildRankUpdate.setString(1, plugin.guildranks.get(p.getName()));
                                guildRankUpdate.setString(2, e.getPlayer().getUniqueId().toString());
                                guildRankUpdate.executeUpdate();

                                rankUpdate.close();
                                guildUpdate.close();
                                guildRankUpdate.close();
                            } else {
                                PreparedStatement rankUpdate = plugin.connection.prepareStatement("UPDATE `player_data` SET rank=? WHERE player=?;");
                                rankUpdate.setString(1, plugin.hypixelranks.get(p.getName()));
                                rankUpdate.setString(2, e.getPlayer().getUniqueId().toString());
                                rankUpdate.executeUpdate();

                                PreparedStatement guildUpdate = plugin.connection.prepareStatement("UPDATE `player_data` SET guild=? WHERE player=?;");
                                guildUpdate.setString(1, "GUILDLESS");
                                guildUpdate.setString(2, e.getPlayer().getUniqueId().toString());
                                guildUpdate.executeUpdate();

                                PreparedStatement guildRankUpdate = plugin.connection.prepareStatement("UPDATE `player_data` SET rank_guild=? WHERE player=?;");
                                guildRankUpdate.setString(1, "NORANK");
                                guildRankUpdate.setString(2, e.getPlayer().getUniqueId().toString());
                                guildRankUpdate.executeUpdate();

                                rankUpdate.close();
                                guildUpdate.close();
                                guildRankUpdate.close();
                            }
                        }
                        sql.close();
                        resultSet.close();
                        ProxyServer.getInstance().getLogger().log(Level.INFO, p.getName() + " was found and now has " + plugin.skyflagwins.get(p.getName())+ " wins!");
                    } else {

                        long time = System.currentTimeMillis();
                        PreparedStatement newPlayer = plugin.connection.prepareStatement("INSERT INTO `player_data` values(?,?,?,0,1000,0,"+ time +",?,?,0,0,0,0,0,?);");
                        newPlayer.setString(1, e.getPlayer().getUniqueId().toString());
                        newPlayer.setString(2, e.getPlayer().getName());
                        if (plugin.assignedguilds.containsKey(p.getName())) {
                            if (plugin.assignedguilds.get(p.getName()) != null) {
                                newPlayer.setString(3, plugin.assignedguilds.get(p.getName()));
                                newPlayer.setString(4, plugin.hypixelranks.get(p.getName()));
                                newPlayer.setString(5, plugin.guildranks.get(p.getName()));
                            } else {
                                newPlayer.setString(3, "GUILDLESS");
                                newPlayer.setString(4, plugin.hypixelranks.get(p.getName()));
                                newPlayer.setString(5, "NORANK");
                            }
                        } else {

                            newPlayer.setString(3, "GUILDLESS");
                            newPlayer.setString(4, plugin.hypixelranks.get(p.getName()));
                            newPlayer.setString(5, "NORANK");
                        }
                        newPlayer.setString(6, "NONE");
                        newPlayer.execute();
                        newPlayer.close();
                        ProxyServer.getInstance().broadcast(new TextComponent(ChatColor.RED + "[BOT] SoonTM: " + ChatColor.YELLOW + "Please welcome " + p.getDisplayName() + ChatColor.YELLOW + " to the server!"));
                        ProxyServer.getInstance().getLogger().log(Level.INFO, "Created a new player on the database!");
                    }
                } catch (Exception error) {
                    error.printStackTrace();
                } finally {
                    plugin.closeConnection();
                }
            }
        }, 12, TimeUnit.MILLISECONDS);
    }

    @EventHandler
    public void onLeave(PlayerDisconnectEvent event) {
        pl = event.getPlayer();
        ProxyServer.getInstance().getScheduler().schedule(plugin, new Runnable() {
            public void run() {
                staffLeave(pl);
            }
        }, 1, TimeUnit.SECONDS);
    }

    public void staffJoin (ProxiedPlayer player) {
        if (BungeePerms.getInstance().getPermissionsManager().getUser(player.getName()).hasPerm("soontm.staff") || player.getDisplayName().contains("ADMIN") || player.getDisplayName().contains("MOD") || player.getDisplayName().contains("HELPER")) {
            //plugin.discordBot
            for (ProxiedPlayer player1 : ProxyServer.getInstance().getPlayers()) {
                if (BungeePerms.getInstance().getPermissionsManager().getUser(player1.getName()).hasPerm("soontm.staff")) {
                    player1.sendMessage(new TextComponent(ChatColor.DARK_GREEN + "[STAFF] " + player.getDisplayName() + ChatColor.YELLOW + " joined."));
                }
            }
        }
    }

    public void staffLeave (ProxiedPlayer player) {
        if (BungeePerms.getInstance().getPermissionsManager().getUser(player.getName()).hasPerm("soontm.staff") || player.getDisplayName().contains("ADMIN") || player.getDisplayName().contains("MOD") || player.getDisplayName().contains("HELPER")) {
            for (ProxiedPlayer player1 : ProxyServer.getInstance().getPlayers()) {
                if (BungeePerms.getInstance().getPermissionsManager().getUser(player1.getName()).hasPerm("soontm.staff")) {
                    player1.sendMessage(new TextComponent(ChatColor.DARK_GREEN + "[STAFF] " + player.getDisplayName() + ChatColor.YELLOW + " left."));
                }
            }
        }
    }
}
