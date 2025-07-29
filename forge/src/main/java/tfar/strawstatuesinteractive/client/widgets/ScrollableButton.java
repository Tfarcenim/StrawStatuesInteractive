package tfar.strawstatuesinteractive.client.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import tfar.strawstatuesinteractive.client.AdvancedSettingsScreen;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class ScrollableButton extends ScrollableWidget {



    private final Runnable onClickEvent;
    private boolean renderBackground;

    public ScrollableButton(Builder builder, AdvancedSettingsScreen.DetailsList.Entry parent) {
        this(builder.x, builder.y, builder.width, builder.height,builder.message,builder.onPress, builder.renderBackground, parent);
    }

    public ScrollableButton(int x, int y, int width, int height, Component message, Runnable onClickEvent, boolean renderBackground, AdvancedSettingsScreen.DetailsList.Entry parent) {
        super(x, y, width, height,message, parent);
        this.onClickEvent = onClickEvent;
        this.renderBackground = renderBackground;
    }

    @Override
    public void renderScrollable(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTick) {
        if (renderBackground) {
            renderBackground(guiGraphics, index, top, left, width, height, mouseX, mouseY, isMouseOver, partialTick);
        }
        guiGraphics.drawString(Minecraft.getInstance().font,message ,left+x,top+y,0xffffff);
        //renderString(guiGraphics, Minecraft.getInstance().font,0xffffffff,left,top);
    }

    public void renderBackground(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTick) {

    }

    public void onClick(double mouseX, double mouseY) {
        onClickEvent.run();
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
        boolean renderBackground = true;
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

        public Builder disableBackground() {
            renderBackground = false;
            return this;
        }

        public Builder createNarration(Button.CreateNarration createNarration) {
            this.createNarration = createNarration;
            return this;
        }
    }
}
