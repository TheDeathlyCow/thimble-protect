package com.github.thedeathlycow.thimbleprotect.mixin;

import com.github.thedeathlycow.thimbleprotect.ThimbleProtect;
import com.github.thedeathlycow.thimbleprotect.events.ThimbleBlockUpdateEvent;
import com.github.thedeathlycow.thimbleprotect.events.ThimbleEvent;
import com.github.thedeathlycow.thimbleprotect.events.ThimbleExplosionEvent;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.time.Instant;
import java.util.List;

@Mixin(Explosion.class)
public abstract class ExplosionMixin {


    @Shadow
    @Final
    private World world;
    @Shadow
    @Final
    private @Nullable Entity entity;

    @Shadow
    public abstract List<BlockPos> getAffectedBlocks();

    @Inject(at = @At("HEAD"), method = "affectWorld")
    public void affectWorld(boolean bl, CallbackInfo ci) {
        List<BlockPos> affectedBlockPositions = this.getAffectedBlocks();

        if (ThimbleProtect.CONFIG.explosion) {
            for (BlockPos currPos : affectedBlockPositions) {
                BlockState currState = this.world.getBlockState(currPos);
                String dimensionName = world.getRegistryKey().getValue().toString();
                if (!currState.isAir()) {
                    String entityUUID = ThimbleBlockUpdateEvent.NULL_ENTITY_STRING;
                    if (this.entity != null) {
                        entityUUID = this.entity.getUuidAsString();
                    }
                    ThimbleExplosionEvent event = new ThimbleExplosionEvent(entityUUID, currPos, dimensionName, Instant.now().getEpochSecond(), currState);
                    event.addToLog();
                }
            }
        }
    }
}
