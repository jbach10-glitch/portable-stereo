package dev.portablestereo;

import java.util.function.Function;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;

public final class ModItems {
    private ModItems() {}

    /** Which music disc is loaded into a stereo. Saved with the item and synced to clients. */
    public static final DataComponentType<Holder<Item>> LOADED_DISC = Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            PortableStereo.id("loaded_disc"),
            DataComponentType.<Holder<Item>>builder()
                    .persistent(BuiltInRegistries.ITEM.holderByNameCodec())
                    .networkSynchronized(ByteBufCodecs.holderRegistry(Registries.ITEM))
                    .build());

    public static final StereoItem PORTABLE_STEREO = register(
            "portable_stereo", StereoItem::new, new Item.Properties().stacksTo(1));

    private static <T extends Item> T register(String name, Function<Item.Properties, T> factory, Item.Properties props) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, PortableStereo.id(name));
        T item = factory.apply(props.setId(key));
        return Registry.register(BuiltInRegistries.ITEM, key, item);
    }

    public static void init() {
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES)
                .register(output -> output.accept(PORTABLE_STEREO));
    }
}
