package com.jdte.common.minerals;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public record MineralSurveyData(
        int schemaVersion,
        long indexVersion,
        ResourceLocation biomeId,
        ResourceLocation dimensionId,
        List<MineralEntry> entries
) {
    public static final int CURRENT_SCHEMA = 1;

    public static final Codec<MineralSurveyData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.optionalFieldOf("schema", CURRENT_SCHEMA).forGetter(MineralSurveyData::schemaVersion),
            Codec.LONG.optionalFieldOf("index_version", 0L).forGetter(MineralSurveyData::indexVersion),
            ResourceLocation.CODEC.fieldOf("biome").forGetter(MineralSurveyData::biomeId),
            ResourceLocation.CODEC.fieldOf("dimension").forGetter(MineralSurveyData::dimensionId),
            MineralEntry.CODEC.listOf().fieldOf("entries").forGetter(MineralSurveyData::entries)
    ).apply(instance, MineralSurveyData::new));

    /** Forge 1.20.1 FriendlyByteBuf encoding. */
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeVarInt(schemaVersion);
        buffer.writeVarLong(indexVersion);
        buffer.writeResourceLocation(biomeId);
        buffer.writeResourceLocation(dimensionId);
        buffer.writeVarInt(entries.size());
        for (MineralEntry entry : entries) entry.encode(buffer);
    }

    public static MineralSurveyData decode(FriendlyByteBuf buffer) {
        int schema = buffer.readVarInt();
        long version = buffer.readVarLong();
        ResourceLocation biome = buffer.readResourceLocation();
        ResourceLocation dimension = buffer.readResourceLocation();
        int count = buffer.readVarInt();
        List<MineralEntry> parsed = new ArrayList<>(count);
        for (int index = 0; index < count; index++) parsed.add(MineralEntry.decode(buffer));
        return new MineralSurveyData(schema, version, biome, dimension, parsed);
    }

    /** NBT storage for ItemStack surveys on 1.20.1 (no data components). */
    public CompoundTag save(CompoundTag tag) {
        tag.putInt("schema", schemaVersion);
        tag.putLong("index_version", indexVersion);
        tag.putString("biome", biomeId.toString());
        tag.putString("dimension", dimensionId.toString());
        ListTag entriesTag = new ListTag();
        for (MineralEntry entry : entries) entriesTag.add(entry.save(new CompoundTag()));
        tag.put("entries", entriesTag);
        return tag;
    }

    public static MineralSurveyData load(CompoundTag tag) {
        List<MineralEntry> parsed = new ArrayList<>();
        if (tag.contains("entries", Tag.TAG_LIST)) {
            ListTag entriesTag = tag.getList("entries", Tag.TAG_COMPOUND);
            for (int index = 0; index < entriesTag.size(); index++) {
                parsed.add(MineralEntry.load(entriesTag.getCompound(index)));
            }
        }
        return new MineralSurveyData(
                tag.getInt("schema"),
                tag.getLong("index_version"),
                new ResourceLocation(tag.getString("biome")),
                new ResourceLocation(tag.getString("dimension")),
                parsed);
    }

    public MineralSurveyData {
        entries = List.copyOf(entries);
    }

    public static MineralSurveyData create(long indexVersion, ResourceLocation biomeId,
                                           ResourceLocation dimensionId, List<MineralEntry> entries) {
        return new MineralSurveyData(CURRENT_SCHEMA, indexVersion, biomeId, dimensionId, entries);
    }

    public long totalWeight() {
        long total = 0L;
        for (MineralEntry entry : entries) {
            total = saturatingAdd(total, entry.weight());
        }
        return total;
    }

    private static long saturatingAdd(long left, long right) {
        return right > Long.MAX_VALUE - left ? Long.MAX_VALUE : left + right;
    }
}
