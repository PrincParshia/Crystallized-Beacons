package princ.crystallizedbeacons.mixin.client.renderer.entity.state;

import net.minecraft.client.renderer.entity.state.EndCrystalRenderState;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import princ.crystallizedbeacons.util.client.renderer.entity.state.EndCrystalRendererStateAccessor;

@Mixin(EndCrystalRenderState.class)
public class EndCrystalRenderStateMixin implements EndCrystalRendererStateAccessor {
    @Unique
    private Vector3f beamTarget;

    @Override
    public Vector3f crystallizedBeacons$getBeamTarget() {
        return this.beamTarget;
    }

    @Override
    public void crystallizedBeacons$setBeamTargetEndCrystal(Vector3f vector3f) {
        this.beamTarget = vector3f;
    }
}
