package com.ome_r.bungeeblacklist.listeners;

import com.ome_r.bungeeblacklist.User;
import com.ome_r.bungeeblacklist.data.Data;
import com.ome_r.bungeeblacklist.Main;
import com.ome_r.bungeeblacklist.data.Messages;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LoginListener implements Listener {

    private Data data = Main.getData();
    private List<User> shouldGetError = new ArrayList<>();

    public LoginListener(Plugin plugin){
        ProxyServer.getInstance().getPluginManager().registerListener(plugin, this);
    }

    @EventHandler
    public void onLogin(LoginEvent e){
        UUID uuid = e.getConnection().getUniqueId();
        User user = data.getUser(uuid);

        if(user == null)
            user = data.newUser(uuid, e.getConnection().getName());

        if(!user.getName().equalsIgnoreCase(e.getConnection().getName())) {
            user.setName(e.getConnection().getName());
            Main.getData().save(user);
        }

        user.setAddress(e.getConnection().getAddress());
        Main.getData().save(user);

        boolean blockPlayer = false;
        String reason = new String();

        for(User users : data.getUsers(user.getAddress())) {
            if (users.isBlacklisted()) {
                blockPlayer = true;
                reason = users.getBlacklistReason();
                break;
            }
        }

        if(blockPlayer){
            if(user.canBypass())
                shouldGetError.add(user);
            else{
                e.setCancelled(true);
                e.setCancelReason(Messages.KICK_MESSAGE.getMessage(reason));
            }
        }

    }

    @EventHandler
    public void onPostLogin(PostLoginEvent e){
        User user = data.getUser(e.getPlayer().getUniqueId());

        if(shouldGetError.contains(user)){
            e.getPlayer().sendMessage(new TextComponent(Messages.BLACKLIST_WARNING.getMessage()));
            shouldGetError.remove(user);
        }

    }

}
