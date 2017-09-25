package com.github.kraftykaleb.rank;

import net.md_5.bungee.api.ChatColor;

public enum Rank {

    ADMIN("Admin", "§c[ADMIN]", ChatColor.RED),
    MODERATOR("Moderator", "§2[MOD]", ChatColor.DARK_GREEN),
    HELPER("Helper", "§9[HELPER]", ChatColor.BLUE),
    MVP_PLUS("MVP+", "§b[MVP§c+§b]", ChatColor.AQUA),
    MVP("MVP", "§b[MVP]", ChatColor.AQUA),
    VIP_PLUS("VIP+", "§a[VIP§6+§a]", ChatColor.GREEN),
    VIP("VIP", "§a[VIP]", ChatColor.GREEN),
    DEFAULT("Default", "", ChatColor.GRAY);

    private String name;
    private String prefix;
    private ChatColor color;

    Rank(String name, String prefix, ChatColor color) {
        this.name = name;
        this.prefix = prefix;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public String getPrefix() {
        return prefix;
    }

    public ChatColor getColor() {
        return color;
    }
}
