package com.github.thedeathlycow.thimbleprotect.mixin;

import com.github.thedeathlycow.thimbleprotect.ThimbleProtect;
import com.github.thedeathlycow.thimbleprotect.events.ThimbleBlockUpdateEvent;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.time.Instant;

@Mixin(BlockItem.class)
public abstract class BlockPlaceMixin {

    @Shadow
    protected @Nullable
    abstract BlockState getPlacementState(ItemPlacementContext context);

    @Inject(at = @At(value = "HEAD"), method = "place(Lnet/minecraft/item/ItemPlacementContext;)Lnet/minecraft/util/ActionResult;")
    public void place(ItemPlacementContext ctx, CallbackInfoReturnable<Boolean> info) {
        if (ctx.getPlayer() != null) {
            BlockState preState = ctx.getWorld().getBlockState(ctx.getBlockPos());
            BlockState postState = this.getPlacementState(ctx);
            PlayerEntity player = ctx.getPlayer();
            BlockPos pos = ctx.getBlockPos();
            World world = ctx.getWorld();
            String dimensionName = world.getRegistryKey().getValue().toString();

            ThimbleBlockUpdateEvent event = new ThimbleBlockUpdateEvent(player.getUuidAsString(), pos, dimensionName, Instant.now().getEpochSecond(), ThimbleBlockUpdateEvent.ThimbleSubType.BLOCK_PLACE);
            event.setPreState(preState);
            event.setPostState(postState);

            if (ThimbleProtect.CONFIG.blockPlace) {
                event.addToLog();
            }
        }
    }
}
