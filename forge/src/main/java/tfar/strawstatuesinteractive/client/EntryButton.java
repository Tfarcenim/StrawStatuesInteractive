package tfar.strawstatuesinteractive.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class EntryButton implements GuiEventListener {


    public int width;
    public int height;
    public int x;
    public int y;
    final AdvancedSettingsScreen.DetailsList.Entry parent;
    private final Runnable onClickEvent;
    public boolean visible = true;

    public boolean focused;

    public EntryButton(int x, int y, int width, int height,
                       Runnable onClickEvent,AdvancedSettingsScreen.DetailsList.Entry parent) {
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
        this.parent = parent;
        this.onClickEvent = onClickEvent;
    }

    public EntryButton(Builder builder, AdvancedSettingsScreen.DetailsList.Entry parent) {
        this(builder.x, builder.y, builder.width, builder.height,builder.onPress, parent);
    }

    public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTick) {

    }

    @Override
    public void setFocused(boolean focused) {
        this.focused = focused;
    }

    @Override
    public boolean isFocused() {
        return focused;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (clicked(mouseX, mouseY)) {
            playDownSound(Minecraft.getInstance().getSoundManager());
            onClick(mouseX, mouseY);
            return true;
        }
        return false;
    }

    public void onClick(double mouseX, double mouseY) {
        onClickEvent.run();
    }

    public void playDownSound(SoundManager handler) {
        handler.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    protected boolean clicked(double mouseX, double mouseY) {

        int left = parent.getLeftPos();
        int top = parent.getTopPos();

        boolean xBounds = mouseX >= x + left && mouseX < x + this.width + left;
        boolean yBounds = mouseY >= y + top && mouseY < y + this.height + top;

        return this.visible && xBounds && yBounds;
    }

    public static Builder builder(Component message, Runnable onPress) {
        return new Builder(message, onPress);
    }

    public static class Builder {
        public final Component message;
        public final Runnable onPress;
        @Nullable
        public Tooltip tooltip;
        public int x;
        public int y;
        public int width = 150;
        public int height = 20;
        public Button.CreateNarration createNarration = DEFAULT_NARRATION;
        protected static final Button.CreateNarration DEFAULT_NARRATION = Supplier::get;
        public Builder(Component message, Runnable onPress) {
            this.message = message;
            this.onPress = onPress;
        }

        public Builder pos(int x, int y) {
            this.x = x;
            this.y = y;
            return this;
        }

        public Builder width(int width) {
            this.width = width;
            return this;
        }

        public Builder size(int width, int height) {
            this.width = width;
            this.height = height;
            return this;
        }

        public Builder bounds(int x, int y, int width, int height) {
            return this.pos(x, y).size(width, height);
        }

        public Builder tooltip(@Nullable Tooltip tooltip) {
            this.tooltip = tooltip;
            return this;
        }

        public Builder createNarration(Button.CreateNarration createNarration) {
            this.createNarration = createNarration;
            return this;
        }
    }
}
