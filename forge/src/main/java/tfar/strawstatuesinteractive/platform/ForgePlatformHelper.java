package tfar.strawstatuesinteractive.platform;

import fuzs.strawstatues.world.entity.decoration.StrawStatue;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import tfar.strawstatuesinteractive.*;
import tfar.strawstatuesinteractive.network.SetDialoguePacket;
import tfar.strawstatuesinteractive.network.client.S2CModPacket;
import tfar.strawstatuesinteractive.network.client.S2CSetTalkingToPacket;
import tfar.strawstatuesinteractive.network.server.C2SDialogueButtonPacket;
import tfar.strawstatuesinteractive.network.server.C2SModPacket;
import tfar.strawstatuesinteractive.platform.services.IPlatformHelper;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;

import java.util.List;
import java.util.function.Function;

public class ForgePlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {

        return "Forge";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return !FMLLoader.isProduction();
    }

    @Override
    public void sendToClient(S2CModPacket msg, ServerPlayer player) {
        PacketHandlerForge.sendToClient(msg,player);
    }

    @Override
    public void sendToTrackingClients(S2CModPacket msg, Entity entity) {
        PacketHandlerForge.INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity),msg);
    }

    @Override
    public void sendToServer(C2SModPacket msg) {
        PacketHandlerForge.sendToServer(msg);
    }

    int i;

    @Override
    public <MSG extends S2CModPacket> void registerClientPacket(Class<MSG> packetLocation, Function<FriendlyByteBuf, MSG> reader) {
        PacketHandlerForge.INSTANCE.registerMessage(i++, packetLocation, MSG::write, reader, PacketHandlerForge.wrapS2C());
    }

    @Override
    public <MSG extends C2SModPacket> void registerServerPacket(Class<MSG>  packetLocation, Function<FriendlyByteBuf, MSG> reader) {
        PacketHandlerForge.INSTANCE.registerMessage(i++, packetLocation, MSG::write, reader, PacketHandlerForge.wrapC2S());
    }

    @Override
    public void handle(S2CSetTalkingToPacket s2CSetTalkingToPacket) {
        StrawStatuesInteractiveForge.Client.handle(s2CSetTalkingToPacket);
    }

    @Override
    public void handle(SetDialoguePacket s2CSetDialoguePacket) {
        StrawStatuesInteractiveForge.Client.handle(s2CSetDialoguePacket);
    }

    @Override
    public void handle(SetDialoguePacket c2SEditDialoguePacket, ServerPlayer player) {
        Entity entity = player.level().getEntity(c2SEditDialoguePacket.entityID());
        if (entity instanceof StrawStatue strawStatue) {
            int permission = player.server.getProfilePermissions(player.getGameProfile());
            if (permission >= Commands.LEVEL_GAMEMASTERS) {
                StrawStatueDuck.of(strawStatue).setDialogue(c2SEditDialoguePacket.dialogue());
            } else {
                StrawStatuesInteractive.LOG.warn("Unauthorized player {} attempted to modify Straw Statue Dialogue",player);
            }
        }
    }

    @Override
    public void handle(C2SDialogueButtonPacket packet, ServerPlayer player) {
        Entity entity = player.level().getEntity(packet.entityID());
        if (entity instanceof StrawStatue strawStatue) {
            StrawStatueDuck strawStatueDuck = StrawStatueDuck.of(strawStatue);
            Player interacting = strawStatueDuck.getTalkingTo();
            Dialogue dialogue = strawStatueDuck.getDialogue();
            if (dialogue!= null) {
                List<NPCCommandEntry> commandEntries = dialogue.commands();
                if (packet.button()>=0 && packet.button() < commandEntries.size()) {
                    NPCCommandEntry commandEntry = commandEntries.get(packet.button());
                    if (commandEntry.buttonMode) {
                        List<String> commands = commandEntry.commands;
                        for (String command : commands) {
                            String replaced = command.replace("@initiator",player.getGameProfile().getName());
                            player.server.getCommands().performPrefixedCommand(((ArmorStand)strawStatue).createCommandSourceStack().withSuppressedOutput(),
                                    replaced);
                        }
                    }
                }
            }
        }
    }
}