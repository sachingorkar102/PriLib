package com.github.sachin.prilib.utils;

import com.github.sachin.prilib.Prilib;
import com.google.common.base.Enums;
import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;

import java.util.HashMap;

public class AbstractPermManager {

    protected static PermissionDefault defaultValue = PermissionDefault.TRUE;


    public static void reload(){
        defaultValue = Enums.getIfPresent(PermissionDefault.class, Prilib.getInstance().getDependPlugin().getConfig().getString("default-permissions","TRUE").toUpperCase()).or(PermissionDefault.TRUE);

    }


    protected static Permission get(String permission){
        Permission perm = new Permission(permission,defaultValue);
        PluginManager pm = Bukkit.getPluginManager();
        Bukkit.getPluginManager().addPermission(perm);
        return perm;
    }

    protected static Permission get(String parent,String child){
        HashMap<String,Boolean> map = new HashMap<>();
        map.put(child,true);
        Permission perm = new Permission(parent,defaultValue,map);
        PluginManager pm = Bukkit.getPluginManager();
        Bukkit.getPluginManager().addPermission(perm);
        return perm;
    }

    protected static PermissionDefault getDefaultValue() {
        return defaultValue;
    }
}
