package com.github.sachin.prilib.nms.v1_20_R3;

import com.github.sachin.prilib.nms.NBTItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;

public class ImplNBTItem extends NBTItem {

    private ItemStack item;
    private CompoundTag compound;

    public ImplNBTItem(org.bukkit.inventory.ItemStack item){
        this.item = CraftItemStack.asNMSCopy(item);
        this.compound = this.item.getOrCreateTag();
    }


    @Override
    public void setString(String key, String value) {
        compound.putString(key,value);
    }

    @Override
    public void setBoolean(String key, boolean value) {
        compound.putBoolean(key,value);
    }

    @Override
    public void setInt(String key, int value) {
        compound.putInt(key,value);
    }

    @Override
    public void setLong(String key, long value) {
        compound.putLong(key,value);
    }

    @Override
    public void setDouble(String key, double value) {
        compound.putDouble(key,value);
    }

    @Override
    public String getString(String key) {
        return compound.getString(key);
    }

    @Override
    public boolean getBoolean(String key) {
        return compound.getBoolean(key);
    }

    @Override
    public int getInt(String key) {
        return compound.getInt(key);
    }

    @Override
    public long getLong(String key) {
        return compound.getLong(key);
    }

    @Override
    public double getDouble(String key) {
        return compound.getDouble(key);
    }

    @Override
    public boolean hasKey(String key) {
        return compound.contains(key);
    }

    @Override
    public org.bukkit.inventory.ItemStack getItem() {
        item.save(compound);
        return CraftItemStack.asBukkitCopy(item);
    }

    @Override
    public void removeKey(String key) {
        compound.remove(key);
    }
}
