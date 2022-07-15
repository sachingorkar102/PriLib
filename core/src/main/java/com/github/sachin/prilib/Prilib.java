package com.github.sachin.prilib;

import com.github.sachin.prilib.nms.NBTItem;
import com.github.sachin.prilib.nms.AbstractNMSHandler;
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
    private String mcVersion;
    private boolean isEnabled;
    private boolean isNMSEnabled;
    private static Prilib instance;


    public void initialize(){
        if(!loadVersions(plugin,mcVersion)){
            this.isNMSEnabled = false;
            log("running incompaitable version of minecraft for the plugin: "+mcVersion);
        }
        else{
            log("Running "+mcVersion+" minecraft version");
        }
    }

    public Prilib(JavaPlugin plugin){
        instance = this;
        this.isEnabled = true;
        this.isNMSEnabled = true;
        this.plugin = plugin;
        this.mcVersion = plugin.getServer().getClass().getPackage().getName().split("\\.")[3];

    }

    private boolean loadVersions(JavaPlugin plugin, String version){

        try {
            //abstractNmsHandler = (AbstractNMSHandler) Class.forName(packageName + ".internal.nms." + internalsName + ".NMSHandler").newInstance();
            nmsHandler = (AbstractNMSHandler) Class.forName("com.github.sachin.prilib.nms."+version+".NMSHandler").getDeclaredConstructor().newInstance(null);
            return true;
        } catch (final ClassNotFoundException | InstantiationException | IllegalAccessException | ClassCastException | NoSuchMethodException |
                       InvocationTargetException exception) {
            exception.printStackTrace();
            return false;
        }

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

    public String getMcVersion() {
        return mcVersion;
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
