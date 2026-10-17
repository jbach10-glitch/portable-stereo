package dev.portablestereo;

import dev.portablestereo.network.PlayStereoPayload;
import dev.portablestereo.network.StopStereoPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

public class PortableStereo implements ModInitializer {
    public static final String MOD_ID = "portable_stereo";

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        ModItems.init();

        // Server -> client packets: "start playing this song on entity X" / "stop entity X"
        PayloadTypeRegistry.clientboundPlay().register(PlayStereoPayload.TYPE, PlayStereoPayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(StopStereoPayload.TYPE, StopStereoPayload.CODEC);

        // End songs on time, and stop if the stereo leaves the player's inventory
        ServerTickEvents.END_SERVER_TICK.register(StereoPlayback::tick);

        // Players who walk into range mid-song start hearing it; players who leave range stop
        EntityTrackingEvents.START_TRACKING.register((tracked, player) -> {
            if (tracked instanceof ServerPlayer holder) {
                StereoPlayback.onStartTracking(holder, player);
            }
        });
        EntityTrackingEvents.STOP_TRACKING.register((tracked, player) -> {
            if (tracked instanceof ServerPlayer holder) {
                StereoPlayback.onStopTracking(holder, player);
            }
        });
    }
}
