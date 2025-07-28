package tfar.strawstatuesinteractive.client;

import com.mojang.blaze3d.systems.RenderSystem;
import fuzs.strawstatues.world.entity.decoration.StrawStatue;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;
import java.util.List;

public class AdvancedSettingsScreen extends AbstractConfiguringScreen{



    protected AdvancedSettingsScreen(Component title, StrawStatue strawStatue) {
        super(title);
        this.strawStatue = strawStatue;
    }

    private DetailsList list;


    @Override
    protected void init() {
        super.init();
        this.list = new DetailsList();
        this.addWidget(this.list);
       this.addRenderableWidget(Button.builder(Component.literal("Add Command"), (button) -> {
           addCommand();
        }).bounds(this.width / 2 -50 , 200, 100, 20).build());
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

    public static final int margin_bottom = 40;
    public static final int margin_top = 16;

    private class DetailsList extends ObjectSelectionList<DetailsList.Entry> {

        public DetailsList() {
            super(AdvancedSettingsScreen.this.minecraft, AdvancedSettingsScreen.this.imageWidth,
                    AdvancedSettingsScreen.this.imageHeight- margin_bottom-margin_top, topPos+margin_top,
                    topPos+imageHeight- margin_bottom, 60);

            setLeftPos(leftPos);
            setRenderBackground(false);
            setRenderTopAndBottom(false);

            for(int i = 0; i < 0; ++i) {
                this.addEntry(new DetailsList.Entry());
            }
        }

        public void setSelected(@Nullable DetailsList.Entry entry) {
            super.setSelected(entry);
        }

        protected int getScrollbarPosition() {
            return this.width+5;
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

        class Entry extends ObjectSelectionList.Entry<DetailsList.Entry> {
            Entry() {
            }

            public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
                Component component = Component.literal("Command").withStyle(ChatFormatting.DARK_GRAY);



                guiGraphics.drawString(font, component, left + 4, top + 4, 0xffffff, false);


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
                    return true;
                } else {
                    return false;
                }
            }
        }
    }
}
