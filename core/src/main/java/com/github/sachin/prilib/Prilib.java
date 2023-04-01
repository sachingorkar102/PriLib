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
    private String mcVersion;

    private boolean isEnabled;
    private boolean isNMSEnabled;
    private static Prilib instance;


    public void initialize(){
        this.isNMSEnabled = loadVersions(plugin, bukkitVersion,mcVersion);
        log("Running "+ bukkitVersion +" bukkit version and "+mcVersion+" minecraft version");
    }

    public Prilib(JavaPlugin plugin){
        instance = this;
        this.isEnabled = true;
        this.isNMSEnabled = true;
        this.plugin = plugin;
        this.bukkitVersion = plugin.getServer().getClass().getPackage().getName().split("\\.")[3];
        int currentMajor = Integer.parseInt(Bukkit.getBukkitVersion().split("\\.")[0]);
        int currentMinor = Integer.parseInt(Bukkit.getBukkitVersion().split("\\.")[1].split("-")[0]);
        int currentPatch = Bukkit.getBukkitVersion().chars().filter(ch -> ch == '.').count() == 2 ? 0 : Integer.parseInt(Bukkit.getBukkitVersion().split("\\.")[2].split("-")[0]);
        mcVersion = currentMajor+"."+currentMinor;
        if(currentPatch>0){
            mcVersion = mcVersion+"."+currentPatch;
        }

    }

    private boolean loadVersions(JavaPlugin plugin, String bukkitVersion,String mcVersion){
        String packageName = bukkitVersion;
        if(mcVersion.equals("1.19")){
            packageName = "v1_19_R1";
        }
        else if(mcVersion.equals("1.19.1") || mcVersion.equals("1.19.2")){
            packageName = "v1_19_R11";
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
