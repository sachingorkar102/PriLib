package com.github.sachin.prilib.nms.v1_19_R2;

import com.github.sachin.prilib.nms.AbstractNMSHandler;
import com.github.sachin.prilib.nms.NBTItem;
import com.github.sachin.prilib.utils.FastItemStack;
import com.github.sachin.prilib.utils.ItemUtils;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.vehicle.MinecartChest;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.v1_19_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftPillager;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftVillager;
import org.bukkit.craftbukkit.v1_19_R2.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Pillager;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.loot.Lootable;
import org.bukkit.permissions.Permission;
import org.bukkit.persistence.PersistentDataType;

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
    public void addFollowGoal(Villager vil, ItemStack[] temptItems, double speed, Permission permission,boolean checkPermission,boolean update) {
        net.minecraft.world.entity.npc.Villager nmsVil = ((CraftVillager)vil).getHandle();
        List<net.minecraft.world.item.ItemStack> nmsStackList = new ArrayList<>();

        for(ItemStack item : temptItems){
            nmsStackList.add(CraftItemStack.asNMSCopy(item));
        }
        VillagerTemptGoal goal = new VillagerTemptGoal(nmsVil,speed, Ingredient.of(nmsStackList.toArray(new net.minecraft.world.item.ItemStack[0])),checkPermission);
        if(update){
            nmsVil.goalSelector.removeGoal(goal);
        }
        nmsVil.goalSelector.addGoal(1,goal);
    }

    @Override
    public void updateFollowGoal(Villager vil, boolean checkPermission) {

    }

    @Override
    public void addHoldBackCrossBowGoal(Pillager pil) {
        net.minecraft.world.entity.monster.Pillager nmsPill = ((CraftPillager)pil).getHandle();

        nmsPill.goalSelector.addGoal(1,new HoldBackCrossBowGoal(nmsPill,0.7));
    }

    @Override
    public void fillLoot(Player player, Lootable lootTable) {
        net.minecraft.world.entity.player.Player nmsPlayer = ((CraftPlayer)player).getHandle();
        Level level = nmsPlayer.getLevel();

        if(lootTable instanceof BlockState){
            BlockState blockState = (BlockState) lootTable;
            RandomizableContainerBlockEntity lootableBlock = (RandomizableContainerBlockEntity) level.getBlockEntity(new BlockPos(blockState.getX(),blockState.getY(),blockState.getZ()));
            lootableBlock.unpackLootTable(nmsPlayer);
            return;
        }
        MinecartChest minecart = (MinecartChest) ((CraftEntity)lootTable).getHandle();
        minecart.unpackChestVehicleLootTable(nmsPlayer);
    }

    @Override
    public Object getElytraUpdatePacket(Object handle, Entity itemframe, NamespacedKey key) {
        ClientboundSetEntityDataPacket nmsPacket = (ClientboundSetEntityDataPacket) handle;
        for(SynchedEntityData.DataValue<?> item : nmsPacket.packedItems()){
            if(itemframe.getPersistentDataContainer().has(key, PersistentDataType.INTEGER)){
                List<SynchedEntityData.DataValue<?>> list = new ArrayList<>();
                SynchedEntityData.DataValue<net.minecraft.world.item.ItemStack> newItem = new SynchedEntityData.DataValue<net.minecraft.world.item.ItemStack>(item.id(), ItemFrame.DATA_ITEM.getSerializer(),CraftItemStack.asNMSCopy(new ItemStack(Material.AIR)));
                list.add(newItem);
                return new ClientboundSetEntityDataPacket(nmsPacket.id(),list);
            }
        }
        return null;
    }
}
