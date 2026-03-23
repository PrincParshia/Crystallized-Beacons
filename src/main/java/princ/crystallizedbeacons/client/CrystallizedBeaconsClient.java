package princ.crystallizedbeacons.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import princ.crystallizedbeacons.util.world.entity.boss.enderdragon.EndCrystalAccessor;
import princ.crystallizedbeacons.network.protocol.world.entity.boss.enderdragon.EndCrystalS2CPayload;

public class CrystallizedBeaconsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        this.registerClientPlayNetworking();
    }

    private void registerClientPlayNetworking() {
        ClientPlayNetworking.registerGlobalReceiver(EndCrystalS2CPayload.TYPE, (s2cPayload, context) -> {
            Level level = context.client().level;
            if (level != null) {
                Entity entity = level.getEntity(s2cPayload.endCrystalId());
                if (entity instanceof EndCrystalAccessor endCrystalAccessor) {
                    int i = s2cPayload.beamTargetId();
                    endCrystalAccessor.crystallizedBeacons$setBeamTarget(i > -1 ? level.getEntity(i) : null);
                }
            }
        });
    }
}
