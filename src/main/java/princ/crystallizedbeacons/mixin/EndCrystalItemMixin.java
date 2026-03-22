package princ.crystallizedbeacons.mixin;

import net.minecraft.world.item.EndCrystalItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EndCrystalItem.class)
public class EndCrystalItemMixin {
    @Redirect(
            method = "useOn",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z"
            )
    )
    boolean useOn(BlockState blockState, Block block) {
        return blockState.is(block) || blockState.is(Blocks.BEACON);
    }
}
