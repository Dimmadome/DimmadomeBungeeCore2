package com.github.kraftykaleb.commands;

import net.alpenblock.bungeeperms.BungeePerms;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 * Created by Kraft on 9/11/2017.
 */
public class OpenReportCommand extends Command {

    public OpenReportCommand() {
        super("openreport");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (BungeePerms.getInstance().getPermissionsChecker().hasPerm(sender.getName(), "soontm.staff")) {
            if (args.length == 1) {
                ProxiedPlayer p = (ProxiedPlayer) sender;
                p.connect(ProxyServer.getInstance().getPlayer(args[0]).getServer().getInfo());
                ReportCommand.reportList.remove(args[0].toLowerCase());
            }
        }
    }
}
