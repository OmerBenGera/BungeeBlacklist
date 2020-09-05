package com.ome_r.bungeeblacklist;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class User {

    private UUID uuid;
    private String name, blacklistReason, address;
    private boolean isBlacklisted, canBypass;
    private List<String> history;

    public User(UUID uuid, String name){
        this.uuid = uuid;
        this.name = name;
        this.isBlacklisted = false;
        this.blacklistReason = new String();
        this.history = new ArrayList<>();
        this.address = "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getUniqueID() {
        return uuid;
    }

    public ProxiedPlayer getPlayer(){
        return ProxyServer.getInstance().getPlayer(uuid);
    }

    public boolean isBlacklisted() {
        return isBlacklisted;
    }

    public boolean canBypass(){
        return canBypass;
    }

    public void setCanBypass(boolean canBypass){
        this.canBypass = canBypass;
    }

    public void setBlacklisted(boolean blacklisted) {
        isBlacklisted = blacklisted;
    }

    public String getBlacklistReason() {
        return blacklistReason;
    }

    public void setBlacklistReason(String blacklistReason) {
        this.blacklistReason = blacklistReason;
    }

    public List<String> getHistory(){
        return this.history;
    }

    public void setHistory(List<String> history){
        this.history = new ArrayList<>(history);
    }

    public void setAddress(InetSocketAddress address){
        setAddress(address.toString().split(":")[0]);
    }

    public void setAddress(String address){
        this.address = address;
    }

    public String getAddress() {
        return address;
    }
}
