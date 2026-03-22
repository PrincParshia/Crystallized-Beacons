package princ.crystallizedbeacons;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class CrystallizedBeacons implements ModInitializer {
    public static final String NAMESPACE = "crystallized-beacons";

    @Override
    public void onInitialize() {
        PayloadTypeRegistry.playS2C().register(EndCrystalS2CPayload.TYPE, EndCrystalS2CPayload.STREAM_CODEC);
    }
}