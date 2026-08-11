package com.jdte.common.network.handler;

import com.jdte.common.containers.MineralExtractorContainer;
import com.jdte.common.network.PacketContext;
import com.jdte.common.network.data.MineralExtractorOutputPagePayload;

public final class MineralExtractorOutputPagePacket {
    private MineralExtractorOutputPagePacket() {
    }

    public static void handle(MineralExtractorOutputPagePayload payload, PacketContext context) {
        context.enqueueWork(() -> {
            if (context.player().containerMenu instanceof MineralExtractorContainer container) {
                container.setOutputPage(payload.page());
            }
        });
    }
}
