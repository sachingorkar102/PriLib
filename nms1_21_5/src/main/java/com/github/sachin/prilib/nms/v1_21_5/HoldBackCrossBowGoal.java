package com.github.sachin.prilib.nms.v1_21_5;


import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

// Original Class: https://github.com/seymourimadeit/Big-Brain/blob/main/src/main/java/tallestegg/bigbrain/entity/ai/goals/RunWhileChargingGoal.java
public class HoldBackCrossBowGoal extends RandomStrollGoal {

    public HoldBackCrossBowGoal(PathfinderMob creatureIn, double speedIn) {
        super(creatureIn, speedIn);
    }

    @Override
    public boolean canUse() {
        return  mob.getUseItem().getItem() instanceof CrossbowItem
                && mob.getTarget() != null && !CrossbowItem.isCharged(mob.getUseItem())
//                && EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MULTISHOT, this.mob.getMainHandItem()) == 0
                && this.findPosition();
    }

    public boolean findPosition() {
        Vec3 vector3d = this.getPosition();
        if (vector3d == null) {
            return false;
        } else {
            this.wantedX = vector3d.x;
            this.wantedY = vector3d.y;
            this.wantedZ = vector3d.z;
            return true;
        }
    }

    @Override
    public void start() {
        super.start();
        this.mob.startUsingItem(InteractionHand.MAIN_HAND);
        ((Pillager) mob).setChargingCrossbow(true);
        if (mob.getTarget() != null) {
            mob.lookAt(mob.getTarget(), 30.0F, 30.0F);
            mob.getLookControl().setLookAt(mob.getTarget(), 30.0F, 30.0F);
        }
    }

    @Override
    public void tick() {
        int i = this.mob.getTicksUsingItem();
        ItemStack itemstack = this.mob.getUseItem();
        if (i >= CrossbowItem.getChargeDuration(itemstack,this.mob)) {
            this.mob.releaseUsingItem();
            ((Pillager) mob).setChargingCrossbow(false);
        }
    }

    @Override
    public void stop() {
        super.stop();
        this.mob.releaseUsingItem();
        ((Pillager) mob).setChargingCrossbow(false);
    }

    @Override
    public boolean canContinueToUse() {
        return !CrossbowItem.isCharged(mob.getUseItem())
                && mob.getUseItem().getItem() instanceof CrossbowItem && !this.mob.isVehicle();
    }

    @Override
    protected Vec3 getPosition() {
        return DefaultRandomPos.getPosAway(this.mob, 16, 7, this.mob.getTarget().position());
    }
}
