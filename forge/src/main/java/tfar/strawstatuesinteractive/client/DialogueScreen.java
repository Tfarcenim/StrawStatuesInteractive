package tfar.strawstatuesinteractive.client;

import fuzs.strawstatues.world.entity.decoration.StrawStatue;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import tfar.strawstatuesinteractive.StrawStatuesInteractive;

public class DialogueScreen extends AbstractScreen {

    public DialogueScreen(Component title, StrawStatue strawStatue) {
        super(title);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderBg(guiGraphics, mouseX, mouseY, partialTick);
    }



    @Override
    protected void init() {
        super.init();


    }

    @Override
    public boolean stillValid(Player player) {
        return false;
    }
}
