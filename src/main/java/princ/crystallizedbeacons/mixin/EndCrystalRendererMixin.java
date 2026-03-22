package princ.crystallizedbeacons.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.entity.EndCrystalRenderer;
import net.minecraft.client.renderer.entity.state.EndCrystalRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import princ.crystallizedbeacons.util.EndCrystalAccessor;

@Mixin(EndCrystalRenderer.class)
public class EndCrystalRendererMixin {
    @Unique
    private static final RenderStateDataKey<Vector3f> END_CRYSTAL_BEAM_TARGET_POS = RenderStateDataKey.create(() -> "crystallized-beacons:endCrystalBeamTargetPos");

    @ModifyExpressionValue(
            method = "submit(Lnet/minecraft/client/renderer/entity/state/EndCrystalRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/renderer/entity/state/EndCrystalRenderState;beamOffset:Lnet/minecraft/world/phys/Vec3;",
                    opcode = Opcodes.GETFIELD
            )
    )
    private static Vec3 submit(Vec3 vec3, EndCrystalRenderState endCrystalRenderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        Vector3f vector3f = endCrystalRenderState.getData(END_CRYSTAL_BEAM_TARGET_POS);
        if (vector3f != null) {
            float i = endCrystalRenderState.ageInTicks;
            float y = -vector3f.y + EndCrystalRenderer.getY(i);
            poseStack.translate(vector3f.x, vector3f.y, vector3f.z);
            submitCrystalBeams(-vector3f.x, y, -vector3f.z, i, poseStack, submitNodeCollector, BeaconRenderer.BEAM_LOCATION, -1, BeaconRenderer.SOLID_BEAM_RADIUS - 0.04F, BeaconRenderer.BEAM_GLOW_RADIUS);
            return null;
        }
        return vec3;
    }

    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/boss/enderdragon/EndCrystal;Lnet/minecraft/client/renderer/entity/state/EndCrystalRenderState;F)V", at = @At("TAIL"))
    private void extractRenderState(EndCrystal endCrystal, EndCrystalRenderState renderState, float partialTick, CallbackInfo ci) {
        Entity entity = ((EndCrystalAccessor) endCrystal).crystallizedBeacons$getTargetEntity();

        if (entity == null) return;

        renderState.setData(END_CRYSTAL_BEAM_TARGET_POS, entity.getPosition(partialTick)
                .subtract(0, 2, 0)
                .add(0, entity.getBbHeight() / 2, 0)
                .subtract(endCrystal.getPosition(partialTick))
                .toVector3f()
        );
    }

    @ModifyExpressionValue(
            method = "shouldRender(Lnet/minecraft/world/entity/boss/enderdragon/EndCrystal;Lnet/minecraft/client/renderer/culling/Frustum;DDD)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/EntityRenderer;shouldRender(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/client/renderer/culling/Frustum;DDD)Z"
            )
    )
    boolean shouldRender(boolean bl, EndCrystal endCrystal) {
        return bl || ((EndCrystalAccessor) endCrystal).crystallizedBeacons$getTargetEntity() != null;
    }

    @Unique
    private static void submitCrystalBeams(float f, float g, float h, float i, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, Identifier identifier, int j, float p, float q) {
        float k = Mth.sqrt(f * f + h * h);
        float l = Mth.sqrt(f * f + g * g + h * h);
        poseStack.pushPose();
        poseStack.translate(0.0F, 2.0F, 0.0F);
        poseStack.mulPose(Axis.YP.rotation((float)(-Math.atan2(h, f)) - ((float)Math.PI / 2F)));
        poseStack.mulPose(Axis.XP.rotation((float)(-Math.atan2(k, g)) - ((float)Math.PI / 2F)));

        float n = -i;
        float o = Mth.frac(n * 0.2F - (float) Mth.floor(n * 0.1F));
        float z = -1.0F + o;
        float aa = l * 1.8F + z;

        float s = 0.0F;
        float t = l;

        submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.beaconBeam(identifier, false), (pose, vertexConsumer) -> renderPart(pose, vertexConsumer, p, j, s, t, 0.0F, 1.0F, z, aa));
        submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.beaconBeam(identifier, true), (pose, vertexConsumer) -> renderPart(pose, vertexConsumer, q, ARGB.color(32, j), s, t, 0.0F, 1.0F, z, aa));
        poseStack.popPose();
    }

    @Unique
    private static void renderPart(PoseStack.Pose pose, VertexConsumer vertexConsumer, float i, int j, float k, float f, float g, float h, float l, float m) {
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
