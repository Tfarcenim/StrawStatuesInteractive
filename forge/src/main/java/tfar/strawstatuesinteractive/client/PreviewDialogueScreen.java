package tfar.strawstatuesinteractive.client;

import fuzs.strawstatues.world.entity.decoration.StrawStatue;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class PreviewDialogueScreen extends DialogueScreen{
    public PreviewDialogueScreen(Component title, StrawStatue strawStatue) {
        super(title, strawStatue);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return false;
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderLabels(guiGraphics, mouseX, mouseY);
        guiGraphics.drawString(font,Component.literal("Preview").withStyle(ChatFormatting.DARK_GRAY),
                20+leftPos,topPos+25,0xffffff,false);
    }
}
