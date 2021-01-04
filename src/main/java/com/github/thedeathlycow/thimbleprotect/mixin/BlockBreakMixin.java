package com.github.thedeathlycow.thimbleprotect.mixin;

import com.github.thedeathlycow.thimbleprotect.events.ThimbleBlockBreakEvent;
import com.github.thedeathlycow.thimbleprotect.events.ThimbleEvent;
import com.github.thedeathlycow.thimbleprotect.events.ThimbleExplosionEvent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public abstract class BlockBreakMixin {

    @Shadow
    public abstract BlockState getDefaultState();

    @Inject(at = @At("HEAD"), method = "onDestroyedByExplosion")
    public void onDestroyedByExplosion(World world, BlockPos pos, Explosion explosion, CallbackInfo ci) {

        BlockState state = this.getDefaultState();
        String name = "#explosion";


        if (explosion.getCausingEntity() != null) {
            name = explosion.getCausingEntity().getName().getString();
        }

        ThimbleEvent event = new ThimbleExplosionEvent(explosion.getCausingEntity(), pos, state);

    }

    @Inject(at = @At("HEAD"), method = "onBreak")
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player, CallbackInfo ci) {
        if (player != null) {
            ThimbleEvent event = new ThimbleBlockBreakEvent(player, pos, state);
        }
    }

}
