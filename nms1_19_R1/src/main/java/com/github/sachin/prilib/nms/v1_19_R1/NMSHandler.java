package com.github.sachin.prilib.nms.v1_19_R1;

import com.github.sachin.prilib.nms.NBTItem;
import com.github.sachin.prilib.nms.AbstractNMSHandler;
import com.github.sachin.prilib.utils.FastItemStack;
import com.github.sachin.prilib.utils.ItemUtils;
import com.github.sachin.prilib.utils.RandomUtils;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.goal.RangedCrossbowAttackGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPillager;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftVillager;
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Pillager;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.permissions.Permission;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NMSHandler extends AbstractNMSHandler {
    @Override
    public NBTItem newItem(ItemStack item) {
        return new ImplNBTItem(item);
    }

    @Override
    public void restock(Villager vil) {
        net.minecraft.world.entity.npc.Villager nmsVil = ((CraftVillager) vil).getHandle();
        nmsVil.restock();
    }


    @Override
    public Entity spawnHelpWantedArmorstand(Location loc, ConfigurationSection config, float facing) {
        HelpWantedArmorStand armorStand = new HelpWantedArmorStand(loc,config,facing);
        ServerLevel level = ((CraftWorld)loc.getWorld()).getHandle();
        level.addFreshEntity(armorStand);
        return armorStand.getBukkitEntity();

    }



//    check-time: 23000
//    villager-spawn-chance: 0.01
//    block-speedup-additional-chance: 0.01
//    block-check-radius: 3
//    temptation-blocks: [EMERALD_BLOCK]
    private static class HelpWantedArmorStand extends ArmorStand {

        private ConfigurationSection config;
        private long checkTime;
        private double speedUpChance;
        private double spawnChance;
        private int checkradius;
        private List<Material> blocks = new ArrayList<>();

        public HelpWantedArmorStand(Location loc,ConfigurationSection config,float facing) {
            super(((CraftWorld)loc.getWorld()).getHandle(),loc.getX(),loc.getY(),loc.getZ());
            this.setInvisible(true);

            this.setInvulnerable(true);
            this.setSmall(true);
            this.setNoGravity(true);
            this.setMarker(true);
            this.setNoBasePlate(true);
            this.setShowArms(false);
            this.config = config;
            this.checkTime = config.getLong("check-time");
            this.speedUpChance = config.getDouble("block-speedup-additional-chance");
            this.spawnChance = config.getDouble("villager-spawn-chance");
            this.checkradius = config.getInt("block-check-radius");
            this.blocks = ItemUtils.getMaterialsFromStrings(config.getStringList("temptation-blocks"),(mat) -> mat.isBlock());
            FastItemStack head = new FastItemStack(Material.PLAYER_HEAD);
            head.setHeadTexture(config.getString("head-texture"));
            this.setItemSlot(EquipmentSlot.HEAD, CraftItemStack.asNMSCopy(head.get()));
            this.setRot(facing,facing);
        }

        @Override
        public void tick() {
            super.tick();
            if(getLevel().getDayTime() != checkTime) return;
            System.out.println("Checked");
            double chance = spawnChance;
        }
    }

    @Override
    public void applyHeadTexture(SkullMeta meta, String b64) {
        Field metaProfileField ;
        Method metaSetProfileMethod;
        try {
            metaSetProfileMethod = meta.getClass().getDeclaredMethod("setProfile", GameProfile.class);
            metaSetProfileMethod.setAccessible(true);

            metaSetProfileMethod.invoke(meta, makeProfile(b64));
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
            try {
                metaProfileField = meta.getClass().getDeclaredField("profile");
                metaProfileField.setAccessible(true);

                metaProfileField.set(meta, makeProfile(b64));

            } catch (NoSuchFieldException | IllegalAccessException ex2) {
                ex2.printStackTrace();
            }
        }
    }

    public GameProfile makeProfile(String b64) {
        UUID id = new UUID(b64.substring(b64.length() - 20).hashCode(),b64.substring(b64.length() - 10).hashCode());
        GameProfile profile = new GameProfile(id, "someName");
        profile.getProperties().put("textures", new Property("textures", b64));
        return profile;
    }

    @Override
    public void addFollowGoal(Villager vil, ItemStack[] temptItems, double speed, Permission permission) {
        net.minecraft.world.entity.npc.Villager nmsVil = ((CraftVillager)vil).getHandle();
        List<net.minecraft.world.item.ItemStack> nmsStackList = new ArrayList<>();

        for(ItemStack item : temptItems){
            nmsStackList.add(CraftItemStack.asNMSCopy(item));
        }
        VillagerTemptGoal goal = new VillagerTemptGoal(nmsVil,speed, Ingredient.of(nmsStackList.toArray(new net.minecraft.world.item.ItemStack[0])),permission);
        nmsVil.goalSelector.addGoal(1,goal);
    }

    @Override
    public void addHoldBackCrossBowGoal(Pillager pil) {
        net.minecraft.world.entity.monster.Pillager nmsPill = ((CraftPillager)pil).getHandle();

        nmsPill.goalSelector.addGoal(1,new HoldBackCrossBowGoal(nmsPill,0.7));
    }
}
