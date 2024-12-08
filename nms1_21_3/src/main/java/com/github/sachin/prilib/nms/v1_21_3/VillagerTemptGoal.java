package com.github.sachin.prilib.nms.v1_21_3;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.animal.armadillo.Armadillo;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.crafting.Ingredient;
import org.bukkit.craftbukkit.v1_21_R2.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_21_R2.event.CraftEventFactory;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.UUID;

public class VillagerTemptGoal extends TemptGoal {

    private final TargetingConditions targetConditions;
    private final Ingredient items;

    private String perm=null;

    private final PathfinderMob villager;
    private final boolean checkPermission;
    private LivingEntity target;

    private static final TargetingConditions TEMP_TARGETING = TargetingConditions.forNonCombat().range(10.0).ignoreLineOfSight();

    public VillagerTemptGoal(PathfinderMob entitycreature, double d0, Ingredient recipeitemstack, boolean checkPermission) {
        super(entitycreature, d0, recipeitemstack, false);


        this.villager = entitycreature;
        this.targetConditions = TEMP_TARGETING.copy().selector(this::shouldFollow);
        this.items = recipeitemstack;
        this.checkPermission = checkPermission;

    }

    public VillagerTemptGoal(PathfinderMob entitycreature, double d0, Ingredient recipeitemstack, boolean checkPermission,String perm) {
        super(entitycreature, d0, recipeitemstack, false);



        this.villager = entitycreature;
        this.targetConditions = TEMP_TARGETING.copy().selector(this::shouldFollow);
        this.items = recipeitemstack;
        this.checkPermission = checkPermission;
        this.perm = perm;
    }



    private boolean shouldFollow(LivingEntity livingEntity, ServerLevel serverLevel) {

        return this.items.test(livingEntity.getItemBySlot(EquipmentSlot.MAINHAND)) || this.items.test(livingEntity.getItemBySlot(EquipmentSlot.OFFHAND));
    }



    @Override
    public boolean canUse() {
//        this.target = this.villager.level().getNearestPlayer(this.villager,10);
//        if(target != null){
//            return shouldFollow(target, this.villager.level().getMinecraftWorld());
//        }
//        return false;
        if(!checkPermission){
            return super.canUse();
        }
        else{
            this.target = this.villager.level().getNearestPlayer(this.villager,10);
            return super.canUse() && this.target != null && this.target.getBukkitEntity().hasPermission(this.perm);

        }
//
//        this.b = getServerLevel(this.mob).getNearestPlayer(this.targetingConditions.range(this.mob.getAttributeValue(Attributes.TEMPT_RANGE)), this.mob);
//        if (this.b != null) {
//            EntityTargetLivingEntityEvent event = CraftEventFactory.callEntityTargetLivingEvent(this.mob, this.b, EntityTargetEvent.TargetReason.TEMPT);
//            if (event.isCancelled()) {
//                return false;
//            }
//
//            this.b = event.getTarget() == null ? null : ((CraftLivingEntity)event.getTarget()).getHandle();
//        }
//
//        return this.b != null;

    }

}
