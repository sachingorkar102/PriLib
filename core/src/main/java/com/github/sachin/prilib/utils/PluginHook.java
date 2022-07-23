package com.github.sachin.prilib.utils;

import com.github.sachin.prilib.Prilib;
import org.bukkit.Bukkit;

public class PluginHook {

    public static boolean isEnabled;
    public static String plugin;

    public static void setUp(String pl){
        plugin = pl;
        isEnabled = Bukkit.getServer().getPluginManager().isPluginEnabled(plugin);
        if(isEnabled){
            Prilib.sendConsoleMessage("Running "+pl+" as a softdepend");
        }
    }
}
