package com.github.kraftykaleb;

import com.github.kraftykaleb.commands.*;
import com.github.kraftykaleb.listeners.onJoin;
import com.github.kraftykaleb.listeners.onKick;
import com.google.common.io.ByteStreams;
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
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.json.JSONObject;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.sql.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * Created by Kraft on 4/19/2017.
 */
public class Main extends Plugin {

    public Connection connection;
    public Configuration config;
    public File configFile;

    public Set<String> serverList;
    public SlackBot slackBot;

    public HashMap<String, Configuration> files = new HashMap<>();
    public HashMap<String, Integer> skyflagwins = new HashMap<>();
    public HashMap<String, String> assignedguilds = new HashMap<>();
    public HashMap<String, String> guildranks = new HashMap<>();
    public HashMap<String, String> hypixelranks = new HashMap<>();
    public HashMap<String, String> donationrank = new HashMap<>();

    public void onEnable() {

        this.serverList = new HashSet<>();

        registerConfig();

        registerCommands();


        slackBot = new SlackBot();

        //session = SlackSessionFactory.createWebSocketSlackSession("xoxb-191922248310-6xlopSsAqDP4D1fMVmsrhjHb");

        //slackMessagePostedEventContent(session);

        /*try {
            session.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        {
            // first define the listener
            SlackMessagePostedListener messagePostedListener = new SlackMessagePostedListener()
            {
                @Override
                public void onEvent(SlackMessagePosted event, SlackSession session)
                {
                    SlackChannel channelOnWhichMessageWasPosted = event.getChannel();
                    String messageContent = event.getMessageContent();
                    SlackUser messageSender = event.getSender();

                    sendStaffMessage(ChatColor.YELLOW + "[SLACK] " + messageSender.getUserName() + ChatColor.WHITE + ": " + messageContent);
                }
            };
            //add it to the session
            session.addMessagePostedListener(messagePostedListener);

            //that's it, the listener will get every message post events the bot can get notified on
            //(IE: the messages sent on channels it joined or sent directly to it)
        }

        SlackChannel channel = session.findChannelByName("guild-ideas"); //make sure bot is a member of the channel.
*/
        ProxyServer.getInstance().getScheduler().schedule(this, new Runnable() {
            @Override
            public void run() {
                for (ServerInfo serverInfo : ProxyServer.getInstance().getServers().values()) {
                    //String address = "167.114.216.188:25591";
                    InetSocketAddress address = serverInfo.getAddress();

                    if (!pingServer(address)) {
                        sendStaffMessage(serverInfo.getName() + " is offline, attempting a reboot! " + ChatColor.RED + ("If this message displays more than 5 times, contact a server developer!").replace(" ", ChatColor.RED + " "));
                    }
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

        try {
            if (connection != null || connection.isClosed()) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public synchronized void openConnection() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://172.106.202.99:3306/Kraft_SoonTMDatabase", "Kraft", "KraftLegos11");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void closeConnection () {
        try {
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized boolean playerDataContainsPlayer(ProxiedPlayer player) {
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

    public void registerCommands () {
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new Msg("msg"));
        ProxyServer.getInstance().getPluginManager().registerListener(this, new onJoin(this));
        ProxyServer.getInstance().getPluginManager().registerListener(this, new onKick(this));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new Rank("rank"));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new Staff("staff"));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new Sc(this, "sc"));
    }

    public void loadFile(Main plugin, String resource) {

    }

    public static void saveFile(Main plugin, Configuration config, String resource) {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, new File(plugin.getDataFolder(), resource));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean pingServer(InetSocketAddress address) {
        try {
            Socket socket = new Socket();
            socket.connect(address, 1 * 1000);

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
                .queryString("key", "94512d8c-d83c-46b4-a789-a11347fff344")
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

                ProxyServer.getInstance().getLogger().log(Level.INFO, "Found player " + hypixelPlayer.getName());

                ChatColor plusColor = null;
                String prefix = null;


                if (apiResponse.has("rank")) {

                    String hypixelRank = (apiResponse.has("rank") ? apiResponse.getString("rank") : "NONE");
                    hypixelranks.put(hypixelPlayer.getName(), hypixelRank);
                    if (hypixelRank.equals("ADMIN")) {
                        hypixelPlayer.setDisplayName((ChatColor.RED + "[ADMIN] " + hypixelPlayer.getName()));
                    }
                    if (hypixelRank.equals("MODERATOR")) {
                        hypixelPlayer.setDisplayName(ChatColor.DARK_GREEN + "[MOD] " + hypixelPlayer.getName());
                    }
                    if (hypixelRank.equals("HELPER")) {
                        hypixelPlayer.setDisplayName(ChatColor.BLUE + "[HELPER] " + hypixelPlayer.getName());
                    }
                    if (hypixelRank.equals("MVP")) {
                        hypixelPlayer.setDisplayName(ChatColor.AQUA + "[MVP] " + hypixelPlayer.getName());
                    }
                    if (hypixelRank.equals("VIP")) {
                        hypixelPlayer.setDisplayName(ChatColor.GREEN + "[VIP] " + hypixelPlayer.getName());
                    }
                    if (hypixelRank.equals("VIP_PLUS")) {
                        hypixelPlayer.setDisplayName(ChatColor.GREEN + "[VIP" + ChatColor.GOLD + "+" + ChatColor.GREEN + "] " + hypixelPlayer.getName());
                    }
                    if (apiResponse.getString("rank").equals("MVP_PLUS")) {
                        if (apiResponse.has("rankPlusColor")) {
                            plusColor = ChatColor.valueOf(apiResponse.getString("rankPlusColor"));
                            hypixelPlayer.setDisplayName(ChatColor.AQUA + "[MVP" + plusColor + "+" + ChatColor.AQUA + "] " + hypixelPlayer.getName());
                        } else {
                            hypixelPlayer.setDisplayName(ChatColor.AQUA + "[MVP" + ChatColor.RED + "+" + ChatColor.AQUA + "] " + hypixelPlayer.getName());
                        }
                    }
                } else if (apiResponse.has("packageRank")) {
                    String oldPackageRank = (apiResponse.has("packageRank") ? apiResponse.getString("packageRank") : "DEFAULT");
                    hypixelranks.put(hypixelPlayer.getName(), oldPackageRank);
                    if (oldPackageRank.equals("ADMIN")) {
                        hypixelPlayer.setDisplayName(ChatColor.RED + "[ADMIN] " + hypixelPlayer.getName());
                    }
                    if (oldPackageRank.equals("MODERATOR")) {
                        hypixelPlayer.setDisplayName(ChatColor.DARK_GREEN + "[MOD] " + hypixelPlayer.getName());
                    }
                    if (oldPackageRank.equals("HELPER")) {
                        hypixelPlayer.setDisplayName(ChatColor.BLUE + "[HELPER] " + hypixelPlayer.getName());
                    }
                    if (oldPackageRank.equals("MVP")) {
                        hypixelPlayer.setDisplayName(ChatColor.AQUA + "[MVP] " + hypixelPlayer.getName());
                    }
                    if (oldPackageRank.equals("VIP")) {
                        hypixelPlayer.setDisplayName(ChatColor.GREEN + "[VIP] " + hypixelPlayer.getName());
                    }
                    if (oldPackageRank.equals("VIP_PLUS")) {
                        hypixelPlayer.setDisplayName(ChatColor.GREEN + "[VIP" + ChatColor.GOLD + "+" + "] " + hypixelPlayer.getName());
                    }
                    if (apiResponse.getString("packageRank").equals("MVP_PLUS")) {
                        if (apiResponse.has("rankPlusColor")) {
                            plusColor = ChatColor.valueOf(apiResponse.getString("rankPlusColor"));
                            hypixelPlayer.setDisplayName(ChatColor.AQUA + "[MVP" + plusColor + "+" + ChatColor.AQUA + "] " + hypixelPlayer.getName());
                        } else {
                            hypixelPlayer.setDisplayName(ChatColor.AQUA + "[MVP" + ChatColor.RED + "+" + ChatColor.AQUA + "] " + hypixelPlayer.getName());
                        }
                    }
                } else if (apiResponse.has("newPackageRank")) {

                    String newPackageRank = (apiResponse.has("newPackageRank") ? apiResponse.getString("newPackageRank") : "DEFAULT");
                    hypixelranks.put(hypixelPlayer.getName(), newPackageRank);
                    if (newPackageRank.equals("ADMIN")) {
                        hypixelPlayer.setDisplayName(ChatColor.RED + "[ADMIN] " + hypixelPlayer.getName());
                    }
                    if (newPackageRank.equals("MODERATOR")) {
                        hypixelPlayer.setDisplayName(ChatColor.DARK_GREEN + "[MOD] " + hypixelPlayer.getName());
                    }
                    if (newPackageRank.equals("HELPER")) {
                        hypixelPlayer.setDisplayName(ChatColor.BLUE + "[HELPER] " + hypixelPlayer.getName());
                    }
                    if (newPackageRank.equals("MVP")) {
                        hypixelPlayer.setDisplayName(ChatColor.AQUA + "[MVP] " + hypixelPlayer.getName());
                    }
                    if (newPackageRank.equals("VIP")) {
                        hypixelPlayer.setDisplayName(ChatColor.GREEN + "[VIP] " + hypixelPlayer.getName());
                    }
                    if (newPackageRank.equals("VIP_PLUS")) {
                        hypixelPlayer.setDisplayName(ChatColor.GREEN + "[VIP" + ChatColor.GOLD + "+" + ChatColor.GREEN + "] " + hypixelPlayer.getName());
                    }
                    if (apiResponse.getString("newPackageRank").equals("MVP_PLUS")) {
                        if (apiResponse.has("rankPlusColor")) {
                            plusColor = ChatColor.valueOf(apiResponse.getString("rankPlusColor"));
                            hypixelPlayer.setDisplayName(ChatColor.AQUA + "[MVP" + plusColor + "+" + ChatColor.AQUA + "] " + hypixelPlayer.getName());

                        } else {
                            hypixelPlayer.setDisplayName(ChatColor.AQUA + "[MVP" + ChatColor.RED + "+" + ChatColor.AQUA + "] " + hypixelPlayer.getName());
                        }
                    }
                } else {
                    hypixelPlayer.setDisplayName(ChatColor.GRAY + hypixelPlayer.getName());
                    hypixelranks.put(hypixelPlayer.getName(), "DEFAULT");
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

    public void findSoonTMGuildMembers () {
        HypixelAPI.getInstance().setApiKey(UUID.fromString("94512d8c-d83c-46b4-a789-a11347fff344"));

        Request request = RequestBuilder.newBuilder(RequestType.GUILD)
                .addParam(RequestParam.GUILD_BY_ID, "574204d60cf2dd4aaafd1586")
                .createRequest();
        HypixelAPI.getInstance().getAsync(request, (net.hypixel.api.util.Callback<GuildReply>) (failCause, result) -> {
            if (failCause != null) {
                failCause.printStackTrace();
            } else {
                for (GuildReply.Guild.Member member : result.getGuild().getMembers()) {
                    //TODO Check if the

                }
            }
            HypixelAPI.getInstance().finish();
        });
    }

    public void findHypixelGuild(ProxiedPlayer p) {
        HypixelAPI.getInstance().setApiKey(UUID.fromString("94512d8c-d83c-46b4-a789-a11347fff344"));

        Request request = RequestBuilder.newBuilder(RequestType.FIND_GUILD)
                .addParam(RequestParam.GUILD_BY_PLAYER_UUID, p.getUniqueId())
                .createRequest();
        HypixelAPI.getInstance().getAsync(request, (net.hypixel.api.util.Callback<FindGuildReply>) (failCause, result) -> {
            if (failCause != null) {
                failCause.printStackTrace();
            } else {
                getLogger().log(Level.INFO, p.getDisplayName() + " is in " + result.getGuild().toString());
                config.set(p.getUniqueId() + ".guildId", result.getGuild().toString());
                saveFile(this, config, "config.yml");
                findGuildRank(p, result.getGuild().toString());
            }
            HypixelAPI.getInstance().finish();
        });

    }

    public void findGuildRank(ProxiedPlayer p, String id) {
        HypixelAPI.getInstance().setApiKey(UUID.fromString("94512d8c-d83c-46b4-a789-a11347fff344"));

        Request request = RequestBuilder.newBuilder(RequestType.GUILD)
                .addParam(RequestParam.GUILD_BY_ID, id)
                .createRequest();
        HypixelAPI.getInstance().getAsync(request, (net.hypixel.api.util.Callback<GuildReply>) (failCause, result) -> {
            if (failCause != null) {
                failCause.printStackTrace();
            } else {
                for (GuildReply.Guild.Member member : result.getGuild().getMembers()) {
                    if (member.getUuid().equals(p.getUniqueId())) {
                        config.set(p.getUniqueId() + ".guildName", result.getGuild().getName());
                        assignedguilds.put(p.getName(), result.getGuild().getName());
                        config.set(p.getUniqueId() + ".guildRank", member.getRank().toString());
                        guildranks.put(p.getName(), member.getRank().toString());
                        saveFile(this, config, "config.yml");
                    }
                }
            }
            HypixelAPI.getInstance().finish();
        });
    }

    public static void sendStaffMessage (String message) {
        for (ProxiedPlayer player1 : ProxyServer.getInstance().getPlayers()) {
            if (BungeePerms.getInstance().getPermissionsManager().getUser(player1.getName()).hasPerm("soontm.staff")) {
                player1.sendMessage(new TextComponent(ChatColor.DARK_GREEN + "[STAFF] " + message));
            }
        }
    }
}
