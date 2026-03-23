package princ.crystallizedbeacons;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import princ.crystallizedbeacons.network.protocol.world.entity.boss.enderdragon.EndCrystalS2CPayload;

public class CrystallizedBeacons implements ModInitializer {
    @Override
    public void onInitialize() {
        this.registerPayload();
    }

    private void registerPayload() {
        PayloadTypeRegistry.playS2C().register(EndCrystalS2CPayload.TYPE, EndCrystalS2CPayload.STREAM_CODEC);
    }
}