package tfar.strawstatuesinteractive.client.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import tfar.strawstatuesinteractive.client.AdvancedSettingsScreen;

public abstract class ScrollableWidget implements GuiEventListener {

    public int width;
    public int height;
    public int x;
    public int y;
    final AdvancedSettingsScreen.DetailsList.Entry parent;
    public boolean visible = true;
    private Component message;

    public ScrollableWidget(int x, int y, int width, int height, AdvancedSettingsScreen.DetailsList.Entry parent) {
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
        this.parent = parent;
    }


    public abstract void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTick);

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (clicked(mouseX, mouseY)) {
            playDownSound(Minecraft.getInstance().getSoundManager());
            onClick(mouseX, mouseY);
            return true;
        }
        return false;
    }

    public void playDownSound(SoundManager handler) {
        handler.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    public void onClick(double mouseX, double mouseY) {
    }

    protected boolean clicked(double mouseX, double mouseY) {

        int left = parent.getLeftPos();
        int top = parent.getTopPos();

        boolean xBounds = mouseX >= x + left && mouseX < x + this.width + left;
        boolean yBounds = mouseY >= y + top && mouseY < y + this.height + top;

        return this.visible && xBounds && yBounds;
    }


    public boolean focused;
    @Override
    public void setFocused(boolean focused) {
        this.focused = focused;
    }

    @Override
    public boolean isFocused() {
        return focused;
    }

}
