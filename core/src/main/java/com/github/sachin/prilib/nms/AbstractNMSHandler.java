package com.github.sachin.prilib.nms;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Pillager;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.permissions.Permission;

public abstract class AbstractNMSHandler {

    public abstract NBTItem newItem(ItemStack item);

    public abstract void restock(Villager vil);


    public abstract Entity spawnHelpWantedArmorstand(Location loc, ConfigurationSection config, float facing);

    public abstract void applyHeadTexture(SkullMeta meta, String b64);

    public abstract void addFollowGoal(Villager vil, ItemStack[] temptItems, double speed, Permission perm);

    public abstract void addHoldBackCrossBowGoal(Pillager pil);
}
