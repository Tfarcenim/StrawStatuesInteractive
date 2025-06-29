package tfar.strawstatuesinteractive;

import fuzs.puzzleslib.api.event.v1.core.EventResultHolder;
import fuzs.strawstatues.world.entity.decoration.StrawStatue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfar.strawstatuesinteractive.client.AbstractConfiguringScreen;
import tfar.strawstatuesinteractive.client.ConfigureDialogueScreen;
import tfar.strawstatuesinteractive.client.DialogueScreen;
import tfar.strawstatuesinteractive.network.client.S2CSetTalkingToPacket;

@Mod(StrawStatuesInteractive.MOD_ID)
public class StrawStatuesInteractiveForge {
    
    public StrawStatuesInteractiveForge() {

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();;
        if (FMLEnvironment.dist.isClient()) {
            Client.init(bus);
        }
        // This method is invoked by the Forge mod loader when it is ready
        // to load your mod. You can access Forge and Common code in this
        // project.
    
        // Use Forge to bootstrap the Common mod.
        StrawStatuesInteractive.init();
    }

    public static void yes(Player player, Level level, InteractionHand interactionHand, Entity target, Vec3 hitVector,
                           CallbackInfoReturnable<EventResultHolder<InteractionResult>> cir) {
        if (target instanceof StrawStatue strawStatue) {
            if (player.level().isClientSide) {
                Client.strawStatue = strawStatue;
                if (player.isCrouching()) return;
                Client.openScreen(player,strawStatue);
            } else {
                if (player.isCrouching()) return;
                ((StrawStatueDuck)(Object)strawStatue).setTalkingTo(player);
            }

            cir.setReturnValue(EventResultHolder.interrupt(InteractionResult.SUCCESS));
        }
    }

        public static void readExtraData(StrawStatue strawStatue,CompoundTag tag) {

    }

    public static void saveExtraData(StrawStatue strawStatue,CompoundTag tag) {

    }

    public static class Client {

        static StrawStatue strawStatue;
        static void init(IEventBus bus) {
            MinecraftForge.EVENT_BUS.addListener(Client::addScreenButton);
        }

        static void addScreenButton(ScreenEvent.Init.Post e) {
            Screen screen = e.getScreen();
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null && player.hasPermissions(2) && isArmorStand(screen)) {
                Button button = Button.builder(Component.literal("Dialogue"),button1 -> onPress(button1)).bounds(0,0,60,20).build();
                e.addListener(button);
            }
        }

        static void openScreen(Player player,StrawStatue strawStatue) {
            Minecraft.getInstance().setScreen(new DialogueScreen(Component.literal("test"),strawStatue));
        }

        static void onPress(Button b) {
            Minecraft.getInstance().pushGuiLayer(new ConfigureDialogueScreen(Component.literal("Non Player Character"),strawStatue));
        }

        static boolean isArmorStand(Screen screen) {
            Class<? extends Screen> clazz = screen.getClass();
            String name = clazz.getName();
            if (name.contains("ArmorStandRotationsScreen")) {
                return true;
            }
            return false;
        }

        public static void handle(S2CSetTalkingToPacket packet) {
            ClientLevel level = Minecraft.getInstance().level;
            if (level != null) {
                Entity entity = level.getEntity(packet.entityID());
                if (entity instanceof StrawStatueDuck strawStatue) {
                    strawStatue.setTalkingTo(Minecraft.getInstance().player);
                    if (Minecraft.getInstance().screen instanceof AbstractConfiguringScreen abstractScreen) {
                        abstractScreen.setStrawStatue((StrawStatue)(Object) strawStatue);
                    }
                }
            }
        }
    }
}