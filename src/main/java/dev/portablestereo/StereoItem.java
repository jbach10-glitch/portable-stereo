package dev.portablestereo;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Controls (right-click = use):
 *   - Use while holding a music disc in your other hand -> loads the disc and starts playing
 *   - Use with a disc loaded                            -> play / stop toggle
 *   - Sneak + use                                       -> eject the disc
 */
public class StereoItem extends Item {
    public StereoItem(Properties properties) {
        super(properties);
    }

    public static ItemStack getDisc(ItemStack stereo) {
        LoadedDisc disc = stereo.get(ModItems.LOADED_DISC);
        return disc == null ? ItemStack.EMPTY : disc.toStack();
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResult.SUCCESS; // client: just swing the arm
        }

        ItemStack stereo = player.getItemInHand(hand);
        ItemStack loaded = getDisc(stereo);
        InteractionHand otherHand = hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
        ItemStack other = player.getItemInHand(otherHand);

        // Sneak + use: eject
        if (player.isShiftKeyDown()) {
            if (loaded.isEmpty()) {
                return InteractionResult.PASS;
            }
            StereoPlayback.stop(serverPlayer);
            stereo.remove(ModItems.LOADED_DISC);
            if (!player.getInventory().add(loaded)) {
                // Inventory full: drop the disc at the player's feet
                level.addFreshEntity(new ItemEntity(level, player.getX(), player.getY(), player.getZ(), loaded));
            }
            tell(serverPlayer, Component.translatable("message.portable_stereo.ejected"));
            return InteractionResult.SUCCESS;
        }

        // Empty stereo: load the disc from the other hand
        if (loaded.isEmpty()) {
            if (!other.isEmpty() && other.has(DataComponents.JUKEBOX_PLAYABLE)) {
                ItemStack disc = other.copyWithCount(1);
                stereo.set(ModItems.LOADED_DISC, LoadedDisc.of(other));
                if (!player.hasInfiniteMaterials()) {
                    other.shrink(1);
                }
                StereoPlayback.start(serverPlayer, disc);
                return InteractionResult.SUCCESS;
            }
            tell(serverPlayer, Component.translatable("message.portable_stereo.no_disc"));
            return InteractionResult.FAIL;
        }

        // Disc loaded: toggle play/stop
        if (StereoPlayback.isPlaying(serverPlayer)) {
            StereoPlayback.stop(serverPlayer);
            tell(serverPlayer, Component.translatable("message.portable_stereo.stopped"));
        } else {
            StereoPlayback.start(serverPlayer, loaded);
        }
        return InteractionResult.SUCCESS;
    }

    static void tell(ServerPlayer player, Component message) {
        player.sendSystemMessage(message, true); // true = action bar
    }
}
