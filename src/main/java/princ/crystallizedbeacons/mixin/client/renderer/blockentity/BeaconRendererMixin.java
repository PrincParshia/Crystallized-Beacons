package princ.crystallizedbeacons.mixin.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import princ.crystallizedbeacons.util.world.entity.boss.enderdragon.EndCrystalAccessor;

import java.util.List;

@Mixin(BeaconRenderer.class)
public class BeaconRendererMixin {
    @Inject(
            method = "render(Lnet/minecraft/world/level/block/entity/BeaconBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V",
            at = @At("HEAD"),
            cancellable = true
    )
    void render(BeaconBlockEntity beaconBlockEntity, float f, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j, CallbackInfo callbackInfo) {
        Level level = beaconBlockEntity.getLevel();
        BlockPos blockPos = beaconBlockEntity.getBlockPos().above();
        AABB aABB = new AABB(blockPos);

        if (level != null) {
            List<EndCrystal> endCrystals = level.getEntitiesOfClass(EndCrystal.class, aABB);
            boolean bl = endCrystals.stream().anyMatch(endCrystal -> ((EndCrystalAccessor) endCrystal).crystallizedBeacons$getBeamTarget() != null);

            if (bl) {
                callbackInfo.cancel();
            }
        }
    }
}
