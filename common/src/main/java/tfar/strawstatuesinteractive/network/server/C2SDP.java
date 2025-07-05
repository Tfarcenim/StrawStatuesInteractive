package tfar.strawstatuesinteractive.network.server;

import net.minecraft.network.FriendlyByteBuf;
import tfar.strawstatuesinteractive.Dialogue;
import tfar.strawstatuesinteractive.network.SetDialoguePacket;

public class C2SDP extends SetDialoguePacket {
    public C2SDP(int entityID, Dialogue dialogue) {
        super(entityID, dialogue);
    }

    public C2SDP(FriendlyByteBuf buf) {
        super(buf);
    }
}
