package princ.crystallizedbeacons;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import static princ.crystallizedbeacons.CrystallizedBeacons.NAMESPACE;

public record EndCrystalS2CPayload(int crystalId, int targetEntityId) implements CustomPacketPayload {
    public static final Type<EndCrystalS2CPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(NAMESPACE, "end_crystal_s2c"));
    public static final StreamCodec<FriendlyByteBuf, EndCrystalS2CPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, EndCrystalS2CPayload::crystalId,
            ByteBufCodecs.INT, EndCrystalS2CPayload::targetEntityId,
            EndCrystalS2CPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
