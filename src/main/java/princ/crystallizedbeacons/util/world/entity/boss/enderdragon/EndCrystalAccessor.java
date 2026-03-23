package princ.crystallizedbeacons.util.world.entity.boss.enderdragon;

import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

public interface EndCrystalAccessor {
    @Nullable Entity crystallizedBeacons$getBeamTarget();
    void crystallizedBeacons$setBeamTarget(@Nullable Entity entity);
}
