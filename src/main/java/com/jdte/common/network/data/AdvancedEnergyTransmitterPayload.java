package com.jdte.common.network.data;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

public record AdvancedEnergyTransmitterPayload(BlockPos blockPos, boolean showParticles) {
    public static AdvancedEnergyTransmitterPayload decode(FriendlyByteBuf buf) {
        return new AdvancedEnergyTransmitterPayload(buf.readBlockPos(), buf.readBoolean());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(blockPos);
        buf.writeBoolean(showParticles);
    }
}
