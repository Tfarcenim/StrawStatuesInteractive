package tfar.strawstatuesinteractive.network.server;

import net.minecraft.server.level.ServerPlayer;
import tfar.strawstatuesinteractive.network.ModPacket;

public interface C2SModPacket extends ModPacket {

    void handleServer(ServerPlayer player);

}
