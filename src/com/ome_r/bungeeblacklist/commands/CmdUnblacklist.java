package com.ome_r.bungeeblacklist.commands;

import com.ome_r.bungeeblacklist.Main;
import com.ome_r.bungeeblacklist.User;
import com.ome_r.bungeeblacklist.data.Messages;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;

public class CmdUnblacklist extends Command {

    public CmdUnblacklist(Plugin plugin){
        super("unblacklist");
        ProxyServer.getInstance().getPluginManager().registerCommand(plugin, this);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!sender.hasPermission("bungeeblacklist.unblacklist") && !sender.hasPermission("bungeeblacklist.*")){
            Messages.sendMessage(sender, Messages.NO_PERMISSION.getMessage());
            return;
        }

        if(args.length != 1 && args.length != 2){
            Messages.sendMessage(sender,
                    Messages.COMMAND_USAGE.getMessage("/unblacklist <player-name> [-s]"));
            return;
        }

        User user = Main.getData().getUser(args[0]);

        if(user == null){
            Messages.sendMessage(sender, Messages.INVALID_PLAYER.getMessage(args[0]));
            return;
        }

        if(!user.isBlacklisted()){
            Messages.sendMessage(sender, Messages.NOT_BLACKLISTED.getMessage(user.getName()));
            return;
        }

        boolean isSilent = false;

        if(args.length == 2){
            if(!args[1].equalsIgnoreCase("-s")){
                Messages.sendMessage(sender,
                        Messages.COMMAND_USAGE.getMessage("/unblacklist <player-name> [-s]"));
                return;
            }
            isSilent = true;
        }

        if((sender instanceof ProxiedPlayer) && ((ProxiedPlayer) sender).getUniqueId().equals(user.getUniqueID())){
            Messages.sendMessage(sender, Messages.SELF_UNBLACKLIST.getMessage());
            return;
        }

        for(ProxiedPlayer pl : ProxyServer.getInstance().getPlayers()){
            if(!isSilent){
                Messages.sendMessage(pl, Messages.UNBLACKLIST_ANNOUNCE.getMessage(sender.getName(), user.getName()));
            }else{
                if(pl.hasPermission("bungeeblacklist.silent") || pl.hasPermission("bungeeblacklist.*")){
                    Messages.sendMessage(pl, Messages.SILENT_PREFIX.getMessage() + " " +
                            Messages.UNBLACKLIST_ANNOUNCE.getMessage(sender.getName(), user.getName()));
                }
            }
        }

        if(!isSilent){
            Messages.sendMessage(Main.getConsole(),
                    Messages.UNBLACKLIST_ANNOUNCE.getMessage(sender.getName(), user.getName()));
        }else{
            Messages.sendMessage(Main.getConsole(), Messages.SILENT_PREFIX.getMessage() + " " +
                    Messages.UNBLACKLIST_ANNOUNCE.getMessage(sender.getName(), user.getName()));
        }

        String uuid = "CONSOLE";
        if(sender instanceof ProxiedPlayer)
            uuid = ((ProxiedPlayer) sender).getUniqueId().toString();

        user.setBlacklisted(false);
        user.getHistory().add("UNBLACKLIST;" + uuid);
        Main.getData().save(user);
    }

}
