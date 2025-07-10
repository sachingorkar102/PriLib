package com.github.sachin.prilib.nms.v1_21_5;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.item.crafting.Ingredient;

// NOT IN USE ANYMORE
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
