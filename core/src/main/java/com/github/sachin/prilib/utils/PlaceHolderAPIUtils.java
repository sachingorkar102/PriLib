package com.github.sachin.prilib.utils;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class PlaceHolderAPIUtils extends PluginHook{



    public static String get(Player player,String text){
        if(isEnabled){
            return me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player,text);
        }
        return text;
    }


    public static String get(OfflinePlayer offlinePlayer, String text){
        if(isEnabled){
            return me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(offlinePlayer,text);
        }
        return text;
    }



}
