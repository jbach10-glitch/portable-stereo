package dev.portablestereo.network;

import dev.portablestereo.PortableStereo;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.JukeboxSong;

/** Tells a client: entity {@code entityId} is now playing {@code song}. */
public record PlayStereoPayload(int entityId, Holder<JukeboxSong> song) implements CustomPacketPayload {
    public static final Type<PlayStereoPayload> TYPE = new Type<>(PortableStereo.id("play"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PlayStereoPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, PlayStereoPayload::entityId,
            JukeboxSong.STREAM_CODEC, PlayStereoPayload::song,
            PlayStereoPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
