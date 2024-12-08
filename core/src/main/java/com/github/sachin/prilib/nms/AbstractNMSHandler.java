package com.github.sachin.prilib.nms;

import org.bukkit.GameEvent;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Pillager;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.loot.Lootable;
import org.bukkit.permissions.Permission;

import java.util.List;

public abstract class AbstractNMSHandler {

    public abstract NBTItem newItem(ItemStack item);

    public abstract void restock(Villager vil);


    public abstract Entity spawnHelpWantedArmorstand(Location loc, ConfigurationSection config, float facing);

    public abstract void applyHeadTexture(SkullMeta meta, String b64);

    public abstract void addFollowGoal(Villager vil, ItemStack[] temptItems, double speed, Permission perm,boolean checkPermission,boolean update);
    public void removeTemptGoal(Villager vil, ItemStack[] temptItems, double speed, Permission perm,boolean checkPermission,boolean update){}

    public abstract void updateFollowGoal(Villager vil,boolean checkPermission);

    public abstract void addHoldBackCrossBowGoal(Pillager pil);

//    public abstract void fillLoot(Player player, Lootable lootTable);

    public void fill(Player player,Lootable lootTable,String lootTableKey,boolean resetSeed){}

    public abstract Object getElytraUpdatePacket(Object handle, Entity itemframe, NamespacedKey key);

    public void placeWater(Block block){};

    public void attack(Player player,Entity target){};

    public boolean placeItem(Player player, Location location, ItemStack item, BlockFace hitFace, String tweakName, boolean playSound){return false;}

    public void spawnVillager(Villager villager,boolean update){};

    public void avoidPlayer(Entity entity,Player player,ConfigurationSection config, NamespacedKey key){};


    public boolean matchAxoltlVariant(Entity entity,String color){return false;}

    public boolean matchWolfVariant(Entity entity,String variant){return variant.equalsIgnoreCase("PALE");}
    public boolean isScreamingGoat(Entity entity){return false;}

    public List<Entity> getEntitiesWithinRadius(int radius, Entity center){return null;}


    public ItemStack createMap(Location dist,byte zoom,boolean biomePreview){return null;}

    public boolean matchFrogVariant(Entity entity, String variant){return false;}

    public Object getBlockHighlightPacket(Location loc,int color){return null;}

    public ItemStack setVillagerEgg(ItemStack item,Villager villager){return null;}

    public Villager getVillager(ItemStack item, World world, Location location){return null;}

    public void triggerGameEvent(Player player,GameEvent gameEvent,Location location){}

    public void removeTemptGoal(Villager villager){}
}
