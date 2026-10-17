package dev.portablestereo.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;

/**
 * A record that plays exactly like a jukebox (same volume, same 64-block linear falloff,
 * on the Jukebox/Note Blocks volume slider) but whose position follows an entity each tick.
 */
public class StereoSoundInstance extends AbstractTickableSoundInstance {
    private final int entityId;
    private int ticksMissing;

    public StereoSoundInstance(SoundEvent sound, int entityId, Entity holder) {
        super(sound, SoundSource.RECORDS, SoundInstance.createUnseededRandom());
        this.entityId = entityId;
        this.volume = 4.0F;            // same as vanilla jukebox -> ~64 block range
        this.pitch = 1.0F;
        this.looping = false;
        this.delay = 0;
        this.attenuation = SoundInstance.Attenuation.LINEAR;
        this.relative = false;
        moveTo(holder);
    }

    private void moveTo(Entity holder) {
        this.x = holder.getX();
        this.y = holder.getY() + holder.getBbHeight() * 0.5;
        this.z = holder.getZ();
    }

    @Override
    public void tick() {
        ClientLevel level = Minecraft.getInstance().level;
        Entity holder = level == null ? null : level.getEntity(entityId);
        if (holder == null || holder.isRemoved()) {
            // Brief grace period for respawns/teleports, then give up
            if (++ticksMissing > 40) {
                stop();
            }
            return;
        }
        ticksMissing = 0;
        moveTo(holder);
    }

    public void halt() {
        stop();
    }
}
