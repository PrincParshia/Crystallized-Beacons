package princ.crystallizedbeacons.network.protocol.world.entity.boss.enderdragon;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import static princ.crystallizedbeacons.CrystallizedBeaconsConstants.withDefaultNamespace;

public record EndCrystalS2CPayload(int endCrystalId, int beamTargetId) implements CustomPacketPayload {
    public static final Type<EndCrystalS2CPayload> TYPE;
    public static final StreamCodec<FriendlyByteBuf, EndCrystalS2CPayload> STREAM_CODEC;

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    static {
        TYPE = new Type<>(withDefaultNamespace("end_crystal_s2c"));
        STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.INT, EndCrystalS2CPayload::endCrystalId, ByteBufCodecs.INT, EndCrystalS2CPayload::beamTargetId, EndCrystalS2CPayload::new);
    }
}
