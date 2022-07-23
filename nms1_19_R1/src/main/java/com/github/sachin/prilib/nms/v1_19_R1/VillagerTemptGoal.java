package com.github.sachin.prilib.nms.v1_19_R1;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.item.crafting.Ingredient;
import org.bukkit.permissions.Permission;

public class VillagerTemptGoal extends TemptGoal {

    private final TargetingConditions targetConditions;
    private final Ingredient items;

    private final PathfinderMob villager;
    private LivingEntity target;
    private static final TargetingConditions TEMP_TARGETING = TargetingConditions.forNonCombat().range(10.0).ignoreLineOfSight();

    public VillagerTemptGoal(PathfinderMob entitycreature, double d0, Ingredient recipeitemstack, Permission permission) {
        super(entitycreature, d0, recipeitemstack, false);
        this.villager = entitycreature;
        this.targetConditions = TEMP_TARGETING.copy().selector(this::shouldFollow);
        this.items = recipeitemstack;
    }

    @Override
    public boolean canUse() {
        this.target = this.villager.level.getNearestPlayer(targetConditions,this.villager);

        return super.canUse() && this.target != null && this.target.getBukkitEntity().hasPermission("dwellin.villagerfollowemerald");
    }

    private boolean shouldFollow(LivingEntity entityliving) {
        return this.items.test(entityliving.getMainHandItem()) || this.items.test(entityliving.getOffhandItem());
    }
}
