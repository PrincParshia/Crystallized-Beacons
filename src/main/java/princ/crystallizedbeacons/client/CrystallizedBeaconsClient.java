package princ.crystallizedbeacons.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import princ.crystallizedbeacons.util.EndCrystalAccessor;
import princ.crystallizedbeacons.EndCrystalS2CPayload;

public class CrystallizedBeaconsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(EndCrystalS2CPayload.TYPE, (s2cPayload, context) -> {
            Level level = context.client().level;
            if (level != null) {
                Entity entity = level.getEntity(s2cPayload.crystalId());
                if (entity instanceof EndCrystalAccessor endCrystalAccessor) {
                    int i = s2cPayload.targetEntityId();
                    endCrystalAccessor.crystallizedBeacons$setTargetEntity(i > -1 ? level.getEntity(i) : null);
                }
            }
        });
    }
}
