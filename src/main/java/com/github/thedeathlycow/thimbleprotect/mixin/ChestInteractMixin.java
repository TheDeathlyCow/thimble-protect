package com.github.thedeathlycow.thimbleprotect.mixin;

import net.minecraft.block.ChestBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChestBlock.class)
public class ChestInteractMixin {

    @Shadow @Final private DefaultedList<ItemStack> trackedStacks;

    @Inject(at = @At("TAIL"), method = "onContentChanged")
    public void onContentChanged(CallbackInfo ci) {
        for (ItemStack changedStack : this.trackedStacks) {
            System.out.println("Changed " + changedStack.toString());
        }
    }

}
