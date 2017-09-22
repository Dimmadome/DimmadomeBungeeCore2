package com.github.kraftykaleb.listeners;

import com.github.kraftykaleb.Main;
import net.md_5.bungee.api.AbstractReconnectHandler;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Iterator;

/**
 * Created by Kraft on 5/5/2017.
 */
public class onKick implements Listener {

    Main plugin;

    public onKick(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onServerKickEvent(ServerKickEvent ev) {
        // Protection against NullPointerException

        ServerInfo kickedFrom = null;

        if (ev.getPlayer().getServer() != null) {
            kickedFrom = ev.getPlayer().getServer().getInfo();
        } else if (this.plugin.getProxy().getReconnectHandler() != null) {// If first server and recohandler
            kickedFrom = this.plugin.getProxy().getReconnectHandler().getServer(ev.getPlayer());
        } else { // If first server and no recohandler
            kickedFrom = AbstractReconnectHandler.getForcedHost(ev.getPlayer().getPendingConnection());
            if (kickedFrom == null) // Can still be null if vhost is null...
            {
                kickedFrom = ProxyServer.getInstance().getServerInfo(ev.getPlayer().getPendingConnection().getListener().getDefaultServer());
            }
        }

        ServerInfo kickTo = this.plugin.getProxy().getServerInfo("lobby1");

        String reason = BaseComponent.toLegacyText(ev.getKickReasonComponent());


        // Avoid the loop
        if (kickedFrom != null && kickedFrom.equals(kickTo)) {

            if (!reason.contains("You are not white")) {
                ev.getPlayer().sendMessage(new TextComponent(ChatColor.RED + reason.replace(" ", ChatColor.RED + " ")));
                return;
            }
            kickTo = this.plugin.getProxy().getServerInfo("neverland");
            ev.setCancelled(true);
            ev.setCancelServer(kickTo);
            return;
        }



        ev.setCancelled(true);
        ev.setCancelServer(kickTo);
        if (!reason.contains("You logged in from another location")) {
            ev.getPlayer().sendMessage(new TextComponent(ChatColor.RED + reason.replace(" ", ChatColor.RED + " ")));
        }


    }
}
