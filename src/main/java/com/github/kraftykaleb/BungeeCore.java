package com.github.kraftykaleb;

import com.github.kraftykaleb.commands.*;
import com.github.kraftykaleb.objects.DiscordBot;
import com.github.kraftykaleb.listeners.PlayerConnectListener;
import com.github.kraftykaleb.listeners.KickListener;
import com.github.kraftykaleb.listeners.PlayerDisconnectListener;
import com.github.kraftykaleb.rank.Rank;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequest;
import net.alpenblock.bungeeperms.BungeePerms;
import net.hypixel.api.HypixelAPI;
import net.hypixel.api.reply.FindGuildReply;
import net.hypixel.api.reply.GuildReply;
import net.hypixel.api.request.Request;
import net.hypixel.api.request.RequestBuilder;
import net.hypixel.api.request.RequestParam;
import net.hypixel.api.request.RequestType;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.json.JSONObject;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by Kraft on 4/19/2017.
 */
public class BungeeCore extends Plugin {

    private static BungeeCore instance;
    public Connection connection;
    public Configuration config;
    public File configFile;

    public Set<String> serverList = new HashSet<>();
    public DiscordBot discordBot;

    public HashMap<String, Configuration> files = new HashMap<>();
    public HashMap<String, Integer> skyflagWins = new HashMap<>();
    public HashMap<String, String> assignedGuilds = new HashMap<>();
    public HashMap<String, String> guildRanks = new HashMap<>();
    public HashMap<String, String> hypixelRanks = new HashMap<>();
//    public HashMap<String, String> donationRank = new HashMap<>();
    public HashMap<String, String> plusColor = new HashMap<>();

    public void onEnable() {
        instance = this;

        registerConfig();
        registerCommandsAndListeners();

        // Opening SQL connection
        openConnection();

        discordBot = new DiscordBot("MzQ5MzkwOTU5MzQ5MzM0MDE2.DH4Unw.VoYKLJNM55eW9Uusklsb2Eas9qw", this);

        ProxyServer.getInstance().getScheduler().schedule(this, () -> {
            for (ServerInfo serverInfo : ProxyServer.getInstance().getServers().values()) {
                InetSocketAddress address = serverInfo.getAddress();
                if (!pingServer(address)) {
                    sendStaffMessage(serverInfo.getName() + " is offline, attempting a reboot! " + ChatColor.RED + ("If this message    §cdisplays more than 5 times, contact a server developer!").replace(" ",  " §c"));
                }
            }
        }, 0, 10, TimeUnit.SECONDS);
    }



    public void onDisable() {

        //try {
        //    session.disconnect();
        //} catch (IOException e) {
        //    e.printStackTrace();
        //}
        closeConnection();
    }

    public static BungeeCore getInstance() {
        return instance;
    }

    public DiscordBot getDiscordBot() {
        return discordBot;
    }


    private synchronized void openConnection() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://172.106.202.99:3306/Kraft_SoonTMDatabase",
                    "Kraft",
                    "KraftLegos11");
        } catch (Exception e) {
            e.printStackTrace();
        }

        ProxyServer.getInstance().getScheduler().schedule(this, () -> {
            closeConnection();
            try {
                connection = DriverManager.getConnection("jdbc:mysql://172.106.202.99:3306/Kraft_SoonTMDatabase",
                        "Kraft",
                        "KraftLegos11");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }, 4,4, TimeUnit.HOURS);
    }

    private synchronized void closeConnection() {
        try {
            if(connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean playerDataContainsPlayer(ProxiedPlayer player) {
        try {
            PreparedStatement sql = connection.prepareStatement("SELECT * FROM `player_data` WHERE player=?;");
            sql.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = sql.executeQuery();

            boolean containsPlayer = resultSet.next();

            sql.close();
            resultSet.close();

            return containsPlayer;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean playerDataContainsPlayer(String player) {
        try {
            PreparedStatement sql = connection.prepareStatement("SELECT * FROM `player_data` WHERE player_name=?;");
            sql.setString(1, player);
            ResultSet resultSet = sql.executeQuery();

            boolean containsPlayer = resultSet.next();

            sql.close();
            resultSet.close();

            return containsPlayer;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void registerCommandsAndListeners() {
        PluginManager pm = ProxyServer.getInstance().getPluginManager();
        // Listeners
        pm.registerListener(this, new PlayerConnectListener(this));
        pm.registerListener(this, new PlayerDisconnectListener(this));
        pm.registerListener(this, new KickListener(this));

        // Commands
        pm.registerCommand(this, new MessageCommand());
        pm.registerCommand(this, new RankCommand());
        pm.registerCommand(this, new StaffCommand());
        pm.registerCommand(this, new StaffChatCommand(this));
        pm.registerCommand(this, new AfkCommand(this));
        pm.registerCommand(this, new ReportCommand());
        pm.registerCommand(this, new ReportsCommand());
        pm.registerCommand(this, new OpenReportCommand());
        pm.registerCommand(this, new BanCommand(this));
        pm.registerCommand(this, new UnbanCommand(this));
        pm.registerCommand(this, new MuteCommand(this));
        pm.registerCommand(this, new UnmuteCommand(this));
        pm.registerCommand(this, new UserInfoCommand(this));
        pm.registerCommand(this, new StatWipeCommand(this));
        pm.registerCommand(this, new KickCommand());
    }

    public static void saveFile(BungeeCore plugin, Configuration config, String resource) {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, new File(plugin.getDataFolder(), resource));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean pingServer(InetSocketAddress address) {
        try {
            Socket socket = new Socket();
            socket.connect(address, 1000);

            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream());

            out.write(0xFE);

            int b;
            StringBuilder str = new StringBuilder();

            while ((b = in.read()) != -1) {
                if (b != 0 && b > 16 && b != 255 && b != 23 && b != 24) {
                    str.append((char) b);
                }
            }

            socket.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void registerConfig() {
        if(!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        configFile = new File(getDataFolder().getPath(), "config.yml");

        if(!configFile.exists()) {
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {

                Configuration config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
                ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, configFile);
                this.config = config;
                this.files.put("config.yml", config);

            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {

            try {
                Configuration config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
                ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, configFile);
                this.config = config;
                this.files.put("config.yml", config);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void findHypixelPlayer(final ProxiedPlayer hypixelPlayer) {
        HttpRequest request = Unirest.get("https://api.hypixel.net/player")
                .queryString("key", "811f839c-b801-48e0-a693-a857e48261a0")
                .queryString("uuid", hypixelPlayer.getUniqueId().toString());

        request.asJsonAsync(new Callback<JsonNode>() {
            @Override
            public void completed(HttpResponse<JsonNode> httpResponse) {
                JSONObject apiResponse = httpResponse.getBody().getObject();
                if (apiResponse.isNull("player")) {
                    // Invalid Hypixel player. Handle how you choose to.
                    return;
                }
                apiResponse = apiResponse.getJSONObject("player");

                //ProxyServer.getInstance().broadcast(new TextComponent("Found player " + hypixelPlayer.getName()));

                ChatColor rankPlusColor;

                if (apiResponse.has("rank")) {

                    String hypixelRank = apiResponse.getString("rank");
                    hypixelRanks.put(hypixelPlayer.getName(), hypixelRank);
                    String prefix = getRankPrefix(hypixelPlayer);

                    if (apiResponse.getString("rank").equals("MVP_PLUS")) {
                        if (apiResponse.has("rankPlusColor")) {
                            rankPlusColor = ChatColor.valueOf(apiResponse.getString("rankPlusColor"));
                            plusColor.put(hypixelPlayer.getName(), apiResponse.getString("rankPlusColor"));
                            hypixelPlayer.setDisplayName(ChatColor.AQUA + "[MVP" + rankPlusColor + "+" + ChatColor.AQUA + "] " + hypixelPlayer.getName());
                        } else {
                            hypixelPlayer.setDisplayName(prefix + hypixelPlayer.getName());
                            plusColor.put(hypixelPlayer.getName(), "NONE");
                        }
                    } else {
                        hypixelPlayer.setDisplayName(prefix + hypixelPlayer.getName());
                        plusColor.put(hypixelPlayer.getName(), "NONE");
                    }
                } else if (apiResponse.has("packageRank")) {
                    String oldPackageRank = apiResponse.getString("packageRank");
                    hypixelRanks.put(hypixelPlayer.getName(), oldPackageRank);
                    String prefix = getRankPrefix(hypixelPlayer);

                    if (apiResponse.getString("packageRank").equals("MVP_PLUS")) {
                        if (apiResponse.has("rankPlusColor")) {
                            rankPlusColor = ChatColor.valueOf(apiResponse.getString("rankPlusColor"));
                            plusColor.put(hypixelPlayer.getName(), apiResponse.getString("rankPlusColor"));
                            hypixelPlayer.setDisplayName(ChatColor.AQUA + "[MVP" + rankPlusColor + "+" + ChatColor.AQUA + "] " + hypixelPlayer.getName());
                            return;
                        } else {
                            hypixelPlayer.setDisplayName(prefix + hypixelPlayer.getName());
                            plusColor.put(hypixelPlayer.getName(), "NONE");
                            return;
                        }
                    } else {
                        hypixelPlayer.setDisplayName(prefix + hypixelPlayer.getName());
                        plusColor.put(hypixelPlayer.getName(), "NONE");
                        return;
                    }
                } else if (apiResponse.has("newPackageRank")) {

                    String newPackageRank = apiResponse.getString("newPackageRank");
                    hypixelRanks.put(hypixelPlayer.getName(), newPackageRank);
                    String prefix = getRankPrefix(hypixelPlayer);
                    if (apiResponse.getString("newPackageRank").equals("MVP_PLUS")) {
                        if (apiResponse.has("rankPlusColor")) {
                            rankPlusColor = ChatColor.valueOf(apiResponse.getString("rankPlusColor"));
                            plusColor.put(hypixelPlayer.getName(), apiResponse.getString("rankPlusColor"));
                            hypixelPlayer.setDisplayName(ChatColor.AQUA + "[MVP" + rankPlusColor + "+" + ChatColor.AQUA + "] " + hypixelPlayer.getName());
                        } else {
                            hypixelPlayer.setDisplayName(prefix + hypixelPlayer.getName());
                            plusColor.put(hypixelPlayer.getName(), "NONE");
                            return;
                        }
                    } else {
                        hypixelPlayer.setDisplayName(prefix + hypixelPlayer.getName());
                        plusColor.put(hypixelPlayer.getName(), "NONE");
                        return;
                    }
                } else {
                    hypixelPlayer.setDisplayName(ChatColor.GRAY + hypixelPlayer.getName());
                    plusColor.put(hypixelPlayer.getName(), "NONE");
                    hypixelRanks.put(hypixelPlayer.getName(), "DEFAULT");
                    return;
                }
                // Handle response some how
            }


            @Override
            public void failed(UnirestException e) {
                // Handle request error some how
            }

            @Override
            public void cancelled() {

            }
        });


    }

    public void findSoonTMGuildMembers() {
        HypixelAPI.getInstance().setApiKey(UUID.fromString("94512d8c-d83c-46b4-a789-a11347fff344"));

        Request request = RequestBuilder.newBuilder(RequestType.GUILD)
                .addParam(RequestParam.GUILD_BY_ID, "574204d60cf2dd4aaafd1586")
                .createRequest();
        HypixelAPI.getInstance().getAsync(request, (net.hypixel.api.util.Callback<GuildReply>) (failCause, result) -> {
            if (failCause != null) {
                failCause.printStackTrace();
            } else {
                for (GuildReply.Guild.Member member : result.getGuild().getMembers()) {
                    //TODO: Check if the

                }
            }
            HypixelAPI.getInstance().finish();
        });
    }

    public void findHypixelGuild(ProxiedPlayer p) {
        HypixelAPI.getInstance().setApiKey(UUID.fromString("811f839c-b801-48e0-a693-a857e48261a0"));

        Request request = RequestBuilder.newBuilder(RequestType.FIND_GUILD)
                .addParam(RequestParam.GUILD_BY_PLAYER_UUID, p.getUniqueId())
                .createRequest();
        HypixelAPI.getInstance().getAsync(request, (net.hypixel.api.util.Callback<FindGuildReply>) (failCause, result) -> {
            if (failCause != null) {
                //failCause.printStackTrace();
            } else {
                //getLogger().log(Level.INFO, p.getDisplayName() + " is in " + result.getGuild().toString());
                config.set(p.getUniqueId() + ".guildId", result.getGuild());
                saveFile(this, config, "config.yml");
                findGuildRank(p, result.getGuild());
            }
            HypixelAPI.getInstance().finish();
        });

    }

    public void findGuildRank(ProxiedPlayer p, String id) {
        HypixelAPI.getInstance().setApiKey(UUID.fromString("811f839c-b801-48e0-a693-a857e48261a0"));

        Request request = RequestBuilder.newBuilder(RequestType.GUILD)
                .addParam(RequestParam.GUILD_BY_ID, id)
                .createRequest();
        HypixelAPI.getInstance().getAsync(request, (net.hypixel.api.util.Callback<GuildReply>) (failCause, result) -> {
            if (failCause != null) {
                //failCause.printStackTrace();
            } else {
                for (GuildReply.Guild.Member member : result.getGuild().getMembers()) {
                    if (member.getUuid().equals(p.getUniqueId())) {
                        config.set(p.getUniqueId() + ".guildName", result.getGuild().getName());
                        assignedGuilds.put(p.getName(), result.getGuild().getName());
                        config.set(p.getUniqueId() + ".guildRank", member.getRank().toString());
                        guildRanks.put(p.getName(), member.getRank().toString());
                        saveFile(this, config, "config.yml");
                    }
                }
            }
            HypixelAPI.getInstance().finish();
        });
    }

    public String getRankPrefix(ProxiedPlayer p) {
        if (hypixelRanks.containsKey(p.getName())) {
            try {
                Rank rank = Rank.valueOf(hypixelRanks.get(p.getName()));
                return rank.getPrefix();
            } catch (Exception e) {
                return Rank.DEFAULT.getColor().toString();
            }
        } else {
            return Rank.DEFAULT.getColor().toString();
        }
    }

    public void sendStaffMessage(String message) {
        for (ProxiedPlayer online : ProxyServer.getInstance().getPlayers()) {
            if (BungeePerms.getInstance().getPermissionsManager().getUser(online.getName()).hasPerm("soontm.staff")) {
                online.sendMessage(new TextComponent(ChatColor.DARK_GREEN + "[STAFF] " + ChatColor.WHITE + message));
            }
        }
    }
}
