package tfar.strawstatuesinteractive.network.client;

import net.minecraft.network.FriendlyByteBuf;
import tfar.strawstatuesinteractive.platform.Services;

public record S2CSetTalkingToPacket(int entityID) implements S2CModPacket {
    public S2CSetTalkingToPacket(FriendlyByteBuf buf) {
        this(buf.readInt());
    }

    @Override
    public void handleClient() {
        Services.PLATFORM.handle(this);
    }

    @Override
    public void write(FriendlyByteBuf to) {
        to.writeInt(entityID);
    }
}
