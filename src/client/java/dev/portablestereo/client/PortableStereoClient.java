package dev.portablestereo.client;

import java.util.HashMap;
import java.util.Map;

import dev.portablestereo.network.PlayStereoPayload;
import dev.portablestereo.network.StopStereoPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;

public class PortableStereoClient implements ClientModInitializer {
    /** entity id -> the song currently coming out of that entity's stereo */
    private static final Map<Integer, StereoSoundInstance> ACTIVE = new HashMap<>();

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(PlayStereoPayload.TYPE, (payload, context) -> {
            Minecraft client = context.client();
            stop(client, payload.entityId());
            if (client.level == null) {
                return;
            }
            Entity holder = client.level.getEntity(payload.entityId());
            if (holder == null) {
                return;
            }
            SoundEvent sound = payload.song().value().soundEvent().value();
            StereoSoundInstance instance = new StereoSoundInstance(sound, payload.entityId(), holder);
            ACTIVE.put(payload.entityId(), instance);
            client.getSoundManager().play(instance);
        });

        ClientPlayNetworking.registerGlobalReceiver(StopStereoPayload.TYPE,
                (payload, context) -> stop(context.client(), payload.entityId()));

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            ACTIVE.values().forEach(StereoSoundInstance::halt);
            ACTIVE.clear();
        });
    }

    private static void stop(Minecraft client, int entityId) {
        StereoSoundInstance old = ACTIVE.remove(entityId);
        if (old != null) {
            old.halt();
            client.getSoundManager().stop(old);
        }
    }
}
