package dev.portablestereo.network;

import dev.portablestereo.PortableStereo;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/** Tells a client: stop whatever entity {@code entityId}'s stereo is playing. */
public record StopStereoPayload(int entityId) implements CustomPacketPayload {
    public static final Type<StopStereoPayload> TYPE = new Type<>(PortableStereo.id("stop"));

    public static final StreamCodec<RegistryFriendlyByteBuf, StopStereoPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, StopStereoPayload::entityId,
            StopStereoPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
