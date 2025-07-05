package tfar.strawstatuesinteractive.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import tfar.strawstatuesinteractive.Dialogue;
import tfar.strawstatuesinteractive.network.client.S2CModPacket;
import tfar.strawstatuesinteractive.network.server.C2SModPacket;
import tfar.strawstatuesinteractive.platform.Services;

import java.util.Objects;

public class SetDialoguePacket implements S2CModPacket, C2SModPacket {
    private final int entityID;
    private final Dialogue dialogue;

    public SetDialoguePacket(int entityID, Dialogue dialogue) {
        this.entityID = entityID;
        this.dialogue = dialogue;
    }

    public SetDialoguePacket(FriendlyByteBuf buf) {
        this(buf.readInt(), Dialogue.fromPacket(buf));
    }

    @Override
    public void handleClient() {
        Services.PLATFORM.handle(this);
    }

    @Override
    public void handleServer(ServerPlayer player) {
        Services.PLATFORM.handle(this, player);
    }

    @Override
    public void write(FriendlyByteBuf to) {
        to.writeInt(entityID);
        dialogue.toPacket(to);
    }

    public int entityID() {
        return entityID;
    }

    public Dialogue dialogue() {
        return dialogue;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (SetDialoguePacket) obj;
        return this.entityID == that.entityID &&
                Objects.equals(this.dialogue, that.dialogue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityID, dialogue);
    }

    @Override
    public String toString() {
        return "SetDialoguePacket[" +
                "entityID=" + entityID + ", " +
                "dialogue=" + dialogue + ']';
    }

}
