package com.enderio.api.travel;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.Lazy;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

@ApiStatus.Internal
public class TravelRegistry {

    private static final Map<ResourceLocation, TravelEntry<?>> registry = new HashMap<>();

    public static <T extends ITravelTarget> void addTravelEntry(ResourceLocation serializationName, Function<CompoundTag, T> constructor,
        Supplier<Lazy<TravelRenderer<T>>> renderer) {
        registry.put(serializationName, new TravelEntry<>(serializationName, constructor, renderer));
    }

    public static <T extends ITravelTarget> void addTravelEntry(TravelEntry<?> travelEntry) {
        registry.put(travelEntry.serializationName(), travelEntry);
    }

    public static <T extends ITravelTarget> TravelRenderer<T> getRenderer(T entry) {
        return (TravelRenderer<T>) registry.get(entry.getSerializationName()).renderer().get().get();
    }

    public static Optional<ITravelTarget> deserialize(CompoundTag nbt) {
        return Optional.ofNullable(registry.get(new ResourceLocation(nbt.getString("name")))).map(entry -> entry.constructor().apply(nbt.getCompound("data")));
    }

    public static boolean isRegistered(ITravelTarget target) {
        return registry.containsKey(target.getSerializationName());
    }

    public static CompoundTag serialize(ITravelTarget travelData) {
        CompoundTag nbt = new CompoundTag();
        nbt.putString("name", travelData.getSerializationName().toString());
        nbt.put("data", travelData.save());
        return nbt;
    }
}
