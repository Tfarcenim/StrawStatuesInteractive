package tfar.strawstatuesinteractive.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.widget.ExtendedButton;
import tfar.strawstatuesinteractive.StrawStatuesInteractive;

public abstract class AbstractScreen extends Screen {

    public static final ResourceLocation BACKGROUND = StrawStatuesInteractive.id("textures/gui/background.png");
    public static final ResourceLocation SLOT = StrawStatuesInteractive.id("textures/gui/slot.png");

    /** The X size of the inventory window in pixels. */
    protected int imageWidth = 320;
    /** The Y size of the inventory window in pixels. */
    protected int imageHeight = 230;
    /** Starting X position for the Gui. Inconsistent use for Gui backgrounds. */
    protected int leftPos;
    /** Starting Y position for the Gui. Inconsistent use for Gui backgrounds. */
    protected int topPos;
    protected int titleLabelX;
    protected int titleLabelY;


    protected AbstractScreen(Component title) {
        super(title);
        this.titleLabelX = 8;
        this.titleLabelY = 6;
    }


    @Override
    protected void init() {
        super.init();
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;

        addRenderableWidget(new ExtendedButton(Button.builder(Component.literal("x"), button -> onClose())
                .bounds(leftPos + imageWidth-16,topPos,16,16)));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public abstract boolean stillValid(Player player);

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBg(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderLabels(guiGraphics, mouseX, mouseY);
    }

    protected void renderBg(GuiGraphics guiGraphics,int pMouseX, int pMouseY,float partialTick) {
        guiGraphics.blitNineSlicedSized(BACKGROUND,leftPos,topPos,
                imageWidth,imageHeight,4,4,12,12,0,0,12,12);
    }

    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title,leftPos+ this.titleLabelX,topPos+ this.titleLabelY, 0x404040, false);
    }
}
