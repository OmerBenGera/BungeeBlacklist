package com.ome_r.bungeeblacklist.data;

import com.ome_r.bungeeblacklist.Main;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public enum Messages {

    NO_PERMISSION, COMMAND_USAGE, DEFAULT_REASON, INVALID_PLAYER, BYPASS_PERMISSION, SELF_BLACKLIST, KICK_MESSAGE,
    BLACKLIST_ANNOUNCE, SILENT_PREFIX, SELF_UNBLACKLIST, UNBLACKLIST_ANNOUNCE, NOT_BLACKLISTED, HISTORY,
    HISTORY_BLACKLIST, HISTORY_UNBLACKLIST, BLACKLIST_WARNING;

    private String message;

    public void setMessage(String message){
        this.message = message;
    }

    public String getMessage(){
        return new String(message);
    }

    public String getMessage(Object... objects){
        String message = new String(this.message);
        int counter = 0;

        for(Object obj : objects){
            message = message.replace("{" + counter + "}", obj.toString());
            counter++;
        }

        return message;
    }

    public static void sendMessage(CommandSender sender, String msg){
        if(msg.contains("\n")){
            String[] split = msg.split("\n");
            for(String str : split)
                sender.sendMessage(new TextComponent(str));
        }

        else{
            sender.sendMessage(new TextComponent(msg));
        }
    }

    public static void loadMessages(){
        try {
            File file = new File("plugins/BungeeBlacklist/messages.yml");

            createFile(file);

            Configuration cfg = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);

            for(String str : cfg.getKeys()) {
                Messages.valueOf(str).setMessage(translateColors(cfg.getString(str)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createFile(File file) throws IOException{
        if (!file.getParentFile().exists())
            file.getParentFile().mkdir();

        if (!file.exists()) {
            InputStream in = Main.getInstance().getResourceAsStream("messages.yml");
            Files.copy(in, file.toPath());
        }
    }

    private static String translateColors(String message){
        return ChatColor.translateAlternateColorCodes('&', message);
    }

}
