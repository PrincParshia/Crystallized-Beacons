package princ.crystallizedbeacons.mixin.client.renderer.entity;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EndCrystalRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import princ.crystallizedbeacons.util.world.entity.boss.enderdragon.EndCrystalAccessor;

import static net.minecraft.client.renderer.blockentity.BeaconRenderer.*;
import static princ.crystallizedbeacons.CrystallizedBeaconsConstants.withDefaultNamespace;

@Mixin(EndCrystalRenderer.class)
public class EndCrystalRendererMixin {
    @Unique
    private static final AttachmentType<Vector3f> BEAM_TARGET = AttachmentRegistry.create(withDefaultNamespace("beam_target"));

    @ModifyExpressionValue(
            method = "render(Lnet/minecraft/world/entity/boss/enderdragon/EndCrystal;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/boss/enderdragon/EndCrystal;getBeamTarget()Lnet/minecraft/core/BlockPos;"
            )
    )
    private static BlockPos render(BlockPos blockPos, EndCrystal endCrystal, float f, float g, PoseStack poseStack, MultiBufferSource multiBufferSource, int i) {
        Vector3f vector3f = endCrystal.getAttached(BEAM_TARGET);
        if (vector3f != null) {
            float y = -vector3f.y + EndCrystalRenderer.getY(endCrystal, g);
            poseStack.pushPose();
            poseStack.translate(vector3f.x, vector3f.y, vector3f.z);
            renderCrystalBeams(-vector3f.x, y, -vector3f.z, g, poseStack, multiBufferSource);
            poseStack.popPose();
            return null;
        }
        return blockPos;
    }

    @Inject(
            method = "render(Lnet/minecraft/world/entity/boss/enderdragon/EndCrystal;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("TAIL")
    )
    void render(EndCrystal endCrystal, float f, float g, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, CallbackInfo callbackInfo) {
        Entity entity = ((EndCrystalAccessor) endCrystal).crystallizedBeacons$getBeamTarget();

        if (entity != null) {
            endCrystal.setAttached(BEAM_TARGET, entity.getPosition(f)
                    .subtract(0, 2, 0)
                    .add(0, entity.getBbHeight() / 2, 0)
                    .subtract(endCrystal.getPosition(f))
                    .toVector3f()
            );
        } else {
            endCrystal.setAttached(BEAM_TARGET, null);
        }
    }

    @ModifyExpressionValue(
            method = "shouldRender(Lnet/minecraft/world/entity/boss/enderdragon/EndCrystal;Lnet/minecraft/client/renderer/culling/Frustum;DDD)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/EntityRenderer;shouldRender(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/client/renderer/culling/Frustum;DDD)Z"
            )
    )
    boolean shouldRender(boolean bl, EndCrystal endCrystal) {
        return bl || ((EndCrystalAccessor) endCrystal).crystallizedBeacons$getBeamTarget() != null;
    }

    @Unique
    private static void renderCrystalBeams(float f, float g, float h, float i, PoseStack poseStack, MultiBufferSource multiBufferSource) {
        renderCrystalBeams(f, g, h, i, poseStack, multiBufferSource, BEAM_LOCATION, -1, 0.2F - 0.06F, 0.25F);
    }

    @Unique
    private static void renderCrystalBeams(float f, float g, float h, float i, PoseStack poseStack, MultiBufferSource multiBufferSource, ResourceLocation resourceLocation, int j, float p, float q) {
        float k = Mth.sqrt(f * f + h * h);
        float l = Mth.sqrt(f * f + g * g + h * h);
        poseStack.pushPose();
        poseStack.translate(0.0F, 2.0F, 0.0F);
        poseStack.mulPose(Axis.YP.rotation((float) (-Math.atan2(h, f)) - ((float) Math.PI / 2F)));
        poseStack.mulPose(Axis.XP.rotation((float) (-Math.atan2(k, g)) - ((float) Math.PI / 2F)));

        float n = -i;
        float o = Mth.frac(n * 0.2F - (float) Mth.floor(n * 0.1F));
        float z = -1.0F + o;
        float aa = l * 1.8F + z;

        float s = 0.0F;
        float t = l;

        renderPart(poseStack, multiBufferSource.getBuffer(RenderType.beaconBeam(resourceLocation, false)), p, j, s, t, 0.0F, 1.0F, z, aa);
        renderPart(poseStack, multiBufferSource.getBuffer(RenderType.beaconBeam(resourceLocation, true)), q, FastColor.ARGB32.color(32, j), s, t, 0.0F, 1.0F, z, aa);
        poseStack.popPose();
    }

    @Unique
    private static void renderPart(PoseStack poseStack, VertexConsumer vertexConsumer, float i, int j, float k, float f, float g, float h, float l, float m) {
        PoseStack.Pose pose = poseStack.last();
        renderQuad(pose, vertexConsumer, j, -i, -i, i, -i, k, f, g, h, l, m);
        renderQuad(pose, vertexConsumer, j, i, -i, i, i, k, f, g, h, l, m);
        renderQuad(pose, vertexConsumer, j, i, i, -i, i, k, f, g, h, l, m);
        renderQuad(pose, vertexConsumer, j, -i, i, -i, -i, k, f, g, h, l, m);
    }

    @Unique
    private static void renderQuad(PoseStack.Pose pose, VertexConsumer vertexConsumer, int i, float j, float k, float f, float g, float h, float l, float m, float n, float o, float p) {
        addVertex(pose, vertexConsumer, i, j, k, h, n, o);
        addVertex(pose, vertexConsumer, i, j, k, l, n, p);
        addVertex(pose, vertexConsumer, i, f, g, l, m, p);
        addVertex(pose, vertexConsumer, i, f, g, h, m, o);
    }

    @Unique
    private static void addVertex(PoseStack.Pose pose, VertexConsumer vertexConsumer, int i, float j, float f, float g, float h, float k) {
        vertexConsumer.addVertex(pose, f, j, g).setColor(i).setUv(h, k).setOverlay(OverlayTexture.NO_OVERLAY).setLight(15728880).setNormal(pose, 0.0F, 1.0F, 0.0F);
    }
}
