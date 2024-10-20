package com.github.sachin.prilib.utils;

import com.github.sachin.prilib.Prilib;
import org.bukkit.*;
import org.bukkit.block.banner.Pattern;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionData;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class FastItemStack {

    private ItemStack item;
    private ItemMeta meta;
    private final Prilib instance = Prilib.getInstance();


    public FastItemStack(ItemStack item){
        this.item = item;
        if(item == null || item.getType()== Material.AIR){
            return;
        }
        this.meta = item.getItemMeta();
    }

    public FastItemStack(Material material){
        this.item = new ItemStack(material);
        if(item == null || item.getType()==Material.AIR){
            return;
        }
        this.meta = item.getItemMeta();
    }


    public boolean isAir(){
        return item == null || item.getType()==Material.AIR;
    }

    public boolean is(Material mat){
        return !isAir() && item.getType()==mat;
    }

    public boolean hasDisplay(){
        return meta.hasDisplayName();
    }

    public boolean hasLore(){
        return meta.hasLore();
    }

    public int getEnchantmentLevel(Enchantment enchant){
        return meta.getEnchantLevel(enchant);
    }


    public FastItemStack setDisplay(String name){
        if(name != null){
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        }
        return this;
    }

    public FastItemStack replaceInDisplay(String target,String replacement){
        meta.setDisplayName(getDisplay().replace(target,replacement));
        return this;
    }

    public String getDisplay(){
        return meta.getDisplayName();
    }

    public FastItemStack setLore(List<String> lore){
        meta.setLore(lore.stream().map(s -> ChatColor.translateAlternateColorCodes('&', s)).collect(Collectors.toList()));
        return this;
    }

    public FastItemStack setLore(String... lore){
        meta.setLore(Arrays.asList(lore).stream().map(s -> ChatColor.translateAlternateColorCodes('&', s)).collect(Collectors.toList()));
        return this;
    }

    public FastItemStack replaceInLore(String target,String replacement){
        meta.setLore(getLore().stream().map(s -> s.replace(target,replacement)).collect(Collectors.toList()));
        return this;
    }
    public FastItemStack addLore(String... lore){
        if(meta.hasLore()){
            List<String> oldLore = meta.getLore();
            List<String> newLore = new ArrayList<>();
            for(String s : lore){
                newLore.add(ChatColor.translateAlternateColorCodes('&', s));
            }
            oldLore.addAll(newLore);
            meta.setLore(oldLore);
        }
        else{
            setLore(lore);
        }
        return this;
    }

    public List<String> getLore(){
        return meta.getLore();
    }

    public FastItemStack setCustomModelData(int data){
        meta.setCustomModelData(data);
        return this;
    }

    public int getCustomModelData(){
        return meta.getCustomModelData();
    }

    public FastItemStack setAmount(int amount){
        if(amount<0) return this;
        item.setAmount(amount);
        return this;
    }

    public int getAmount(){
        return item.getAmount();
    }

    public FastItemStack shrink(){
        item.setAmount(item.getAmount()-1);
        return this;
    }

    public FastItemStack setPotionData(PotionData data){
        if(meta instanceof PotionMeta){
            ((PotionMeta) meta).setBasePotionData(data);
        }
        return this;
    }

    public PotionData getPotionData(){
        if(meta instanceof PotionMeta){
            return ((PotionMeta) meta).getBasePotionData();
        }
        return null;
    }

    public FastItemStack setColor(Color color){
        if(meta instanceof LeatherArmorMeta){
            LeatherArmorMeta lMeta = (LeatherArmorMeta) meta;
            lMeta.setColor(color);
        }
        if(meta instanceof PotionMeta){
            PotionMeta pMeta = (PotionMeta) meta;
            pMeta.setColor(color);
        }
        return this;
    }



    public Color getColor(){
        if(meta instanceof LeatherArmorMeta){
            LeatherArmorMeta lMeta = (LeatherArmorMeta) meta;
            return lMeta.getColor();
        }
        if(meta instanceof PotionMeta){
            PotionMeta pMeta = (PotionMeta) meta;
            return pMeta.getColor();
        }
        return null;
    }

    public FastItemStack setFlag(ItemFlag...flags){
        meta.addItemFlags(flags);
        return this;
    }

    public FastItemStack removeFlag(ItemFlag ... flags){
        meta.removeItemFlags(flags);
        return this;
    }

    public FastItemStack setAllFlags(){
        meta.addItemFlags(ItemFlag.values());

        return this;
    }

    public boolean hasFlag(ItemFlag flag){
        return meta.hasItemFlag(flag);
    }

    public FastItemStack damage(int amount, @Nullable Player player){
        if(!(meta instanceof Damageable) || amount < 0) return this;
        int durability = item.getEnchantmentLevel(Enchantment.DURABILITY);
        int k = 0;
        for (int l = 0; durability > 0 && l < amount; l++) {
            if (ThreadLocalRandom.current().nextInt(durability +1) > 0){
                k++;
            }
        }
        amount -= k;
        if(player != null){
            PlayerItemDamageEvent damageEvent = new PlayerItemDamageEvent(player, item, amount);
            Bukkit.getServer().getPluginManager().callEvent(damageEvent);
            if(amount != damageEvent.getDamage() || damageEvent.isCancelled()){
                damageEvent.getPlayer().updateInventory();
            }
            else if(damageEvent.isCancelled()){
                return this;
            }
            amount = damageEvent.getDamage();

        }
        if (amount <= 0)
            return this;

        Damageable damageable = (Damageable) meta;
        damageable.setDamage(damageable.getDamage()+amount);
        return this;
    }

    public FastItemStack setUnbreakable(boolean unbreakable){
        meta.setUnbreakable(unbreakable);
        return this;
    }


    public FastItemStack setFireWorkEffect(FireworkEffect.Type type, List<Color> colors, List<Color> fadeColors, boolean trail, boolean flicker){
        if(meta instanceof FireworkEffectMeta){
            FireworkEffectMeta firework = (FireworkEffectMeta) meta;
            FireworkEffect.Builder builder = FireworkEffect.builder();
            firework.setEffect(builder.trail(trail).flicker(flicker).with(type).withColor(colors).withFade(fadeColors).build());
        }
        return this;
    }

    public FastItemStack setBannerPattern(int layer,Pattern pattern){
        if(meta instanceof BannerMeta){
            BannerMeta bMeta = (BannerMeta) meta;
            bMeta.setPattern(layer,pattern);
        }
        return this;
    }

    public FastItemStack setBannerPatterns(List<Pattern> patterns){
        if(meta instanceof BannerMeta){
            BannerMeta bMeta = (BannerMeta) meta;
            bMeta.setPatterns(patterns);
        }
        return this;
    }

    public FastItemStack setHeadTexture(String texture){
        if(meta instanceof SkullMeta){
            instance.getNmsHandler().applyHeadTexture((SkullMeta) meta,texture);
        }
        return this;
    }


    public int getDamage(){
        if(meta instanceof Damageable){
            return ((Damageable) meta).getDamage();
        }
        return 0;
    }

    public FastItemStack setDamage(int amount){
        if(meta instanceof Damageable){
            Damageable damageable = (Damageable) meta;
            damageable.setDamage(damageable.getDamage()+amount);
        }
        return this;
    }

    public int getMaxDurability(){
        return item.getType().getMaxDurability();
    }

    public <T,Z> boolean hasKey(String key, PersistentDataType<T,Z> type){
        return hasKey(instance.getKey(key), type);
    }

    public <T,Z> boolean hasKey(NamespacedKey key,PersistentDataType<T,Z> type){
        return meta.getPersistentDataContainer().has(key, type);
    }


    public <T,Z> FastItemStack set(String key,PersistentDataType<T,Z> type,Z value){
        return set(instance.getKey(key), type, value);
    }


    public <T,Z> FastItemStack set(NamespacedKey key,PersistentDataType<T,Z> type,Z value){
        meta.getPersistentDataContainer().set(key, type, value);
        return this;
    }

    public <T,Z> Z get(String key,PersistentDataType<T,Z> type){
        return get(instance.getKey(key), type);
    }


    public <T,Z> Z get(NamespacedKey key,PersistentDataType<T,Z> type){
        return meta.getPersistentDataContainer().get(key, type);
    }

    public <T,Z> Z getOrDefault(String key,PersistentDataType<T,Z> type,Z defaultValue){
        return getOrDefault(instance.getKey(key), type,defaultValue);
    }


    public <T,Z> Z getOrDefault(NamespacedKey key,PersistentDataType<T,Z> type,Z defaultValue){
        return meta.getPersistentDataContainer().getOrDefault(key, type,defaultValue);
    }


    public Set<NamespacedKey> getKeys(){
        return meta.getPersistentDataContainer().getKeys();
    }

    public FastItemStack clone(){
        if(isAir()){
            return this;
        }
        return new FastItemStack(item.clone());
    }



    @Override
    public String toString() {
        return item+"\nPersistentKeys: "+getKeys();
    }

    public Material getType(){
        return item.getType();
    }


    public ItemStack get(){
        if(!isAir()){
            item.setItemMeta(meta);
        }
        return item;
    }

    public FastItemStack setMeta(){
        item.setItemMeta(meta);
        return this;
    }
}
