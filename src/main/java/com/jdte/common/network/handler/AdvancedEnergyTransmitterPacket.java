package com.jdte.common.network.handler;

import com.direwolf20.justdirethings.common.containers.basecontainers.BaseMachineContainer;
import com.jdte.common.blockentities.AdvancedEnergyTransmitterBE;
import com.jdte.common.network.PacketContext;
import com.jdte.common.network.data.AdvancedEnergyTransmitterPayload;

public final class AdvancedEnergyTransmitterPacket {
    private AdvancedEnergyTransmitterPacket() {
    }

    public static void handle(AdvancedEnergyTransmitterPayload payload, PacketContext context) {
        context.enqueueWork(() -> {
            if (!(context.player().containerMenu instanceof BaseMachineContainer menu)
                    || !(menu.baseMachineBE instanceof AdvancedEnergyTransmitterBE transmitter)
                    || !transmitter.getBlockPos().equals(payload.blockPos())) {
                return;
            }
            transmitter.setShowParticles(payload.showParticles());
        });
    }
}
