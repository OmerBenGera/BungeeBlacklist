package com.ome_r.bungeeblacklist;

import com.ome_r.bungeeblacklist.commands.CmdBlacklist;
import com.ome_r.bungeeblacklist.commands.CmdHistory;
import com.ome_r.bungeeblacklist.commands.CmdUnblacklist;
import com.ome_r.bungeeblacklist.data.Data;
import com.ome_r.bungeeblacklist.data.Messages;
import com.ome_r.bungeeblacklist.listeners.LoginListener;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

public class Main extends Plugin{

    private static Main instance;
    private static Data data;

    @Override
    public void onEnable() {
        setupClasses();
        loadListener();
        loadCommands();
        Messages.loadMessages();
    }

    @Override
    public void onDisable() {
        data.save();
    }

    private void setupClasses(){
        instance = this;
        data = new Data();
    }

    private void loadListener(){
        new LoginListener(this);
    }

    private void loadCommands(){
        new CmdBlacklist(this);
        new CmdUnblacklist(this);
        new CmdHistory(this);
    }

    public static CommandSender getConsole(){
        return ProxyServer.getInstance().getConsole();
    }

    public static Data getData(){
        return data;
    }

    public static Main getInstance(){
        return instance;
    }

}
