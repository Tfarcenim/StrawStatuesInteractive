package tfar.strawstatuesinteractive.platform.services;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import tfar.strawstatuesinteractive.network.SetDialoguePacket;
import tfar.strawstatuesinteractive.network.client.S2CModPacket;
import tfar.strawstatuesinteractive.network.client.S2CSetTalkingToPacket;
import tfar.strawstatuesinteractive.network.server.C2SDialogueButtonPacket;
import tfar.strawstatuesinteractive.network.server.C2SModPacket;

import java.util.Collection;
import java.util.function.Function;

public interface IPlatformHelper {

    /**
     * Gets the name of the current platform
     *
     * @return The name of the current platform.
     */
    String getPlatformName();

    /**
     * Checks if a mod with the given id is loaded.
     *
     * @param modId The mod to check if it is loaded.
     * @return True if the mod is loaded, false otherwise.
     */
    boolean isModLoaded(String modId);

    /**
     * Check if the game is currently in a development environment.
     *
     * @return True if in a development environment, false otherwise.
     */
    boolean isDevelopmentEnvironment();

    /**
     * Gets the name of the environment type as a string.
     *
     * @return The name of the environment type.
     */
    default String getEnvironmentName() {

        return isDevelopmentEnvironment() ? "development" : "production";
    }

    void sendToClient(S2CModPacket msg, ServerPlayer player);

    default void sendToClients(S2CModPacket msg, Collection<ServerPlayer> playerList) {
        playerList.forEach(player -> sendToClient(msg,player));
    }

    void sendToTrackingClients(S2CModPacket msg, Entity entity);

    void sendToServer(C2SModPacket msg);

    <MSG extends S2CModPacket> void registerClientPacket(Class<MSG> packetLocation, Function<FriendlyByteBuf,MSG> reader);

    <MSG extends C2SModPacket> void registerServerPacket(Class<MSG> packetLocation, Function<FriendlyByteBuf,MSG> reader);

    void handle(S2CSetTalkingToPacket s2CSetTalkingToPacket);

    void handle(SetDialoguePacket s2CSetDialoguePacket);

    void handle(SetDialoguePacket c2SEditDialoguePacket, ServerPlayer player);

    void handle(C2SDialogueButtonPacket c2SDialogueButtonPacket, ServerPlayer player);
}