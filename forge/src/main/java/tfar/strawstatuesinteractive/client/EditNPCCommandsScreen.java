package tfar.strawstatuesinteractive.client;

import fuzs.strawstatues.world.entity.decoration.StrawStatue;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.MutableComponent;
import tfar.strawstatuesinteractive.NPCCommandEntry;

public class EditNPCCommandsScreen extends CommonEditTextScreen {
    private static final int IMAGE_WIDTH = 320;
    private static final int IMAGE_HEIGHT = 240;
    static final int PAGE_TEXT_X_OFFSET = 8;

    private final StrawStatue strawStatue;
    private final NPCCommandEntry npcCommandEntry;

    private String page = "";

    public EditNPCCommandsScreen(MutableComponent commands, StrawStatue strawStatue, NPCCommandEntry npcCommandEntry) {
        super(commands,300,224);
        this.strawStatue = strawStatue;
        this.npcCommandEntry = npcCommandEntry;
        loadString();
    }

    void loadString() {
        StringBuilder full = new StringBuilder();
        String s = String.join("\n", npcCommandEntry.commands);
        setCurrentPageText(s);
        this.pageEdit.setCursorToEnd(false);
    }

    @Override
    protected String getCurrentPageText() {
        return page != null ? page : "";
    }

    @Override
    protected void setCurrentPageText(String text) {
        this.page = text;
        this.isModified = true;
        this.clearDisplayCache();
    }


    @Override
    public void onClose() {
        super.onClose();
        saveChanges();
    }

    private void saveChanges() {
        if (this.isModified) {
            npcCommandEntry.commands.clear();
            String[] strings = displayCache.fullText.split("/");
            for (String s : strings) {
                s = s.replace("\n","");
                if (!s.isBlank()) {
                    npcCommandEntry.commands.add("/"+s);
                }
            }
        }
    }

    @Override
    public void renderBg(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderBg(guiGraphics, mouseX, mouseY, partialTick);
        int i = (this.width - IMAGE_WIDTH) / 2;

        guiGraphics.blitNineSlicedSized(AbstractScreen.SLOT,i+5,15,
                IMAGE_WIDTH-10,IMAGE_HEIGHT-20,4,4,12,12,0,0,12,12);
    }

    @Override
    protected Pos2i convertScreenToLocal(Pos2i screenPos) {
        return new Pos2i(screenPos.x() - (this.width - IMAGE_WIDTH) / 2 - PAGE_TEXT_X_OFFSET, screenPos.y() - 18);
    }

    @Override
    protected Pos2i convertLocalToScreen(Pos2i localScreenPos) {
        return new Pos2i(localScreenPos.x() + (this.width - IMAGE_WIDTH) / 2 + PAGE_TEXT_X_OFFSET, localScreenPos.y() + 18);
    }

}
