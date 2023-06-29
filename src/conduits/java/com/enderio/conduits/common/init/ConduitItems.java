package com.enderio.conduits.common.init;

import com.enderio.EnderIO;
import com.enderio.api.conduit.ConduitItemFactory;
import com.enderio.api.conduit.IConduitType;
import com.enderio.base.common.init.EIOCreativeTabs;
import com.enderio.base.common.init.EIOItems;
import com.enderio.conduits.common.items.ConduitBlockItem;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import java.util.function.Supplier;

@Mod.EventBusSubscriber
public class ConduitItems {
    private static final Registrate REGISTRATE = EnderIO.registrate();

    public static final ItemEntry<Item> ENERGY = createConduitItem(() -> EnderConduitTypes.ENERGY.get(), "energy");
    public static final ItemEntry<Item> FLUID = createConduitItem(() -> EnderConduitTypes.FLUID.get(), "fluid");
    public static final ItemEntry<Item> PRESSURIZED_FLUID = createConduitItem(() -> EnderConduitTypes.FLUID2.get(), "pressurized_fluid");
    public static final ItemEntry<Item> ENDER_FLUID = createConduitItem(() -> EnderConduitTypes.FLUID3.get(), "ender_fluid");
    public static final ItemEntry<Item> REDSTONE = createConduitItem(() -> EnderConduitTypes.REDSTONE.get(), "redstone");
    public static final ItemEntry<Item> ITEM = createConduitItem(() -> EnderConduitTypes.ITEM.get(), "item");

    private static ItemEntry<Item> createConduitItem(Supplier<? extends IConduitType<?>> type, String itemName) {
        return REGISTRATE.item(itemName + "_conduit",
            properties -> ConduitItemFactory.build(type, properties))
            .tab(EIOCreativeTabs.CONDUITS)
            .model((ctx, prov) -> prov.withExistingParent(itemName+"_conduit", EnderIO.loc("item/conduit")).texture("0", type.get().getItemTexture()))
            .register();
    }

    public static void register() {}
}
