package com.jdte.common.network.handler;

import com.jdte.common.network.PacketContext;
import com.jdte.common.network.data.MineralSurveyOpenPayload;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLEnvironment;

public final class MineralSurveyOpenPacket {
    private MineralSurveyOpenPacket() {
    }

    public static void handle(MineralSurveyOpenPayload payload, PacketContext context) {
        context.enqueueWork(() -> {
            if (FMLEnvironment.dist == Dist.CLIENT) {
                openScreen(payload);
            }
        });
    }

    @OnlyIn(Dist.CLIENT)
    private static void openScreen(MineralSurveyOpenPayload payload) {
        Minecraft.getInstance().setScreen(
                new com.jdte.client.screens.MineralSurveyScreen(payload.survey()));
    }
}
