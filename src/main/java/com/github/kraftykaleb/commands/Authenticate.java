package com.github.kraftykaleb.commands;

import com.github.kraftykaleb.Main;
import net.alpenblock.bungeeperms.BungeePerms;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

/**
 * Created by Kraft on 8/27/2017.
 */
public class Authenticate {
    /*private Main plugin;
    public Authenticate(Main instance, String name) {
        super(name);
        plugin = instance;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (BungeePerms.getInstance().getPermissionsChecker().hasPerm(sender.getName(), "soontm.staff")) {
            if (plugin.config.getString(p.getUniqueId().toString() + ".ip") != null) {
                if (!(p.getAddress().getAddress().getHostAddress().toString().equals(plugin.config.getString(p.getUniqueId().toString() + ".ip")))) {
                    if (BungeePerms.getInstance().getPermissionsChecker().hasPerm(p.getName(), "soontm.staff")) {
                        p.disconnect(new TextComponent(ChatColor.RED + "You have been permanently banned from this server. \nYour account might be compromised, since this is a staff account. \nPlease contact a guild admin to be unbanned."));
                        return;
                    }
                }
            }
        }
    }*/
}
