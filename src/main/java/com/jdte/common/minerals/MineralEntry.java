package com.jdte.common.minerals;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.Locale;

public record MineralEntry(
        ResourceLocation oreId,
        long weight,
        int minY,
        int maxY,
        int veinSize,
        Confidence confidence
) {
    public static final Codec<MineralEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("ore").forGetter(MineralEntry::oreId),
            Codec.LONG.fieldOf("weight").forGetter(MineralEntry::weight),
            Codec.INT.optionalFieldOf("min_y", Integer.MIN_VALUE).forGetter(MineralEntry::minY),
            Codec.INT.optionalFieldOf("max_y", Integer.MAX_VALUE).forGetter(MineralEntry::maxY),
            Codec.INT.optionalFieldOf("vein_size", 1).forGetter(MineralEntry::veinSize),
            Confidence.CODEC.optionalFieldOf("confidence", Confidence.ESTIMATED).forGetter(MineralEntry::confidence)
    ).apply(instance, MineralEntry::new));

    /** Forge 1.20.1 FriendlyByteBuf encoding. */
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeResourceLocation(oreId);
        buffer.writeVarLong(weight);
        buffer.writeVarInt(minY);
        buffer.writeVarInt(maxY);
        buffer.writeVarInt(veinSize);
        buffer.writeEnum(confidence);
    }

    public static MineralEntry decode(FriendlyByteBuf buffer) {
        return new MineralEntry(
                buffer.readResourceLocation(),
                buffer.readVarLong(),
                buffer.readVarInt(),
                buffer.readVarInt(),
                buffer.readVarInt(),
                buffer.readEnum(Confidence.class));
    }

    /** NBT storage for ItemStack surveys on 1.20.1 (no data components). */
    public CompoundTag save(CompoundTag tag) {
        tag.putString("ore", oreId.toString());
        tag.putLong("weight", weight);
        tag.putInt("min_y", minY);
        tag.putInt("max_y", maxY);
        tag.putInt("vein_size", veinSize);
        tag.putString("confidence", confidence.name());
        return tag;
    }

    public static MineralEntry load(CompoundTag tag) {
        return new MineralEntry(
                new ResourceLocation(tag.getString("ore")),
                tag.getLong("weight"),
                tag.getInt("min_y"),
                tag.getInt("max_y"),
                tag.getInt("vein_size"),
                Confidence.valueOf(tag.getString("confidence")));
    }

    public MineralEntry {
        weight = Math.max(1L, weight);
        if (minY > maxY) {
            int swap = minY;
            minY = maxY;
            maxY = swap;
        }
        veinSize = Math.max(1, veinSize);
        confidence = confidence == null ? Confidence.ESTIMATED : confidence;
    }

    public enum Confidence {
        EXACT,
        ESTIMATED,
        DATA_PACK;

        public static final Codec<Confidence> CODEC = Codec.STRING.xmap(
                value -> Confidence.valueOf(value.toUpperCase(Locale.ROOT)),
                value -> value.name().toLowerCase(Locale.ROOT));
    }
}
