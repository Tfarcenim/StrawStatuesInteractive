package tfar.strawstatuesinteractive.platform;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import tfar.strawstatuesinteractive.network.SetDialoguePacket;
import tfar.strawstatuesinteractive.network.client.S2CModPacket;
import tfar.strawstatuesinteractive.network.client.S2CSetTalkingToPacket;
import tfar.strawstatuesinteractive.network.server.C2SDialogueButtonPacket;
import tfar.strawstatuesinteractive.network.server.C2SModPacket;
import tfar.strawstatuesinteractive.platform.services.IPlatformHelper;
import net.fabricmc.loader.api.FabricLoader;

import java.util.function.Function;

public class FabricPlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public void sendToClient(S2CModPacket msg, ServerPlayer player) {

    }

    @Override
    public void sendToTrackingClients(S2CModPacket msg, Entity entity) {

    }

    @Override
    public void sendToServer(C2SModPacket msg) {

    }

    @Override
    public <MSG extends S2CModPacket> void registerClientPacket(Class<MSG> packetLocation, Function<FriendlyByteBuf, MSG> reader) {

    }

    @Override
    public <MSG extends C2SModPacket> void registerServerPacket(Class<MSG> packetLocation, Function<FriendlyByteBuf, MSG> reader) {

    }

    @Override
    public void handle(S2CSetTalkingToPacket s2CSetTalkingToPacket) {

    }

    @Override
    public void handle(SetDialoguePacket s2CSetDialoguePacket) {

    }

    @Override
    public void handle(SetDialoguePacket c2SEditDialoguePacket, ServerPlayer player) {

    }

    @Override
    public void handle(C2SDialogueButtonPacket c2SDialogueButtonPacket, ServerPlayer player) {

    }
}
