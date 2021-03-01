package com.github.thedeathlycow.thimbleprotect.mixin;

import net.minecraft.block.AbstractBlock;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AbstractBlock.class)
public class AbstractBlockMixin {

//    @Inject(at = @At(value = "HEAD"), method = "onUse")
//    public void logUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
//        if (player != null) {
//            ThimbleInteractEvent event = new ThimbleInteractEvent(player, pos, world.getDimension(), world.getTime(), state.getBlock());
//            event.addToLog();
//        }
//    }
}


