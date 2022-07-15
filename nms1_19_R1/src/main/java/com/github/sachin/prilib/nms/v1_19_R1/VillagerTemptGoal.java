package com.github.sachin.prilib.nms.v1_19_R1;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.item.crafting.Ingredient;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_19_R1.event.CraftEventFactory;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.permissions.Permission;

public class VillagerTemptGoal extends TemptGoal {

    public VillagerTemptGoal(PathfinderMob entitycreature, double d0, Ingredient recipeitemstack, Permission permission) {
        super(entitycreature, d0, recipeitemstack, false);
    }

    @Override
    public boolean canUse() {
        return super.canUse() && this.b != null && this.b.getBukkitEntity().hasPermission("dwellin.villagerfollowemerald");
    }
}
