package com.github.kraftykaleb.listeners;

import com.github.kraftykaleb.BungeeCore;
import net.md_5.bungee.api.event.ServerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * Created by Kraft on 4/20/2017.
 */
public class ServerDownListener implements Listener {

    public ServerDownListener() {
    }

    @EventHandler
    public void onServerDown(ServerDisconnectEvent e) {
        BungeeCore.getInstance().sendStaffMessage("");
    }
}
