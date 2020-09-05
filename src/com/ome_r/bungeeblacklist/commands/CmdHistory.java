package com.ome_r.bungeeblacklist.commands;

import com.ome_r.bungeeblacklist.Main;
import com.ome_r.bungeeblacklist.User;
import com.ome_r.bungeeblacklist.data.Messages;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.UUID;

public class CmdHistory extends Command {

    public CmdHistory(Plugin plugin){
        super("bhistory");
        ProxyServer.getInstance().getPluginManager().registerCommand(plugin, this);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!sender.hasPermission("bungeeblacklist.history") && !sender.hasPermission("bungeeblacklist.*")){
            Messages.sendMessage(sender, Messages.NO_PERMISSION.getMessage());
            return;
        }

        if(args.length != 1){
            Messages.sendMessage(sender, Messages.COMMAND_USAGE.getMessage("/bhistory <player-name>"));
            return;
        }

        User user = Main.getData().getUser(args[0]);

        if(user == null){
            Messages.sendMessage(sender, Messages.INVALID_PLAYER.getMessage(args[0]));
            return;
        }

        String history = "none";

        for(int i = 0; i < user.getHistory().size(); i++){
            String[] split = user.getHistory().get(i).split(";");
            String type = upFirstLetter(split[0]), spacer = "\n", name = "CONSOLE";

            if(!split[1].equals("CONSOLE"))
                name = Main.getData().getUser(UUID.fromString(split[1])).getName();

            if(history.equals("none")) {
                history = new String();
                spacer = "";
            }

            if(type.equalsIgnoreCase("blacklist"))
                history += spacer + Messages.HISTORY_BLACKLIST.getMessage((i + 1), name, split[2]);
            else history += spacer + Messages.HISTORY_UNBLACKLIST.getMessage((i + 1), name);
        }

        Messages.sendMessage(sender, Messages.HISTORY.getMessage(user.getName(), history));
    }

    private String upFirstLetter(String message){
        return message.substring(0, 1).toUpperCase() + message.substring(1).toLowerCase();
    }

}
