package com.github.sachin.prilib.nms.v1_21_4;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class VillagerFollowEmeraldGoal extends Goal {
    private final Villager villager;

    private final double speed;

    private final int followRange;

    private Player nearestPlayer;

    private List<Item> temptItems;

    private boolean checkPerm;
    private String perm;

    public VillagerFollowEmeraldGoal(Villager villager, double speed, int followRange, List<Item> temptItems, boolean checkPerm, String perm) {
        this.villager = villager;
        this.speed = speed;
        this.followRange = followRange;
        this.temptItems = temptItems;
        this.checkPerm = checkPerm;
        this.perm = perm;
        if(perm == null) this.checkPerm = false;
    }

    public boolean canUse() {
        this.nearestPlayer = findNearestPlayerHoldingEmerald();
        return (this.nearestPlayer != null);
    }

    public void tick() {
        if (this.nearestPlayer != null)
            this.villager.getNavigation().moveTo((Entity)this.nearestPlayer, this.speed);
    }

    private Player findNearestPlayerHoldingEmerald() {
        return this.villager.level().players().stream()
                .filter(player -> (player.distanceTo((Entity)this.villager) <= this.followRange))
                .filter(player -> isHoldingTemptItem(player))
                .filter(player -> !checkPerm || player.getBukkitEntity().hasPermission(perm))
                .findFirst()
                .orElse(null);
    }

    private boolean isHoldingTemptItem(Player player) {
        ItemStack heldItem = player.getMainHandItem();
        for(Item item : temptItems){
            if(heldItem.is(item)) return true;
        }
        return false;
    }
}
