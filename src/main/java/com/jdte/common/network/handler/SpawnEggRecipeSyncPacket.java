package com.jdte.common.network.handler;

import com.jdte.common.network.data.SpawnEggRecipeSyncPayload;
import com.jdte.common.network.PacketContext;

public final class SpawnEggRecipeSyncPacket {
    private SpawnEggRecipeSyncPacket() {
    }

    public static void handle(SpawnEggRecipeSyncPayload payload, PacketContext context) {
        if (net.minecraftforge.api.distmarker.Dist.CLIENT != net.minecraftforge.fml.loading.FMLEnvironment.dist) {
            return;
        }
        context.enqueueWork(() -> com.jdte.client.SpawnEggRecipeClientCache.set(payload.recipes()));
    }
}
