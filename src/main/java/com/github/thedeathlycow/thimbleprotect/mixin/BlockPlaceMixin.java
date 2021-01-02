package com.github.thedeathlycow.thimbleprotect.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.FireBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public class BlockPlaceMixin {

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemPlacementContext;getBlockPos()Lnet/minecraft/util/math/BlockPos;"), method = "place")
    public void place(ItemPlacementContext ctx, CallbackInfoReturnable<Boolean> info) {
        if (ctx.getPlayer() != null) {
            BlockState state = ctx.getWorld().getBlockState(ctx.getBlockPos());
            PlayerEntity player = ctx.getPlayer();
            BlockPos pos = ctx.getBlockPos();
            System.out.println(player.getDisplayName().asString() + " placed a " + state.getBlock() + " at " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ());
        }

    }
}
