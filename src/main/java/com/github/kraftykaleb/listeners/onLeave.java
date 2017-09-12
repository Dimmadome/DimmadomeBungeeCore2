package com.github.kraftykaleb.listeners;

import com.github.kraftykaleb.Main;
import net.alpenblock.bungeeperms.BungeePerms;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.concurrent.TimeUnit;

/**
 * Created by Kraft on 8/25/2017.
 */
public class onLeave implements Listener {

    private Main plugin;
    public onLeave(Main ins) { plugin = ins; }

    @EventHandler
    public void onLeave(PlayerDisconnectEvent event) {
        ProxiedPlayer p = event.getPlayer();
        ProxyServer.getInstance().getScheduler().schedule(plugin, new Runnable() {
            public void run() {
                staffLeave(p);
            }
        }, 1, TimeUnit.SECONDS);
    }

    public void staffLeave (ProxiedPlayer player) {
        if (BungeePerms.getInstance().getPermissionsManager().getUser(player.getName()).hasPerm("soontm.staff") || player.getDisplayName().contains("ADMIN") || player.getDisplayName().contains("MOD") || player.getDisplayName().contains("HELPER")) {
            for (ProxiedPlayer player1 : ProxyServer.getInstance().getPlayers()) {
                if (BungeePerms.getInstance().getPermissionsManager().getUser(player1.getName()).hasPerm("soontm.staff")) {
                    plugin.getDiscordBot().sendDiscordMessage("STAFF", player.getName() + " has disconnected!");
                    player1.sendMessage(new TextComponent(ChatColor.DARK_GREEN + "[STAFF] " + player.getDisplayName() + ChatColor.YELLOW + " left."));
                }
            }
        }
    }
}
