package com.jdte.common.network.data;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

public record AdvancedEnergyTransmitterBindingPayload(BlockPos blockPos) {
    public static AdvancedEnergyTransmitterBindingPayload decode(FriendlyByteBuf buf) {
        return new AdvancedEnergyTransmitterBindingPayload(buf.readBlockPos());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(blockPos);
    }
}
