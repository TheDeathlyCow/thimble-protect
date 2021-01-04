package com.github.thedeathlycow.thimbleprotect.events;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class AffectedBlock {
    BlockPos pos;
    BlockState state;

    public AffectedBlock(BlockPos pos, BlockState state) {
        this.pos = pos;
        this.state = state;
    }
}
