package com.github.sachin.prilib.nms;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Pillager;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.loot.Lootable;
import org.bukkit.permissions.Permission;

public abstract class AbstractNMSHandler {

    public abstract NBTItem newItem(ItemStack item);

    public abstract void restock(Villager vil);


    public abstract Entity spawnHelpWantedArmorstand(Location loc, ConfigurationSection config, float facing);

    public abstract void applyHeadTexture(SkullMeta meta, String b64);

    public abstract void addFollowGoal(Villager vil, ItemStack[] temptItems, double speed, Permission perm,boolean checkPermission,boolean update);

    public abstract void updateFollowGoal(Villager vil,boolean checkPermission);

    public abstract void addHoldBackCrossBowGoal(Pillager pil);

    public abstract void fillLoot(Player player, Lootable lootTable);

    public abstract Object getElytraUpdatePacket(Object handle, Entity itemframe, NamespacedKey key);
}
