package com.github.thedeathlycow.thimbleprotect.mixin;

import com.github.thedeathlycow.thimbleprotect.ThimbleProtect;
import com.github.thedeathlycow.thimbleprotect.events.ThimbleBlockUpdateEvent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.time.Instant;

@Mixin(Block.class)
public abstract class BlockBreakMixin {

    @Inject(at = @At("HEAD"), method = "onBreak")
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player, CallbackInfo ci) {
        if (player != null) {
            String dimensionName = world.getRegistryKey().getValue().toString();
            ThimbleBlockUpdateEvent event = new ThimbleBlockUpdateEvent(player.getDisplayName().asString(), pos, dimensionName, Instant.now().getEpochSecond(), ThimbleBlockUpdateEvent.ThimbleSubType.BLOCK_BREAK);
            event.setPreState(state);
            event.setPostState(Blocks.AIR.getDefaultState());

            if (ThimbleProtect.CONFIG.blockBreak) {
                event.addToLog();
            }
        }
    }

}
