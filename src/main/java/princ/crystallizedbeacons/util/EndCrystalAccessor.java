package princ.crystallizedbeacons.util;

import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

public interface EndCrystalAccessor {
    @Nullable Entity crystallizedBeacons$getTargetEntity();
    void crystallizedBeacons$setTargetEntity(@Nullable Entity entity);
}
