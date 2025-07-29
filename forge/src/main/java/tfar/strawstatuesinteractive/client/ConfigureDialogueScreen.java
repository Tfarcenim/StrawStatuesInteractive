package tfar.strawstatuesinteractive.client;

import fuzs.strawstatues.world.entity.decoration.StrawStatue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.decoration.ArmorStand;

public class ConfigureDialogueScreen extends AbstractConfiguringScreen {


    private EditBox name;


    public ConfigureDialogueScreen(Component title, StrawStatue strawStatue) {
        super(title);
        this.titleLabelX = 8;
        this.titleLabelY = 6;
        this.strawStatue = strawStatue;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected void init() {
        super.init();

        int centerX = leftPos + imageWidth/2;

        this.name = new EditBox(this.font, leftPos + 6, topPos + 35, imageWidth - 12, 14, Component.translatable("container.repair"));
        this.name.setTextColor(-1);
        this.name.setTextColorUneditable(-1);
        this.name.setMaxLength(50);
        this.name.setResponder(this::onNameChanged);
        this.name.setValue("");
        this.addWidget(this.name);
        this.setInitialFocus(this.name);

        addRenderableWidget(Button.builder(Component.literal("Edit Dialogue"),button -> editDialogue())
                .bounds(centerX-60,topPos+60,120,20).build());

        addRenderableWidget(Button.builder(Component.literal("Preview Dialogue"),button -> preview())
                .bounds(centerX-60,topPos+120,120,20).build());

        addRenderableWidget(Button.builder(Component.literal("Advanced Settings"),button -> advancedSettings())
                .bounds(centerX - 60,topPos+imageHeight - 30,120,20).build());

    }

    private void preview() {
        Minecraft.getInstance().pushGuiLayer(new PreviewDialogueScreen(((ArmorStand)(Object)strawStatue).getName(),strawStatue));
    }

    private void editDialogue() {
        Minecraft.getInstance().pushGuiLayer(new EditDialogueScreen(((ArmorStand)(Object)strawStatue).getName(),strawStatue));
    }

    private void advancedSettings() {
        Minecraft.getInstance().pushGuiLayer(new AdvancedSettingsScreen(Component.literal("Advanced NPC Settings"),strawStatue));
    }


    private void onNameChanged(String s) {

    }

    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderLabels(guiGraphics, mouseX, mouseY);
    }
}
