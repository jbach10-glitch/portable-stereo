package dev.portablestereo;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * The disc inside a stereo: the base item PLUS all of its extra data (custom song,
 * name, artwork, etc.), so custom discs from datapacks come back out exactly as they went in.
 */
public record LoadedDisc(Holder<Item> item, DataComponentPatch components) {
    public static final Codec<LoadedDisc> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.ITEM.holderByNameCodec().fieldOf("item").forGetter(LoadedDisc::item),
            DataComponentPatch.CODEC.optionalFieldOf("components", DataComponentPatch.EMPTY).forGetter(LoadedDisc::components)
    ).apply(instance, LoadedDisc::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, LoadedDisc> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.holderRegistry(Registries.ITEM), LoadedDisc::item,
            DataComponentPatch.STREAM_CODEC, LoadedDisc::components,
            LoadedDisc::new);

    public static LoadedDisc of(ItemStack stack) {
        return new LoadedDisc(BuiltInRegistries.ITEM.wrapAsHolder(stack.getItem()), stack.getComponentsPatch());
    }

    public ItemStack toStack() {
        return new ItemStack(item, 1, components);
    }
}
