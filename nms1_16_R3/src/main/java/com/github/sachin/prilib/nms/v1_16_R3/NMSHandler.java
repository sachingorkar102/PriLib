package com.github.sachin.prilib.nms.v1_16_R3;

import com.github.sachin.prilib.nms.AbstractNMSHandler;
import com.github.sachin.prilib.nms.NBTItem;
import com.github.sachin.prilib.utils.FastItemStack;
import com.github.sachin.prilib.utils.ItemUtils;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPillager;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftVillager;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
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
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class NMSHandler extends AbstractNMSHandler {
    @Override
    public NBTItem newItem(ItemStack item) {
        return new ImplNBTItem(item);
    }

    @Override
    public void restock(Villager vil) {
//        net.minecraft.world.entity.npc.Villager nmsVil = ((CraftVillager) vil).getHandle();
//        nmsVil.restock();
    }


    @Override
    public Entity spawnHelpWantedArmorstand(Location loc, ConfigurationSection config, float facing) {
//        HelpWantedArmorStand armorStand = new HelpWantedArmorStand(loc,config,facing);
//        ServerLevel level = ((CraftWorld)loc.getWorld()).getHandle();
//        level.addFreshEntity(armorStand);
//        return armorStand.getBukkitEntity();
        return null;
    }



//    check-time: 23000
//    villager-spawn-chance: 0.01
//    block-speedup-additional-chance: 0.01
//    block-check-radius: 3
//    temptation-blocks: [EMERALD_BLOCK]
//    private static class HelpWantedArmorStand extends ArmorStand {
//
//        private ConfigurationSection config;
//        private long checkTime;
//        private double speedUpChance;
//        private double spawnChance;
//        private int checkradius;
//        private List<Material> blocks = new ArrayList<>();
//
//        public HelpWantedArmorStand(Location loc,ConfigurationSection config,float facing) {
//            super(((CraftWorld)loc.getWorld()).getHandle(),loc.getX(),loc.getY(),loc.getZ());
//            this.setInvisible(true);
//
//            this.setInvulnerable(true);
//            this.setSmall(true);
//            this.setNoGravity(true);
//            this.setMarker(true);
//            this.setNoBasePlate(true);
//            this.setShowArms(false);
//            this.config = config;
//            this.checkTime = config.getLong("check-time");
//            this.speedUpChance = config.getDouble("block-speedup-additional-chance");
//            this.spawnChance = config.getDouble("villager-spawn-chance");
//            this.checkradius = config.getInt("block-check-radius");
//            this.blocks = ItemUtils.getMaterialsFromStrings(config.getStringList("temptation-blocks"),(mat) -> mat.isBlock());
//            FastItemStack head = new FastItemStack(Material.PLAYER_HEAD);
//            head.setHeadTexture(config.getString("head-texture"));
//            this.setItemSlot(EquipmentSlot.HEAD, CraftItemStack.asNMSCopy(head.get()));
//            this.setRot(facing,facing);
//        }
//
//        @Override
//        public void tick() {
//            super.tick();
//            if(level().getDayTime() != checkTime) return;
//            System.out.println("Checked");
//            double chance = spawnChance;
//        }
//    }

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
//        net.minecraft.world.entity.npc.Villager nmsVil = ((CraftVillager)vil).getHandle();
//        List<net.minecraft.world.item.ItemStack> nmsStackList = new ArrayList<>();
//
//        for(ItemStack item : temptItems){
//            nmsStackList.add(CraftItemStack.asNMSCopy(item));
//        }
//        VillagerTemptGoal goal = new VillagerTemptGoal(nmsVil,speed, Ingredient.of(nmsStackList.toArray(new net.minecraft.world.item.ItemStack[0])),checkPermission);
//        if(update){
//            nmsVil.goalSelector.removeGoal(goal);
//        }
//        nmsVil.goalSelector.addGoal(1,goal);
    }

    @Override
    public void updateFollowGoal(Villager vil, boolean checkPermission) {

    }

    @Override
    public void addHoldBackCrossBowGoal(Pillager pil) {
//        net.minecraft.world.entity.monster.Pillager nmsPill = ((CraftPillager)pil).getHandle();
//
//        nmsPill.goalSelector.addGoal(1,new HoldBackCrossBowGoal(nmsPill,0.7));
    }

//    @Override
//    public void fillLoot(Player player, Lootable lootTable) {
//        net.minecraft.server.v1_16_R3.EntityPlayer nmsPlayer = ((CraftPlayer)player).getHandle();
//        net.minecraft.server.v1_16_R3.World level = nmsPlayer.world;
//
//        if(lootTable instanceof BlockState){
//            BlockState blockState = (BlockState) lootTable;
//            TileEntity blockEntity = level.getTileEntity(new BlockPosition(blockState.getX(),blockState.getY(),blockState.getZ()));
//            if(blockEntity instanceof TileEntityLootable){
//                TileEntityLootable lootableBlock = (TileEntityLootable) blockEntity;
//                if(lootableBlock.e(nmsPlayer)){
//                    lootableBlock.d(nmsPlayer);
//                }
//            }
//            return;
//        }
//        EntityMinecartChest minecart = (EntityMinecartChest) ((CraftEntity)lootTable).getHandle();
//        minecart.d(nmsPlayer);
//    }

    @Override
    public Object getElytraUpdatePacket(Object handle, Entity itemFrame, NamespacedKey key) {
//        ClientboundSetEntityDataPacket nmsPacket = (ClientboundSetEntityDataPacket) handle;
//        for(SynchedEntityData.DataValue<?> item : nmsPacket.packedItems()){
//            if(itemFrame.getPersistentDataContainer().has(key, PersistentDataType.INTEGER)){
//                List<SynchedEntityData.DataValue<?>> list = new ArrayList<>();
//                SynchedEntityData.DataValue<net.minecraft.world.item.ItemStack> newItem = new SynchedEntityData.DataValue<net.minecraft.world.item.ItemStack>(item.id(), ItemFrame.DATA_ITEM.getSerializer(),CraftItemStack.asNMSCopy(new ItemStack(Material.AIR)));
//                list.add(newItem);
//                return new ClientboundSetEntityDataPacket(nmsPacket.id(),list);
//            }
//        }
        return null;

    }

    @Override
    public void placeWater(Block block) {
//        Location loc = block.getLocation();
//        BlockPos blockPos = new BlockPos(loc.getBlockX(),loc.getBlockY(),loc.getBlockZ());
//        ServerLevel level = ((CraftWorld) block.getWorld()).getHandle();
//        net.minecraft.world.level.block.state.BlockState iBlockData = level.getBlockState(blockPos);
//        net.minecraft.world.level.block.Block nmsBlock = iBlockData.getBlock();
//        if(nmsBlock instanceof LiquidBlockContainer){
//            ((LiquidBlockContainer)nmsBlock).placeLiquid(level,blockPos,iBlockData, Fluids.WATER.getSource(false));
//        }
    }

    @Override
    public void attack(Player player, Entity target) {
//
//        ((CraftPlayer)player).getHandle().attack(((CraftEntity)target).getHandle());
//        ((CraftPlayer)player).getHandle().resetAttackStrengthTicker();
    }

    @Override
    public boolean placeItem(Player player, Location location, ItemStack item, BlockFace hitFace, String tweakName, boolean playSound) {
//        net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
//        BlockPos pos = new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ());
//        ServerPlayer nmsPlayer = ((CraftPlayer)player).getHandle();
//
//        UseOnContext context = new UseOnContext(nmsPlayer, InteractionHand.MAIN_HAND, new BlockHitResult(new Vec3(0.5F, 1F, 0.5F), Direction.UP, pos, false));
//        InteractionResult res = nmsItem.useOn(context);
//        if(res==InteractionResult.CONSUME){
//            player.swingMainHand();
//            BlockPos placedPos = context.getClickedPos().relative(context.getClickedFace());
//            Block placedBlock = player.getWorld().getBlockAt(placedPos.getX(),placedPos.getY(),placedPos.getZ());
//            if (placedBlock.getType()==Material.BARRIER){
//                placedBlock.setType(Material.AIR);
//            }
//            if(playSound){
//                player.getWorld().playSound(location, location.getBlock().getBlockData().getSoundGroup().getPlaceSound(), 1F, 1F);
//            }
//            return true;
//        }
        return false;
    }



    @Override
    public void spawnVillager(Villager villager,boolean update) {
//        net.minecraft.world.entity.npc.Villager vil = (net.minecraft.world.entity.npc.Villager) ((CraftEntity)villager).getHandle();
//        TemptGoal goal = new TemptGoal(vil, 0.6, Ingredient.of(Items.EMERALD_BLOCK), false);
//        if(update){
//            vil.goalSelector.removeGoal(goal);
//        }
//        vil.goalSelector.addGoal(2,goal);

    }

    @Override
    public void avoidPlayer(Entity entity, Player player, ConfigurationSection config, NamespacedKey key) {
//        Animal animal = (Animal) ((CraftEntity)entity).getHandle();
//
//        List<Animal> list = animal.level().getEntitiesOfClass(Animal.class,animal.getBoundingBox().inflate(5));
//        animal.goalSelector.removeGoal(new PanicGoal(animal, 2.0));
//        if(!list.isEmpty()){
//            for (Animal en : list) {
//                Entity bEn = en.getBukkitEntity();
//
//                if(bEn.getType() == entity.getType()){
//                    if(bEn.getPersistentDataContainer().has(key, PersistentDataType.INTEGER) && config.getBoolean("ignore-breeded")) continue;
//                    FleePathFinder<ServerPlayer> goal = new FleePathFinder<ServerPlayer>(en, ServerPlayer.class, config.getInt("max-radius"), config.getDouble("walk-speed"), config.getDouble("sprint-speed"), (pl) -> pl.getUUID() == player.getUniqueId(), config.getInt("cooldown"));
//                    en.goalSelector.removeGoal(goal);
//                    en.goalSelector.addGoal(1, goal);
//                }
//            }
//        }
    }

    @Override
    public ItemStack createMap(Location dist,byte zoom,boolean biomePreview){
//        BlockPos loc = new BlockPos(dist.getBlockX(),dist.getBlockY(),dist.getBlockZ());
//        ServerLevel level = ((CraftWorld) dist.getWorld()).getHandle();
//        net.minecraft.world.item.ItemStack mapItem = MapItem.create(level,dist.getBlockX(),dist.getBlockZ(),zoom,true,true);
//        MapItemSavedData.addTargetDecoration(mapItem,loc,"+", MapDecoration.Type.RED_X);
//        if(biomePreview){
//            MapItem.renderBiomePreviewMap(level,mapItem);
//        }
//        return CraftItemStack.asBukkitCopy(mapItem);
        return null;
    }

    @Override
    public boolean matchAxoltlVariant(Entity entity, String color) {
//        return ((Axolotl)entity).getVariant().toString().equals(color);
        return false;
    }

    @Override
    public boolean matchFrogVariant(Entity entity, String variant) {
//        return ((Frog)entity).getVariant().toString().equals(variant);
        return false;
    }

    @Override
    public boolean isScreamingGoat(Entity entity) {
        return false;
    }

    @Override
    public List<Entity> getEntitiesWithinRadius(int radius, Entity center) {
        return null;
//        net.minecraft.world.entity.Entity entity = ((CraftEntity)center).getHandle();
//        return entity.level().getEntities(entity,entity.getBoundingBox().inflate(radius)).stream().map(net.minecraft.world.entity.Entity::getBukkitEntity).collect(Collectors.toList());
    }


    @Override
    public Object getBlockHighlightPacket(Location loc, int color) {
//        ClientboundCustomPayloadPacket nmsPacket = new ClientboundCustomPayloadPacket(new GameTestAddMarkerDebugPayload(new BlockPos(loc.getBlockX(),loc.getBlockY(),loc.getBlockZ()),color,"",200));
//        return nmsPacket;
        return null;
    }

    public ItemStack setVillagerEgg(ItemStack item,Villager villager){
//        net.minecraft.world.entity.npc.Villager nmsVillager = (net.minecraft.world.entity.npc.Villager) ((CraftEntity)villager).getHandle();
//        CompoundTag compound = new CompoundTag();
//        nmsVillager.addAdditionalSaveData(compound);
//        net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
//        nmsItem.getOrCreateTag().put("dwellin-villager-data",compound);
//        return CraftItemStack.asBukkitCopy(nmsItem);
        return null;
    }

    public Villager getVillagerEgg(ItemStack item, World world,Location location){
//        Villager villager = world.spawn(location,Villager.class);
//        net.minecraft.world.entity.npc.Villager nmsVillager = (net.minecraft.world.entity.npc.Villager) ((CraftEntity)villager).getHandle();
//        net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
//        nmsVillager.readAdditionalSaveData(nmsItem.getTagElement("dwellin-villager-data"));
//        return villager;
        return null;
    }



//    private static class FleePathFinder<T extends net.minecraft.world.entity.LivingEntity> extends AvoidEntityGoal<T> {
//
//        private int cooldown;
//
//        private final int finalCoolDown;
//
//
//        public FleePathFinder(PathfinderMob entity, Class<T> avoider, float maxDis, double walkSpeedModifier, double sprintSpeedModifier,
//                              Predicate<net.minecraft.world.entity.LivingEntity> condition, int cooldown) {
//            super(entity, avoider, maxDis, walkSpeedModifier, sprintSpeedModifier, condition);
//            this.finalCoolDown = cooldown*20;
//            this.cooldown = finalCoolDown;
//        }
//
//        @Override
//        public boolean canUse() {
//            if(cooldown==0){
//                return false;
//            }
//            else{
//                cooldown--;
//                return super.canUse();
//            }
//        }
//
//        @Override
//        public boolean canContinueToUse() {
//            if(cooldown==0){
//                this.cooldown = this.finalCoolDown;
//            }
//            if(cooldown==finalCoolDown){
//                return false;
//            }
//            return super.canContinueToUse();
//        }
//    }
}
