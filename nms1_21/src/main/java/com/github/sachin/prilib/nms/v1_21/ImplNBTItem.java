package com.github.sachin.prilib.nms.v1_21;

import com.github.sachin.prilib.nms.NBTItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.bukkit.craftbukkit.v1_21_R1.inventory.CraftItemStack;

public class ImplNBTItem extends NBTItem {

    private ItemStack item;
    private CompoundTag compound;

    public ImplNBTItem(org.bukkit.inventory.ItemStack item){
        this.item = CraftItemStack.asNMSCopy(item);

        this.compound = this.item.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
    }


    @Override
    public void setString(String key, String value) {
        compound.putString(key,value);
    }

    public void setTag(String key, CompoundTag compound){
        this.compound.put(key,compound);
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

    public CompoundTag getTag() {
        return compound;
    }

    @Override
    public org.bukkit.inventory.ItemStack getItem() {
        item.set(DataComponents.CUSTOM_DATA,CustomData.of(compound));
        return CraftItemStack.asBukkitCopy(item);
    }

    @Override
    public void removeKey(String key) {
        compound.remove(key);
    }
}
