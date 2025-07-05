package tfar.strawstatuesinteractive.client;

import fuzs.strawstatues.world.entity.decoration.StrawStatue;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class AdvancedSettingsScreen extends AbstractConfiguringScreen{
    protected AdvancedSettingsScreen(Component title, StrawStatue strawStatue) {
        super(title);
        this.strawStatue = strawStatue;
    }

    @Override
    protected void init() {
        super.init();
       this.addRenderableWidget(Button.builder(Component.literal("Add Command"), (button) -> {
        }).bounds(this.width / 2 + 2, 196, 98, 20).build());
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderLabels(guiGraphics, mouseX, mouseY);
       // guiGraphics.drawString(font,Component.literal(""))
    }
}
