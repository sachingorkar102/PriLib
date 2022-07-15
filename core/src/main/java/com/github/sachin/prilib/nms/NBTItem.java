package com.github.sachin.prilib.nms;

import org.bukkit.inventory.ItemStack;

public abstract class NBTItem {

    public abstract void setString(String key,String value);
    public abstract void setBoolean(String key,boolean value);
    public abstract void setInt(String key,int value);
    public abstract void setLong(String key,long value);
    public abstract void setDouble(String key,double value);

    public abstract String getString(String key);
    public abstract boolean getBoolean(String key);
    public abstract int getInt(String key);
    public abstract long getLong(String key);
    public abstract double getDouble(String key);

    public abstract boolean hasKey(String key);
    public abstract ItemStack getItem();
    public abstract void removeKey(String key);
}
