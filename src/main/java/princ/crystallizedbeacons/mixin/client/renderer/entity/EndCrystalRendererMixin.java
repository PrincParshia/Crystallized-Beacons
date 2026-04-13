package princ.crystallizedbeacons.mixin.client.renderer.entity;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EndCrystalRenderer;
import net.minecraft.client.renderer.entity.state.EndCrystalRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import princ.crystallizedbeacons.util.client.renderer.entity.state.EndCrystalRendererStateAccessor;
import princ.crystallizedbeacons.util.world.entity.boss.enderdragon.EndCrystalAccessor;

import static net.minecraft.client.renderer.blockentity.BeaconRenderer.*;

@Mixin(EndCrystalRenderer.class)
public class EndCrystalRendererMixin {
    @ModifyExpressionValue(
            method = "render(Lnet/minecraft/client/renderer/entity/state/EndCrystalRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/renderer/entity/state/EndCrystalRenderState;beamOffset:Lnet/minecraft/world/phys/Vec3;",
                    opcode = Opcodes.GETFIELD
            )
    )
    private static Vec3 render(Vec3 vec3, EndCrystalRenderState endCrystalRenderState, PoseStack poseStack, MultiBufferSource multiBufferSource, int i) {
        Vector3f vector3f = ((EndCrystalRendererStateAccessor) endCrystalRenderState).crystallizedBeacons$getBeamTarget();
        if (vector3f != null) {
            float g = endCrystalRenderState.ageInTicks;
            float y = -vector3f.y + EndCrystalRenderer.getY(g);
            poseStack.pushPose();
            poseStack.translate(vector3f.x, vector3f.y, vector3f.z);
            renderCrystalBeams(-vector3f.x, y, -vector3f.z, g, poseStack, multiBufferSource);
            poseStack.popPose();
            return null;
        }
        return vec3;
    }

    @Inject(
            method = "extractRenderState(Lnet/minecraft/world/entity/boss/enderdragon/EndCrystal;Lnet/minecraft/client/renderer/entity/state/EndCrystalRenderState;F)V",
            at = @At("TAIL")
    )
    void extractRenderState(EndCrystal endCrystal, EndCrystalRenderState endCrystalRenderState, float f, CallbackInfo callbackInfo) {
        Entity entity = ((EndCrystalAccessor) endCrystal).crystallizedBeacons$getBeamTarget();

        if (entity != null) {
            ((EndCrystalRendererStateAccessor) endCrystalRenderState).crystallizedBeacons$setBeamTargetEndCrystal(
                    entity.getPosition(f)
                            .subtract(0, 2, 0)
                            .add(0, entity.getBbHeight() / 2, 0)
                            .subtract(endCrystal.getPosition(f))
                            .toVector3f()
            );
        } else {
            ((EndCrystalRendererStateAccessor) endCrystalRenderState).crystallizedBeacons$setBeamTargetEndCrystal(null);
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
        poseStack.mulPose(Axis.ZN.rotationDegrees(i * 2.25F - 45.0F));
        float n = -i;
        float o = Mth.frac(n * 0.2F - (float) Mth.floor(n * 0.1F));
        float z = -1.0F + o;
        float aa = l * 1.8F + z;
        float s = 0.0F;
        float t = l;
        renderPart(poseStack, multiBufferSource.getBuffer(RenderType.beaconBeam(resourceLocation, false)), p, j, s, t, 0.0F, 1.0F, z, aa);
        renderPart(poseStack, multiBufferSource.getBuffer(RenderType.beaconBeam(resourceLocation, true)), q, ARGB.color(32, j), s, t, 0.0F, 1.0F, z, aa);
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