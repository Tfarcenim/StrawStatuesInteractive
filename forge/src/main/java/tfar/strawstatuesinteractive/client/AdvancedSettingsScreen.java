package tfar.strawstatuesinteractive.client;

import com.mojang.blaze3d.systems.RenderSystem;
import fuzs.strawstatues.world.entity.decoration.StrawStatue;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import tfar.strawstatuesinteractive.Dialogue;
import tfar.strawstatuesinteractive.NPCCommandEntry;
import tfar.strawstatuesinteractive.StrawStatueDuck;
import tfar.strawstatuesinteractive.client.widgets.ScrollableButton;
import tfar.strawstatuesinteractive.client.widgets.ScrollableCommandPreview;
import tfar.strawstatuesinteractive.client.widgets.ScrollableEditBox;
import tfar.strawstatuesinteractive.client.widgets.ScrollableWidget;
import tfar.strawstatuesinteractive.network.SetDialoguePacket;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AdvancedSettingsScreen extends AbstractConfiguringScreen{



    protected AdvancedSettingsScreen(Component title, StrawStatue strawStatue) {
        super(title);
        this.strawStatue = strawStatue;
        loadCommands();
    }

    void loadCommands() {

        Dialogue dialogue = StrawStatueDuck.of(strawStatue).getDialogue();
        if (dialogue != null) {
            commandEntries.addAll(dialogue.commands());
        }
    }

    private DetailsList list;
    List<NPCCommandEntry> commandEntries = new ArrayList<>();

    @Override
    protected void init() {
        super.init();
        this.list = new DetailsList();
        this.addWidget(this.list);
       this.addRenderableWidget(Button.builder(Component.literal("Add Command"), button -> {
           addCommand();
        }).bounds(this.width / 2 -100 , 205, 100, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("Save Changes"), button -> {
            saveChanges();
        }).bounds(this.width / 2, 205, 100, 20).build());
    }

    void addCommand() {
        list.addEntry();
    }

    void saveChanges() {
        extract();
        SetDialoguePacket.updateDialogue(strawStatue,null,commandEntries);
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

    void extract() {
        commandEntries.clear();
        for (DetailsList.Entry entry: list.children() ) {
            commandEntries.add(entry.npcCommandEntry);
        }
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        extract();
        super.resize(minecraft,width,height);
    }

    public static final int margin_bottom = 45;
    public static final int margin_top = 16;

    public static final int LIST_WIDTH = 250;

    public class DetailsList extends ObjectSelectionList<DetailsList.Entry> {

        public DetailsList() {
            super(AdvancedSettingsScreen.this.minecraft, LIST_WIDTH,
                    LIST_WIDTH- margin_bottom-margin_top, topPos+margin_top,
                    topPos+imageHeight- margin_bottom, 100);

            setLeftPos(leftPos+35);
            setRenderBackground(false);
            setRenderTopAndBottom(false);

            for(int i = 0; i < commandEntries.size(); i++) {
                this.addEntry(new DetailsList.Entry(commandEntries.get(i), i));
            }
        }

        @Override
        public int getRowWidth() {
            return LIST_WIDTH;
        }

        @Override
        public void setSelected(@Nullable DetailsList.Entry entry) {
            super.setSelected(entry);
        }

        @Override
        protected int getScrollbarPosition() {
            return this.getRowWidth()+getRowLeft();
        }

        @Override
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
            children().add(new DetailsList.Entry(children().size()));
        }

        public class Entry extends ObjectSelectionList.Entry<DetailsList.Entry> implements ContainerEventHandler {

            private boolean isDragging;

            public NPCCommandEntry npcCommandEntry;

            ScrollableButton mode;
            ScrollableButton enter;
            ScrollableButton exit;
            private int index;

            ScrollableWidget focusedButton;

            List<ScrollableWidget> buttons = new ArrayList<>();

            ScrollableEditBox editBox;

            ScrollableButton trash;

            ScrollableCommandPreview commandPreview;

            //button mode
            //on enter
            //on exit
            Entry(NPCCommandEntry npcCommandEntry, int index) {
                this.npcCommandEntry = npcCommandEntry;
                mode = new ScrollableCheckbox(ScrollableButton.builder(Component.literal("I"), () -> pressMode()).bounds(10, 50, 20, 20),this, npcCommandEntry.buttonMode);
                enter = new ScrollableCheckbox(ScrollableButton.builder(Component.literal("I"), () ->  pressEnter()).bounds(100, 50, 20, 20),this, npcCommandEntry.onEnter);
                exit = new ScrollableCheckbox(ScrollableButton.builder(Component.literal("I"), () ->  pressExit()).bounds(180, 50, 20, 20),this, npcCommandEntry.onExit);
                trash = new ScrollableButton(ScrollableButton.builder(Component.literal("\uD83D\uDDD1").withStyle(ChatFormatting.RED),
                        () ->  pressTrash()).bounds(LIST_WIDTH - 14, 2, 20, 20).disableBackground(),this);
                this.index = index;

                editBox = new ScrollableEditBox(minecraft.font,12,75,120,12,this);

                commandPreview = new ScrollableCommandPreview(4,15,DetailsList.this.getWidth()-12,24,this);

                editBox.setResponder(this::onNameChanged);
                editBox.setValue(npcCommandEntry.name);

                buttons.add(mode);
                buttons.add(enter);
                buttons.add(exit);
                buttons.add(editBox);
                buttons.add(trash);
                buttons.add(commandPreview);
            }

            private void onNameChanged(String s) {
                npcCommandEntry.name = s;
            }

            Entry(int index) {
                this(new NPCCommandEntry(),index);
            }


            void pressMode() {
                npcCommandEntry.buttonMode = !npcCommandEntry.buttonMode;
            }

            void pressEnter() {
                npcCommandEntry.onEnter = !npcCommandEntry.onEnter;
            }
            void pressExit() {
                npcCommandEntry.onExit= !npcCommandEntry.onExit;
            }

            void pressTrash() {
                AdvancedSettingsScreen.this.list.delete(this);
            }

            @Override
            public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
                Component component = Component.literal("Command "+index).withStyle(ChatFormatting.DARK_GRAY);
                guiGraphics.drawString(font, component, left + 4, top + 4, 0xffffff, false);

                int labelY = top + 55;
                Component buttonModeC = Component.literal("Button Mode").withStyle(ChatFormatting.DARK_GRAY);
                guiGraphics.drawString(font, buttonModeC, left + 35, labelY, 0xffffff, false);
                Component onEnterC = Component.literal("On Enter").withStyle(ChatFormatting.DARK_GRAY);
                guiGraphics.drawString(font, onEnterC, left + 125, labelY, 0xffffff, false);
                Component onExitC = Component.literal("On Exit").withStyle(ChatFormatting.DARK_GRAY);
                guiGraphics.drawString(font, onExitC, left + 205, labelY, 0xffffff, false);


                for (ScrollableWidget button : buttons) {
                    button.renderScrollable(guiGraphics, index, top, left, width, height, mouseX, mouseY,
                            Objects.equals(DetailsList.this.getHovered(), this), partialTick);
                }
            }

            @Override
            public void renderBack(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTick) {

                if (this == list.getSelected()) {
                    RenderSystem.setShaderColor(.5f,1,.5f,1);
                }
                guiGraphics.blitNineSlicedSized(BACKGROUND,left,top,
                        width-3,height,4,4,12,12,0,0,12,12);
                RenderSystem.setShaderColor(1,1,1,1);
            }

            @Override
            public Component getNarration() {
                return CommonComponents.EMPTY;
            }

            @Override
            public List<? extends GuiEventListener> children() {
                return buttons;
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                boolean handle = ContainerEventHandler.super.mouseClicked(mouseX,mouseY,button);
                if (button == 0) {
                    DetailsList.this.setSelected(this);
                    return true;
                } else {
                    return handle;
                }
            }

            @Override
            public boolean isDragging() {
                return isDragging;
            }

            @Override
            public void setDragging(boolean isDragging) {
                this.isDragging = isDragging;
            }

            @Nullable
            @Override
            public GuiEventListener getFocused() {
                return focusedButton;
            }

            @Override
            public void setFocused(@Nullable GuiEventListener focused) {
                if (this.focusedButton != null) {
                    this.focusedButton.setFocused(false);
                }

                if (focused != null) {
                    focused.setFocused(true);
                }

                this.focusedButton = (ScrollableWidget) focused;
            }

            public int getTopPos() {
                return DetailsList.this.getRowTop(getIndex());
            }

            public int getIndex() {
                return index;
            }

            public int getLeftPos() {
                return DetailsList.this.getRowLeft();
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
                public void renderScrollable(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTick) {
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

        private void delete(Entry entry) {
            remove(entry.index);
            for (int i = 0; i < children().size();i++) {
                Entry entry1 = children().get(i);
                entry1.index = i;
            }
        }
    }
}
