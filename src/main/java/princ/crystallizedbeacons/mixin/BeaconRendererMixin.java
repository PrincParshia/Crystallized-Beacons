package princ.crystallizedbeacons.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.blockentity.state.BeaconRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeaconBeamOwner;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import princ.crystallizedbeacons.util.EndCrystalAccessor;

import java.util.stream.Stream;

@Mixin(BeaconRenderer.class)
public class BeaconRendererMixin {
    @Unique
    private static final RenderStateDataKey<Boolean> HAS_BEAM_TARGET = RenderStateDataKey.create(() -> "hasBeamTarget");;

    @Inject(method = "extract", at = @At("HEAD"))
    private static <T extends BlockEntity & BeaconBeamOwner> void extract(T blockEntity, BeaconRenderState beaconRenderState, float f, Vec3 vec3, CallbackInfo callbackInfo) {
        Level level = blockEntity.getLevel();
        BlockPos blockPos = blockEntity.getBlockPos().above();
        AABB aABB = new AABB(blockPos);
        if (level != null) {
            Stream<EndCrystal> endCrystalStream = level.getEntitiesOfClass(EndCrystal.class, aABB).stream();
            boolean bl = endCrystalStream.anyMatch(endCrystal ->
                    ((EndCrystalAccessor) endCrystal).crystallizedBeacons$getTargetEntity() != null
            );
            beaconRenderState.setData(HAS_BEAM_TARGET, bl);
        }
    }

    @Inject(
            method = "submit(Lnet/minecraft/client/renderer/blockentity/state/BeaconRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    void submitCrystalBeam(BeaconRenderState beaconRenderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState, CallbackInfo callbackInfo) {
        if (Boolean.TRUE.equals(beaconRenderState.getData(HAS_BEAM_TARGET))) {
            callbackInfo.cancel();
        }
    }
}
