package com.ome_r.bungeeblacklist.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public class Utils {

    private static Gson gson = new GsonBuilder().create();

    public static UUID getUUID(String name) {
        try {
            HttpURLConnection con = (HttpURLConnection) new URL("https://api.mojang.com/users/profiles/minecraft/" + name).openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

            JsonElement element = gson.fromJson(reader, JsonElement.class);

            if(element == null)
                return null;

            JsonObject json = element.getAsJsonObject();

            reader.close();
            con.disconnect();

            return parseUUID(json.get("id").getAsString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static UUID parseUUID(String str){
        return UUID.fromString(str.replaceFirst("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));
    }

}