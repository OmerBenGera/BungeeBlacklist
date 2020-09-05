package com.ome_r.bungeeblacklist.data;

import com.ome_r.bungeeblacklist.User;
import com.ome_r.bungeeblacklist.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Data {

    private File file;
    private List<User> players;

    public Data(){
        file = new File("plugins/BungeeBlacklist/data.yml");
        players = new ArrayList<>();

        try{
            loadData();
            loadBypass();
        } catch (IOException e){
            e.printStackTrace();
        }

    }

    public void loadData() throws IOException{
        if(!file.getParentFile().exists())
            file.getParentFile().mkdir();

        if(!file.exists())
            file.createNewFile();

        Configuration cfg = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);

        if(cfg.getSection("players") != null){
            for(String str : cfg.getSection("players").getKeys())
                if(!str.contains(".")){
                    UUID uuid = UUID.fromString(str);
                    String name = cfg.getString("players." + str + ".lastName"),
                            reason = translateColors('&', cfg.getString("players." + str + ".reason")),
                            address = cfg.getString("players." + str + ".address");
                    boolean blacklisted = cfg.getBoolean("players." + str + ".blacklisted"),
                            ipBlacklisted = cfg.getBoolean("players." + str + ".ipBlacklisted");
                    List<String> history = new ArrayList<>(cfg.getStringList("players." + str + ".history"));

                    User user = new User(uuid, name);
                    user.setBlacklisted(blacklisted);
                    user.setBlacklistReason(reason);
                    user.setHistory(history);
                    user.setAddress(address);

                    players.add(user);
                }
        }

    }

    public void loadBypass() throws IOException {
        File file = new File("config.yml");
        Configuration cfg = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);

        List<String> groups = new ArrayList<>();

        if(cfg.getSection("permissions") != null){
            for(String group : cfg.getSection("permissions").getKeys())
                for(String permission : cfg.getStringList("permissions." + group))
                    if(permission.equalsIgnoreCase("bungeeblacklist.bypass") ||
                            permission.equalsIgnoreCase("bungeeblacklist.*"))
                        groups.add(group);
        }

        if(cfg.getSection("groups") != null){
            for(String player : cfg.getSection("groups").getKeys())
                for(String group : cfg.getStringList("groups." + player))
                    if(groups.contains(group))
                        if(getUser(player) != null)
                            getUser(player).setCanBypass(true);
        }

    }

    public void save(){
        for(User user : players)
            save(user);
    }

    public void save(User user){
        try{
            ConfigurationProvider provider = ConfigurationProvider.getProvider(YamlConfiguration.class);
            Configuration cfg = provider.load(file);

            cfg.set("players." + user.getUniqueID() + ".address", user.getAddress());
            cfg.set("players." + user.getUniqueID() + ".blacklisted", user.isBlacklisted());
            cfg.set("players." + user.getUniqueID() + ".lastName", user.getName());
            cfg.set("players." + user.getUniqueID() + ".reason",
                    user.getBlacklistReason().replace('§', '&'));
            cfg.set("players." + user.getUniqueID() + ".history", user.getHistory());

            provider.save(cfg, file);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public User newUser(UUID uuid, String name){
        User user = new User(uuid, name);
        players.add(user);
        save(user);

        return user;
    }

    public User getUser(UUID uuid){
        for(User user : players)
            if(user.getUniqueID().equals(uuid))
                return user;

        return null;
    }

    public User getUser(String name){
        for(User user : players)
            if(user.getName().equalsIgnoreCase(name))
                return user;

        UUID uuid = Utils.getUUID(name);

        if(uuid != null)
            return newUser(uuid, name);

        return null;
    }

    public List<User> getUsers(String address){
        List<User> list = new ArrayList<>();

        for(User user : players)
            if(user.getAddress().equals(address))
                list.add(user);

        return list;
    }

    public List<User> getUsers(){
        return new ArrayList<>(players);
    }

    private String translateColors(char altColorChar, String textToTranslate){
        return ChatColor.translateAlternateColorCodes(altColorChar, textToTranslate);
    }

}
