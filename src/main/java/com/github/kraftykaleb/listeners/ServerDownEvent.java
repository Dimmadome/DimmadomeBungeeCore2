package com.github.kraftykaleb.listeners;

import com.github.kraftykaleb.Main;
import net.md_5.bungee.api.event.ServerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * Created by Kraft on 4/20/2017.
 */
public class ServerDownEvent implements Listener {

    private Main plugin;
    public ServerDownEvent(Main instance) {plugin = instance;}
    @EventHandler
    public void onServerDown(ServerDisconnectEvent e) {
        plugin.sendStaffMessage("");
    }
}
