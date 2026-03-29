package princ.crystallizedbeacons.util.client.renderer.entity.state;

import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public interface EndCrystalRendererStateAccessor {
    @Nullable Vector3f crystallizedBeacons$getBeamTarget();
    void crystallizedBeacons$setBeamTargetEndCrystal(Vector3f vector3f);
}
