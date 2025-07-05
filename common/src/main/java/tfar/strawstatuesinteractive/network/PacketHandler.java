package tfar.strawstatuesinteractive.network;

import net.minecraft.resources.ResourceLocation;
import tfar.strawstatuesinteractive.StrawStatuesInteractive;
import tfar.strawstatuesinteractive.network.client.S2CSetTalkingToPacket;
import tfar.strawstatuesinteractive.network.server.C2SDP;
import tfar.strawstatuesinteractive.platform.Services;

import java.util.Locale;

public class PacketHandler {

    public static void registerPackets() {
        Services.PLATFORM.registerClientPacket(S2CSetTalkingToPacket.class, S2CSetTalkingToPacket::new);
        Services.PLATFORM.registerClientPacket(SetDialoguePacket.class, SetDialoguePacket::new);

        Services.PLATFORM.registerServerPacket(C2SDP.class, C2SDP::new);
    }

    public static ResourceLocation packet(Class<?> clazz) {
        return StrawStatuesInteractive.id(clazz.getName().toLowerCase(Locale.ROOT));
    }

}
