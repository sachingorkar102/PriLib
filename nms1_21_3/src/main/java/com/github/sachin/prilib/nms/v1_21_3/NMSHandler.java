package com.github.sachin.prilib.nms.v1_21_3;

import com.github.sachin.prilib.nms.AbstractNMSHandler;
import com.github.sachin.prilib.nms.NBTItem;
import com.github.sachin.prilib.utils.FastItemStack;
import com.github.sachin.prilib.utils.ItemUtils;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.GameTestAddMarkerDebugPayload;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.vehicle.MinecartChest;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.player.Input;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.v1_21_R2.CraftRegistry;
import org.bukkit.craftbukkit.v1_21_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_21_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_21_R2.entity.CraftPillager;
import org.bukkit.craftbukkit.v1_21_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_21_R2.entity.CraftVillager;
import org.bukkit.craftbukkit.v1_21_R2.inventory.CraftItemStack;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.loot.Lootable;
import org.bukkit.permissions.Permission;
import org.bukkit.persistence.PersistentDataType;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class NMSHandler extends AbstractNMSHandler {

    private static Constructor<?> resolvableProfileConst = null;

    static {
        try {
            Class<?> profile = Class.forName("net.minecraft.world.item.component.ResolvableProfile");
            resolvableProfileConst = profile.getDeclaredConstructor(GameProfile.class);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
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
            if(level().getDayTime() != checkTime) return;
            System.out.println("Checked");
            double chance = spawnChance;
        }
    }

    @Override
    public void applyHeadTexture(SkullMeta meta, String b64) {
        try {
            Field metaProfileField = meta.getClass().getDeclaredField("profile");
            metaProfileField.setAccessible(true);
            metaProfileField.set(meta, makeProfile(b64));
        } catch (NoSuchFieldException | IllegalAccessException | IllegalArgumentException ex2) {
            try {
                Field metaProfileField = meta.getClass().getDeclaredField("profile");
                metaProfileField.setAccessible(true);
                if (resolvableProfileConst != null) {
                    Object newProfile = resolvableProfileConst.newInstance(makeProfile(b64));
                    metaProfileField.set(meta, newProfile);
                }
            } catch (NoSuchFieldException | IllegalAccessException | InstantiationException | InvocationTargetException ex3) {
                ex3.printStackTrace();
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
    public void updateFollowGoal(Villager vil, boolean checkPermission) {

    }

    @Override
    public void addHoldBackCrossBowGoal(Pillager pil) {
        net.minecraft.world.entity.monster.Pillager nmsPill = ((CraftPillager)pil).getHandle();

        nmsPill.goalSelector.addGoal(1,new HoldBackCrossBowGoal(nmsPill,0.7));
    }

    @Override
    public void fill(Player player, Lootable lootTable, String lootTableKey, boolean resetSeed) {
        net.minecraft.world.entity.player.Player nmsPlayer = player != null ? ((CraftPlayer)player).getHandle() : null;


        ResourceLocation resourceLocation = ResourceLocation.parse(lootTableKey);

        if(lootTable instanceof BlockState){
            BlockState blockState = (BlockState) lootTable;
            Level level = ((CraftWorld)blockState.getWorld()).getHandle();
            BlockEntity blockEntity = level.getBlockEntity(new BlockPos(blockState.getX(),blockState.getY(),blockState.getZ()));

            if(blockEntity instanceof RandomizableContainerBlockEntity){
                RandomizableContainerBlockEntity lootableBlock = (RandomizableContainerBlockEntity) blockEntity;
                lootableBlock.setLootTable(ResourceKey.create(Registries.LOOT_TABLE,resourceLocation));
                if(nmsPlayer != null && !lootableBlock.canOpen(nmsPlayer)) return;
                if(resetSeed){

                    lootableBlock.setLootTableSeed(level.random.nextLong());
                }
                lootableBlock.unpackLootTable(nmsPlayer);
            }
            return;
        }
        MinecartChest minecart = (MinecartChest) ((CraftEntity)lootTable).getHandle();

        minecart.setContainerLootTable(ResourceKey.create(Registries.LOOT_TABLE,resourceLocation));
        if(resetSeed){
            minecart.setContainerLootTableSeed(minecart.level().random.nextLong());
        }
        minecart.unpackChestVehicleLootTable(nmsPlayer);
    }

//    @Override
//    public void fillLoot(Player player, Lootable lootTable) {
//        net.minecraft.world.entity.player.Player nmsPlayer = ((CraftPlayer)player).getHandle();
//        Level level = nmsPlayer.level();
//
//        if(lootTable instanceof BlockState){
//            BlockState blockState = (BlockState) lootTable;
//            BlockEntity blockEntity = level.getBlockEntity(new BlockPos(blockState.getX(),blockState.getY(),blockState.getZ()));
//            if(blockEntity instanceof RandomizableContainerBlockEntity){
//                RandomizableContainerBlockEntity lootableBlock = (RandomizableContainerBlockEntity) blockEntity;
//                if(lootableBlock.canOpen(nmsPlayer)){
//                    lootableBlock.setLootTableSeed(ThreadLocalRandom.current().nextLong());
//                    lootableBlock.unpackLootTable(nmsPlayer);
//                }
//            }
//            return;
//        }
//        MinecartChest minecart = (MinecartChest) ((CraftEntity)lootTable).getHandle();
//        minecart.unpackChestVehicleLootTable(nmsPlayer);
//    }

    @Override
    public Object getElytraUpdatePacket(Object handle, Entity itemFrame, NamespacedKey key) {

        ClientboundSetEntityDataPacket nmsPacket = (ClientboundSetEntityDataPacket) handle;
        for(SynchedEntityData.DataValue<?> item : nmsPacket.packedItems()){
            if(itemFrame.getPersistentDataContainer().has(key, PersistentDataType.INTEGER)){
                List<SynchedEntityData.DataValue<?>> list = new ArrayList<>();
                SynchedEntityData.DataValue<net.minecraft.world.item.ItemStack> newItem = new SynchedEntityData.DataValue<net.minecraft.world.item.ItemStack>(item.id(), ItemFrame.DATA_ITEM.serializer(),CraftItemStack.asNMSCopy(new ItemStack(Material.AIR)));
                list.add(newItem);
                return new ClientboundSetEntityDataPacket(nmsPacket.id(),list);
            }
        }
        return null;
    }

    @Override
    public void placeWater(Block block) {
        Location loc = block.getLocation();
        BlockPos blockPos = new BlockPos(loc.getBlockX(),loc.getBlockY(),loc.getBlockZ());
        ServerLevel level = ((CraftWorld) block.getWorld()).getHandle();
        net.minecraft.world.level.block.state.BlockState iBlockData = level.getBlockState(blockPos);
        net.minecraft.world.level.block.Block nmsBlock = iBlockData.getBlock();
        if(nmsBlock instanceof LiquidBlockContainer){
            ((LiquidBlockContainer)nmsBlock).placeLiquid(level,blockPos,iBlockData, Fluids.WATER.getSource(false));
        }
    }

    @Override
    public void attack(Player player, Entity target) {

        ((CraftPlayer)player).getHandle().attack(((CraftEntity)target).getHandle());
        ((CraftPlayer)player).getHandle().resetAttackStrengthTicker();
    }

    @Override
    public boolean placeItem(Player player, Location location, ItemStack item, BlockFace hitFace, String tweakName, boolean playSound) {
        net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        BlockPos pos = new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        ServerPlayer nmsPlayer = ((CraftPlayer)player).getHandle();

        UseOnContext context = new UseOnContext(nmsPlayer, InteractionHand.MAIN_HAND, new BlockHitResult(new Vec3(0.5F, 1F, 0.5F), Direction.UP, pos, false));
        InteractionResult res = nmsItem.useOn(context);
        if(res.consumesAction()){
            player.swingMainHand();
            BlockPos placedPos = context.getClickedPos().relative(context.getClickedFace());
            Block placedBlock = player.getWorld().getBlockAt(placedPos.getX(),placedPos.getY(),placedPos.getZ());
            if (placedBlock.getType()==Material.BARRIER){
                placedBlock.setType(Material.AIR);
            }
            if(playSound){
                player.getWorld().playSound(location, location.getBlock().getBlockData().getSoundGroup().getPlaceSound(), 1F, 1F);
            }
            return true;
        }
        return false;
    }

    @Override
    public void addFollowGoal(Villager vil, ItemStack[] temptItems, double speed, Permission permission,boolean checkPermission,boolean update) {
        net.minecraft.world.entity.npc.Villager nmsVil = ((CraftVillager)vil).getHandle();
        List<net.minecraft.world.item.ItemStack> nmsStackList = new ArrayList<>();
        List<Item> nmsMaterialList = new ArrayList<>();
        for(ItemStack item : temptItems){
            nmsStackList.add(CraftItemStack.asNMSCopy(item));
        }
        for(ItemStack item : temptItems){
            nmsMaterialList.add(CraftItemStack.asNMSCopy(item).getItem());
        }
        VillagerFollowEmeraldGoal newGoal = new VillagerFollowEmeraldGoal(nmsVil,speed,10,nmsMaterialList,checkPermission,permission==null ? null : permission.getName());
        VillagerTemptGoal oldGoal = new VillagerTemptGoal(nmsVil,speed, Ingredient.ofStacks(nmsStackList),checkPermission,permission==null ? null : permission.getName());
        if(update){
            nmsVil.goalSelector.removeGoal(oldGoal);
            nmsVil.goalSelector.removeGoal(newGoal);
        }
        nmsVil.goalSelector.addGoal(3,newGoal);
    }



    @Override
    public void spawnVillager(Villager villager,boolean update) {
        net.minecraft.world.entity.npc.Villager vil = (net.minecraft.world.entity.npc.Villager) ((CraftEntity)villager).getHandle();
        TemptGoal goal = new TemptGoal(vil, 0.6, Ingredient.of(Items.EMERALD_BLOCK), false);
        if(update){
            vil.goalSelector.removeGoal(goal);
        }
        vil.goalSelector.addGoal(2,goal);

    }

    @Override
    public void removeTemptGoal(Villager villager){
        net.minecraft.world.entity.npc.Villager vil = (net.minecraft.world.entity.npc.Villager) ((CraftEntity)villager).getHandle();
        TemptGoal goal = new TemptGoal(vil, 0.6, Ingredient.of(Items.EMERALD_BLOCK), false);
        vil.goalSelector.removeGoal(goal);

    }

    @Override
    public void removeTemptGoal(Villager vil, ItemStack[] temptItems, double speed, Permission permission,boolean checkPermission,boolean update) {
        net.minecraft.world.entity.npc.Villager nmsVil = ((CraftVillager)vil).getHandle();
        List<net.minecraft.world.item.ItemStack> nmsStackList = new ArrayList<>();

        for(ItemStack item : temptItems){
            nmsStackList.add(CraftItemStack.asNMSCopy(item));
        }
        String perm = null;
        if(permission != null) perm = permission.getName();
        VillagerTemptGoal goal = new VillagerTemptGoal(nmsVil,speed, Ingredient.ofStacks(nmsStackList),checkPermission,perm);
        nmsVil.goalSelector.removeGoal(goal);
    }

    @Override
    public void avoidPlayer(Entity entity, Player player, ConfigurationSection config, NamespacedKey key) {
        Animal animal = (Animal) ((CraftEntity)entity).getHandle();

        List<Animal> list = animal.level().getEntitiesOfClass(Animal.class,animal.getBoundingBox().inflate(5));
        animal.goalSelector.removeGoal(new PanicGoal(animal, 2.0));
        if(!list.isEmpty()){
            for (Animal en : list) {
                Entity bEn = en.getBukkitEntity();

                if(bEn.getType() == entity.getType()){
                    if(bEn.getPersistentDataContainer().has(key, PersistentDataType.INTEGER) && config.getBoolean("ignore-breeded")) continue;
                    FleePathFinder<ServerPlayer> goal = new FleePathFinder<ServerPlayer>(en, ServerPlayer.class, config.getInt("max-radius"), config.getDouble("walk-speed"), config.getDouble("sprint-speed"), (pl) -> pl.getUUID() == player.getUniqueId(), config.getInt("cooldown"));
                    en.goalSelector.removeGoal(goal);
                    en.goalSelector.addGoal(1, goal);
                }
            }
        }
    }

    @Override
    public ItemStack createMap(Location dist,byte zoom,boolean biomePreview){
        BlockPos loc = new BlockPos(dist.getBlockX(),dist.getBlockY(),dist.getBlockZ());
        ServerLevel level = ((CraftWorld) dist.getWorld()).getHandle();
        net.minecraft.world.item.ItemStack mapItem = MapItem.create(level,dist.getBlockX(),dist.getBlockZ(),zoom,true,true);
//        MapItemSavedData.addTargetDecoration(mapItem,loc,"+", MapDecoration.Type.RED_X);
        if(biomePreview){
            MapItem.renderBiomePreviewMap(level,mapItem);
        }
        return CraftItemStack.asBukkitCopy(mapItem);
    }

    @Override
    public boolean matchAxoltlVariant(Entity entity, String color) {
        return ((Axolotl)entity).getVariant().toString().equals(color);
    }

    @Override
    public boolean matchFrogVariant(Entity entity, String variant) {
        return ((Frog)entity).getVariant().toString().equals(variant);
    }

    @Override
    public boolean matchWolfVariant(Entity entity,String variant){
        return ((Wolf)entity).getVariant().toString().equalsIgnoreCase(variant);
    }

    @Override
    public boolean isScreamingGoat(Entity entity) {
        return ((Goat)entity).isScreaming();
    }

    @Override
    public List<Entity> getEntitiesWithinRadius(int radius, Entity center) {
        net.minecraft.world.entity.Entity entity = ((CraftEntity)center).getHandle();
        return entity.level().getEntities(entity,entity.getBoundingBox().inflate(radius)).stream().map(net.minecraft.world.entity.Entity::getBukkitEntity).collect(Collectors.toList());
    }


    @Override
    public Object getBlockHighlightPacket(Location loc, int color) {
        ClientboundCustomPayloadPacket nmsPacket = new ClientboundCustomPayloadPacket(new GameTestAddMarkerDebugPayload(new BlockPos(loc.getBlockX(),loc.getBlockY(),loc.getBlockZ()),color,"",200));

        return nmsPacket;
    }

    @Override
    public ItemStack setVillagerEgg(ItemStack item,Villager villager){
        net.minecraft.world.entity.npc.Villager nmsVillager = (net.minecraft.world.entity.npc.Villager) ((CraftEntity)villager).getHandle();
        CompoundTag compound = new CompoundTag();
        nmsVillager.addAdditionalSaveData(compound);
        if(villager.getCustomName() != null){
            compound.putString("dwellin-custom-name",villager.getCustomName());
        }
        ImplNBTItem nmsItem = new ImplNBTItem(item);
        nmsItem.setTag("dwellin-villager-data",compound);
        return nmsItem.getItem();
    }

    @Override
    public Villager getVillager(ItemStack item, World world,Location location){
        Villager villager = world.spawn(location,Villager.class);
        net.minecraft.world.entity.npc.Villager nmsVillager = (net.minecraft.world.entity.npc.Villager) ((CraftEntity)villager).getHandle();
        ImplNBTItem nmsItem = new ImplNBTItem(item);
        if(nmsItem.getTag().contains("dwellin-villager-data")){
            CompoundTag compound = nmsItem.getTag().getCompound("dwellin-villager-data");
            nmsVillager.readAdditionalSaveData(compound);
            if(compound.contains("dwellin-custom-name")){
                villager.setCustomName(compound.getString("dwellin-custom-name"));
            }
        }
        return villager;
    }

    @Override
    public boolean isPlayerJumping(Object inputObj) {
        Input input = (Input) inputObj;
        return input.jump();
    }

    @Override
    public boolean isPlayerHoldingShift(Object inputObj) {
        Input input = (Input) inputObj;
        return input.shift();
    }

    @Override
    public void triggerGameEvent(Player player, GameEvent gameEvent, Location location) {
        ServerLevel level = ((CraftWorld)location.getWorld()).getHandle();
        level.gameEvent(((CraftPlayer)player).getHandle(),CraftRegistry.bukkitToMinecraftHolder(gameEvent,Registries.GAME_EVENT),new BlockPos(location.getBlockX(),location.getBlockY(),location.getBlockZ()));
    }

    private static class FleePathFinder<T extends net.minecraft.world.entity.LivingEntity> extends AvoidEntityGoal<T> {

        private int cooldown;

        private final int finalCoolDown;


        public FleePathFinder(PathfinderMob entity, Class<T> avoider, float maxDis, double walkSpeedModifier, double sprintSpeedModifier,
                              Predicate<net.minecraft.world.entity.LivingEntity> condition, int cooldown) {
            super(entity, avoider, maxDis, walkSpeedModifier, sprintSpeedModifier, condition);
            this.finalCoolDown = cooldown*20;
            this.cooldown = finalCoolDown;
        }

        @Override
        public boolean canUse() {
            if(cooldown==0){
                return false;
            }
            else{
                cooldown--;
                return super.canUse();
            }
        }

        @Override
        public boolean canContinueToUse() {
            if(cooldown==0){
                this.cooldown = this.finalCoolDown;
            }
            if(cooldown==finalCoolDown){
                return false;
            }
            return super.canContinueToUse();
        }
    }
}
