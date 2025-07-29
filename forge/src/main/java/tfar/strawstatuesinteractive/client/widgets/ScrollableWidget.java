package tfar.strawstatuesinteractive.client.widgets;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import tfar.strawstatuesinteractive.client.AdvancedSettingsScreen;

public abstract class ScrollableWidget implements GuiEventListener {

    public int width;
    public int height;
    public int x;
    public int y;
    final AdvancedSettingsScreen.DetailsList.Entry parent;
    public boolean visible = true;
    protected Component message;

    public ScrollableWidget(int x, int y, int width, int height,Component message, AdvancedSettingsScreen.DetailsList.Entry parent) {
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
        this.message = message;
        this.parent = parent;
    }


    public abstract void renderScrollable(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTick);

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (clicked(mouseX, mouseY)) {
            playDownSound(Minecraft.getInstance().getSoundManager());
            onClick(mouseX, mouseY);
            return true;
        }
        return false;
    }

    public void renderString(GuiGraphics guiGraphics, Font font, int color,int left,int top) {
        this.renderScrollingString(guiGraphics, font, 2, color,left,top);
    }

    protected void renderScrollingString(GuiGraphics guiGraphics, Font font, int width, int color,int left,int top) {
        int i = this.x + width + top;
        int j = this.x + this.width - width + left;
        //renderScrollingString(guiGraphics, font, message, i, this.y, j, this.y + this.height, color);
        renderScrollingString(guiGraphics, font,Component.literal("AAAAAAAAAAAAAAAAA"), 0, 0, j, this.y + this.height, color);

    }

    protected static void renderScrollingString(GuiGraphics guiGraphics, Font font, Component text, int minX, int minY, int maxX, int maxY, int color) {
        int i = font.width(text);
        int j = (minY + maxY - 9) / 2 + 1;
        int k = maxX - minX;
        if (i > k) {
            int l = i - k;
            double d0 = (double) Util.getMillis() / 1000.0D;
            double d1 = Math.max((double)l * 0.5D, 3.0D);
            double d2 = Math.sin((Math.PI / 2D) * Math.cos((Math.PI * 2D) * d0 / d1)) / 2.0D + 0.5D;
            double d3 = Mth.lerp(d2, 0.0D, (double)l);
            guiGraphics.enableScissor(minX, minY, maxX, maxY);
            guiGraphics.drawString(font, text, minX - (int)d3, j, color);
            guiGraphics.disableScissor();
        } else {
            guiGraphics.drawCenteredString(font, text, (minX + maxX) / 2, j, color);
        }

    }

    public void playDownSound(SoundManager handler) {
        handler.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    public abstract void onClick(double mouseX, double mouseY);

    protected boolean clicked(double mouseX, double mouseY) {

        int left = parent.getLeftPos();
        int top = parent.getTopPos();

        boolean xBounds = mouseX >= x + left && mouseX < x + this.width + left;
        boolean yBounds = mouseY >= y + top && mouseY < y + this.height + top;

        return this.visible && xBounds && yBounds;
    }

    /**
     * Checks if the given mouse coordinates are over the GUI element.
     * <p>
     * @return {@code true} if the mouse is over the GUI element, {@code false} otherwise.
     *
     * @param mouseX the X coordinate of the mouse.
     * @param mouseY the Y coordinate of the mouse.
     */
    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return clicked(mouseX,mouseY);
    }


    private boolean focused;
    @Override
    public void setFocused(boolean focused) {
        this.focused = focused;
    }

    @Override
    public boolean isFocused() {
        return focused;
    }

}
