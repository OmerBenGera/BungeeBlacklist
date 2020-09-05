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

public class CmdBlacklist extends Command {

    public CmdBlacklist(Plugin plugin){
        super("blacklist");
        ProxyServer.getInstance().getPluginManager().registerCommand(plugin, this);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!sender.hasPermission("bungeeblacklist.blacklist") && !sender.hasPermission("bungeeblacklist.*")){
            Messages.sendMessage(sender, Messages.NO_PERMISSION.getMessage());
            return;
        }

        if(args.length < 1){
            Messages.sendMessage(sender,
                    Messages.COMMAND_USAGE.getMessage("/blacklist <player-name> [reason] [-s]"));
            return;
        }

        User user = Main.getData().getUser(args[0]);

        if(user == null){
            Messages.sendMessage(sender, Messages.INVALID_PLAYER.getMessage(args[0]));
            return;
        }

        if((sender instanceof ProxiedPlayer) && ((ProxiedPlayer) sender).getUniqueId().equals(user.getUniqueID())){
            Messages.sendMessage(sender, Messages.SELF_BLACKLIST.getMessage());
            return;
        }

        String reason = Messages.DEFAULT_REASON.getMessage();
        boolean isSilent = false;

        if(args.length > 1){
            reason = new String();
            for(int i = 1; i < args.length; i++){
                if(args[i].equalsIgnoreCase("-s")){
                    isSilent = true;
                    continue;
                }
                reason += args[i] + " ";
            }
            if(reason.equals(""))
                reason = Messages.DEFAULT_REASON.getMessage();
        }

        if(user.getPlayer() != null){
            ProxiedPlayer target = user.getPlayer();
            if((sender instanceof ProxiedPlayer) && (target.hasPermission("bungeeblacklist.bypass") ||
                    target.hasPermission("bungeeblacklist.*"))){
                Messages.sendMessage(sender, Messages.BYPASS_PERMISSION.getMessage(args[0]));
                return;
            }
            for(User tUser : Main.getData().getUsers(user.getAddress()))
                if(tUser.getPlayer() != null) {
                    if(tUser.getPlayer().hasPermission("bungeeblacklist.blacklist") ||
                            tUser.getPlayer().hasPermission("bungeeblacklist.*"))
                        Messages.sendMessage(tUser.getPlayer(), Messages.BLACKLIST_WARNING.getMessage());
                    else tUser.getPlayer().disconnect(new TextComponent(Messages.KICK_MESSAGE.getMessage(reason)));
                }
        }

        for(ProxiedPlayer pl : ProxyServer.getInstance().getPlayers()){
            if(!isSilent){
                Messages.sendMessage(pl,
                        Messages.BLACKLIST_ANNOUNCE.getMessage(sender.getName(), user.getName(), reason));
            }else{
                if(pl.hasPermission("bungeeblacklist.silent") || pl.hasPermission("bungeeblacklist.*")){
                    Messages.sendMessage(pl, Messages.SILENT_PREFIX.getMessage() + " " +
                            Messages.BLACKLIST_ANNOUNCE.getMessage(sender.getName(), user.getName(), reason));
                }
            }
        }

        if(!isSilent){
            Messages.sendMessage(Main.getConsole(),
                    Messages.BLACKLIST_ANNOUNCE.getMessage(sender.getName(), user.getName(), reason));
        }else{
            Messages.sendMessage(Main.getConsole(), Messages.SILENT_PREFIX.getMessage() + " " +
                    Messages.BLACKLIST_ANNOUNCE.getMessage(sender.getName(), user.getName(), reason));
        }

        String uuid = "CONSOLE";
        if(sender instanceof ProxiedPlayer)
            uuid = ((ProxiedPlayer) sender).getUniqueId().toString();

        if(user.isBlacklisted()){
            user.getHistory().remove(user.getHistory().size() - 1);
        }

        user.setBlacklisted(true);
        user.getHistory().add("BLACKLIST;" + uuid + ";" + reason);
        user.setBlacklistReason(reason);
        Main.getData().save(user);
    }

}
