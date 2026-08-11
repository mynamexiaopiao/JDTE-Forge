package com.jdte.common.network.data;

import com.jdte.common.minerals.MineralSurveyData;
import net.minecraft.network.FriendlyByteBuf;

public record MineralSurveyOpenPayload(MineralSurveyData survey) {
    public static MineralSurveyOpenPayload decode(FriendlyByteBuf buf) {
        return new MineralSurveyOpenPayload(MineralSurveyData.decode(buf));
    }

    public void encode(FriendlyByteBuf buf) {
        survey.encode(buf);
    }
}
