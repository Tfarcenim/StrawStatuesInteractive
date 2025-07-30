package tfar.strawstatuesinteractive.client.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import tfar.strawstatuesinteractive.NPCCommandEntry;
import tfar.strawstatuesinteractive.client.*;

import java.util.List;

public class ScrollableCommandPreview extends ScrollableWidget {
    public ScrollableCommandPreview(int x, int y, int width, int height, AdvancedSettingsScreen.DetailsList.Entry parent) {
        super(x, y, width, height, Component.empty(), parent);
    }

    @Override
    public void renderScrollable(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTick) {

        guiGraphics.blitNineSlicedSized(AbstractScreen.SLOT,left+x,top+y,
                this.width,this.height,4,4,12,12,0,0,12,12);

        Font font = Minecraft.getInstance().font;
        NPCCommandEntry npcCommandEntry= parent.npcCommandEntry;
        List<String> commands = npcCommandEntry.commands;
        int maxWidth = 200;
        if (!commands.isEmpty()) {
            guiGraphics.enableScissor(left+x, top + y, left+x+this.width-1, top + y + this.height);
            String command0 = commands.get(0);
            guiGraphics.drawString(font,command0,left+x+2,top+y+3,0xff000000,false);
            if (commands.size()>1) {
                guiGraphics.drawString(font,commands.get(1),left+x+2,top+y+3+font.lineHeight,0xff000000,false);
            }
            guiGraphics.disableScissor();
        }
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        Minecraft.getInstance().pushGuiLayer(new EditNPCCommandsScreen(Component.literal("Commands"),
                ((AdvancedSettingsScreen)Minecraft.getInstance().screen).strawStatue,parent.npcCommandEntry));
    }
}
