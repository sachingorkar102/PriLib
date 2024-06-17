package com.github.sachin.prilib;

import com.github.sachin.prilib.nms.NBTItem;
import com.github.sachin.prilib.nms.AbstractNMSHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

// Main class for lib, should be initialized to work
public final class Prilib {


    private AbstractNMSHandler nmsHandler;

    private JavaPlugin plugin;

    public final Random random = new Random();
    public final ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
    private String bukkitVersion;
    private McVersion mcVersion;

    private boolean isEnabled;
    private boolean isNMSEnabled;
    private static Prilib instance;


    public void initialize(){
        this.isNMSEnabled = loadVersions(plugin, bukkitVersion,mcVersion);
        log("Running "+mcVersion+" minecraft version");
    }

    public Prilib(JavaPlugin plugin){
        instance = this;
        this.isEnabled = true;
        this.isNMSEnabled = true;
        this.plugin = plugin;
        mcVersion = McVersion.current();

        if (mcVersion.isAtLeast(1, 20)) {
            bukkitVersion = "v" + mcVersion.getMajor() + "_" + mcVersion.getMinor() + ((mcVersion.getPatch() > 0) ? ("_" + mcVersion.getPatch()) : "");
        } else {
            bukkitVersion = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        }

    }

    private boolean loadVersions(JavaPlugin plugin, String bukkitVersion,McVersion mcVersion){
        String packageName = bukkitVersion;
        if(mcVersion.equals(new McVersion(1,19))){
            packageName = "v1_19_R1";
        }
        else if(mcVersion.equals(new McVersion(1,19,1)) || mcVersion.equals(new McVersion(1,19,2))){
            packageName = "v1_19_R11";
        }
        else if(mcVersion.equals(new McVersion(1,20,6))){
            packageName = "v1_20_5";
        }
        try {
            //abstractNmsHandler = (AbstractNMSHandler) Class.forName(packageName + ".internal.nms." + internalsName + ".NMSHandler").newInstance();
            nmsHandler = (AbstractNMSHandler) Class.forName("com.github.sachin.prilib.nms."+packageName+".NMSHandler").getDeclaredConstructor().newInstance(null);
            return true;
        } catch (final ClassNotFoundException | InstantiationException | IllegalAccessException | ClassCastException | NoSuchMethodException |
                       InvocationTargetException exception) {
            exception.printStackTrace();
            return false;
        }

    }

    public static void sendConsoleMessage(String message){
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',message));
    }

    public NamespacedKey getKey(String key){
        return new NamespacedKey(plugin,key);
    }

    public void log(String text){
        getDependPlugin().getLogger().info("[PriLib] "+text);
    }

    public AbstractNMSHandler getNmsHandler() {
        return nmsHandler;
    }

    public McVersion getMcVersion() {
        return mcVersion;
    }

    public String getBukkitVersion() {
        return bukkitVersion;
    }

    public JavaPlugin getDependPlugin() {
        return plugin;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public boolean isNMSEnabled() {
        return isNMSEnabled;
    }

    public static Prilib getInstance() {
        return instance;
    }

    public Random getRandom() {
        return random;
    }

    public ThreadLocalRandom getThreadLocalRandom() {
        return threadLocalRandom;
    }

    public static NBTItem newItem(ItemStack item){
        return getInstance().getNmsHandler().newItem(item);
    }
}
