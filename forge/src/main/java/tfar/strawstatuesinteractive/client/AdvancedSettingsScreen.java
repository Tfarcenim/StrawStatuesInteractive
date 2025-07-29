package tfar.strawstatuesinteractive.client;

import com.mojang.blaze3d.systems.RenderSystem;
import fuzs.strawstatues.world.entity.decoration.StrawStatue;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import tfar.strawstatuesinteractive.CommandEntry;
import tfar.strawstatuesinteractive.client.widgets.ScrollableButton;
import tfar.strawstatuesinteractive.client.widgets.ScrollableEditBox;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AdvancedSettingsScreen extends AbstractConfiguringScreen{



    protected AdvancedSettingsScreen(Component title, StrawStatue strawStatue) {
        super(title);
        this.strawStatue = strawStatue;
    }

    private DetailsList list;
    List<CommandEntry> commandEntries = new ArrayList<>();

    @Override
    protected void init() {
        super.init();
        this.list = new DetailsList();
        this.addWidget(this.list);
       this.addRenderableWidget(Button.builder(Component.literal("Add Command"), button -> {
           addCommand();
        }).bounds(this.width / 2 -50 , 205, 100, 20).build());
    }

    void addCommand() {
        list.addEntry();
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderLabels(guiGraphics, mouseX, mouseY);
       // guiGraphics.drawString(font,Component.literal(""))
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.list.render(guiGraphics, mouseX, mouseY, partialTick);
    }


    public void resize(Minecraft minecraft, int width, int height) {
        commandEntries.clear();
        for (DetailsList.Entry entry: list.children() ) {
            commandEntries.add(entry.commandEntry);
        }
        super.resize(minecraft,width,height);
    }

    public static final int margin_bottom = 45;
    public static final int margin_top = 16;

    public class DetailsList extends ObjectSelectionList<DetailsList.Entry> {

        public DetailsList() {
            super(AdvancedSettingsScreen.this.minecraft, AdvancedSettingsScreen.this.imageWidth,
                    AdvancedSettingsScreen.this.imageHeight- margin_bottom-margin_top, topPos+margin_top,
                    topPos+imageHeight- margin_bottom, 60);

            setLeftPos(leftPos);
            setRenderBackground(false);
            setRenderTopAndBottom(false);

            for(int i = 0; i < commandEntries.size(); i++) {
                this.addEntry(new DetailsList.Entry(commandEntries.get(i)));
            }
        }

        @Override
        public int getRowWidth() {
            return 240;
        }

        public void setSelected(@Nullable DetailsList.Entry entry) {
            super.setSelected(entry);
        }

        protected int getScrollbarPosition() {
            return this.getRowWidth()+getRowLeft();
        }

        protected void renderSelection(GuiGraphics guiGraphics, int top, int width, int height, int outerColor, int innerColor) {
         //   int i = this.x0 + (this.width - width) / 2;
         //   int j = this.x0 + (this.width + width) / 2;
        //    guiGraphics.fill(i, top - 2, j, top + height + 2, outerColor);
        //    guiGraphics.fill(i + 1, top - 1, j - 1, top + height + 1, innerColor);
        }


        public void resetRows() {
            int i = this.children().indexOf(this.getSelected());
            this.clearEntries();

           // for(int j = 0; j < this.generator.getLayersInfo().size(); ++j) {
           //     this.addEntry(new DetailsList.Entry());
           // }

            List<DetailsList.Entry> list = this.children();
            if (i >= 0 && i < list.size()) {
                this.setSelected(list.get(i));
            }
        }

        void addEntry() {
            children().add(new DetailsList.Entry());
        }

        public class Entry extends ObjectSelectionList.Entry<DetailsList.Entry> implements  GuiEventListener {

            CommandEntry commandEntry;

            ScrollableButton mode;
            ScrollableButton enter;
            ScrollableButton exit;

            ScrollableButton focusedButton;

            List<ScrollableButton> buttons = new ArrayList<>();

            ScrollableEditBox editBox;

            //button mode
            //on enter
            //on exit
            Entry(CommandEntry commandEntry) {
                this.commandEntry = commandEntry;
                mode = new ScrollableCheckbox(ScrollableButton.builder(Component.literal("I"), () -> pressMode()).bounds(10, 20, 20, 20),this,commandEntry.buttonMode);
                enter = new ScrollableCheckbox(ScrollableButton.builder(Component.literal("I"), () ->  pressEnter()).bounds(95, 20, 20, 20),this,commandEntry.onEnter);
                exit = new ScrollableCheckbox(ScrollableButton.builder(Component.literal("I"), () ->  pressExit()).bounds(175, 20, 20, 20),this,commandEntry.onExit);

                editBox = new ScrollableEditBox(minecraft.font,20,20,100,12,this);

                buttons.add(mode);
                buttons.add(enter);
                buttons.add(exit);
            }

            Entry() {
                this(new CommandEntry());
            }


            void pressMode() {
                commandEntry.buttonMode = !commandEntry.buttonMode;
            }

            void pressEnter() {
                commandEntry.onEnter = !commandEntry.onEnter;
            }
            void pressExit() {
                commandEntry.onExit= !commandEntry.onExit;
            }

            public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
                Component component = Component.literal("Command "+index).withStyle(ChatFormatting.DARK_GRAY);
                guiGraphics.drawString(font, component, left + 4, top + 4, 0xffffff, false);

                Component buttonModeC = Component.literal("Button Mode").withStyle(ChatFormatting.DARK_GRAY);
                guiGraphics.drawString(font, buttonModeC, left + 35, top + 24, 0xffffff, false);
                Component onEnterC = Component.literal("On Enter").withStyle(ChatFormatting.DARK_GRAY);
                guiGraphics.drawString(font, onEnterC, left + 120, top + 24, 0xffffff, false);
                Component onExitC = Component.literal("On Exit").withStyle(ChatFormatting.DARK_GRAY);
                guiGraphics.drawString(font, onExitC, left + 200, top + 24, 0xffffff, false);


                for (ScrollableButton button : buttons) {
                    button.render(guiGraphics, index, top, left, width, height, mouseX, mouseY,
                            Objects.equals(DetailsList.this.getHovered(), this), partialTick);
                }
            }

            @Override
            public void renderBack(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTick) {

                if (this == list.getSelected()) {
                    RenderSystem.setShaderColor(.5f,1,.5f,1);
                }
                guiGraphics.blitNineSlicedSized(BACKGROUND,left,top,
                        width,height,4,4,12,12,0,0,12,12);
                RenderSystem.setShaderColor(1,1,1,1);
            }

            public Component getNarration() {
                return CommonComponents.EMPTY;
            }

            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                if (button == 0) {
                    DetailsList.this.setSelected(this);
                    handleSubButtons(mouseX,mouseY,button);
                    return true;
                } else {
                    return false;
                }
            }

            public int getTopPos() {
                return DetailsList.this.getRowTop(getIndex());
            }

            public int getIndex() {
                return children().indexOf(this);
            }

            public int getLeftPos() {
                return DetailsList.this.getRowLeft();
            }

            public boolean handleSubButtons(double mouseX, double mouseY, int button) {
                for(ScrollableButton scrollableButton : buttons) {
                    if (scrollableButton.mouseClicked(mouseX, mouseY, button)) {
                        this.focusedButton = scrollableButton;
                      //  if (button == 0) {
                      //      this.setDragging(true);
                      //  }

                        return true;
                    }
                }
                return false;
            }


            public static class ScrollableCheckbox extends ScrollableButton {

                private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/checkbox.png");

                public boolean selected;



                @Override
                public boolean mouseClicked(double mouseX, double mouseY, int button) {
                    return super.mouseClicked(mouseX, mouseY, button);
                }

                public ScrollableCheckbox(ScrollableButton.Builder builder, Entry parent, boolean selected) {
                    super(builder,parent);
                    this.selected = selected;
                }

                @Override
                public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTick) {
                    super.render(guiGraphics, index, top, left, width, height, mouseX, mouseY, isMouseOver, partialTick);
                    Minecraft minecraft = Minecraft.getInstance();
                    RenderSystem.enableDepthTest();
                    Font font = minecraft.font;
                    guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1);
                    RenderSystem.enableBlend();
                    guiGraphics.blit(TEXTURE, left+x, top+y, this.isFocused() ? 20 : 0, this.selected ? 20 : 0,
                            20, this.height, 64, 64);
                    guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
                 //   if (this.showLabel) {
                 //       guiGraphics.drawString(font, this.getMessage(), this.getX() + 24, this.getY() + (this.height - 8) / 2, 14737632 | Mth.ceil(this.alpha * 255.0F) << 24);
                 //   }

                }

                @Override
                public void onClick(double mouseX, double mouseY) {
                    super.onClick(mouseX, mouseY);
                    selected = !selected;
                }
            }
        }
    }
}
