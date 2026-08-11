package com.jdte.common.network.data;

import net.minecraft.network.FriendlyByteBuf;

public record MineralExtractorOutputPagePayload(int page) {
    public static MineralExtractorOutputPagePayload decode(FriendlyByteBuf buf) {
        return new MineralExtractorOutputPagePayload(buf.readVarInt());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(page);
    }
}
