package tfar.strawstatuesinteractive.network.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import tfar.strawstatuesinteractive.platform.Services;

public record C2SDialogueButtonPacket(int entityID, int button) implements C2SModPacket{

    public C2SDialogueButtonPacket(FriendlyByteBuf buf) {
        this(buf.readInt(),buf.readInt());
    }

    @Override
    public void handleServer(ServerPlayer player) {
        Services.PLATFORM.handle(this,player);
    }

    @Override
    public void write(FriendlyByteBuf to) {
        to.writeInt(entityID);
        to.writeInt(button);
    }
}
