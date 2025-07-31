package tfar.strawstatuesinteractive.client;

import fuzs.strawstatues.world.entity.decoration.StrawStatue;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.joml.Quaternionf;
import tfar.strawstatuesinteractive.Dialogue;
import tfar.strawstatuesinteractive.StrawStatueDuck;
import tfar.strawstatuesinteractive.network.SetDialoguePacket;

import java.util.List;

public class EditDialogueScreen extends CommonEditTextScreen {
    private final StrawStatue strawStatue;
    private String page = "";

    public static final int TEXT_WIDTH = 180;
    public static final int TEXT_HEIGHT = 200;
    public static final int PAGE_TEXT_X_OFFSET = 99;


    public EditDialogueScreen(Component title, StrawStatue strawStatue) {
        super(title,TEXT_WIDTH,TEXT_HEIGHT,PAGE_TEXT_X_OFFSET);
        this.strawStatue = strawStatue;
        loadString();
    }

    void loadString() {
        StrawStatueDuck strawStatueDuck = StrawStatueDuck.of(strawStatue);
        Dialogue dialogue = strawStatueDuck.getDialogue();
        if (dialogue != null) {
            List<String> pages = dialogue.pages();
            setCurrentPageText(pages.isEmpty() ? "" : pages.get(0));
            this.pageEdit.setCursorToEnd(false);
        }
    }

    @Override
    public void onClose() {
        super.onClose();
        saveChanges();
    }

    private void saveChanges() {
        if (this.isModified) {
            SetDialoguePacket.updateDialogue(strawStatue,List.of(page),null);
        }
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

    /**
     * Renders the graphical user interface (GUI) element.
     *
     * @param guiGraphics the GuiGraphics object used for rendering.
     * @param mouseX      the x-coordinate of the mouse cursor.
     * @param mouseY      the y-coordinate of the mouse cursor.
     * @param partialTick the partial tick time.
     */
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        int i = (this.width - IMAGE_WIDTH) / 2;
        int j = (height - IMAGE_HEIGHT)/2;

        super.render(guiGraphics, mouseX, mouseY, partialTick);

        if (strawStatue != null) {
            float yBodyRot = (float) (((ArmorStand)strawStatue).yBodyRot * Math.PI/180 + Math.PI/8);
            InventoryScreen.renderEntityInInventory(guiGraphics, i+50, j+150, 64,
                    new Quaternionf().rotateXYZ((float) Math.PI,/*Util.getMillis() / 200f*/yBodyRot,0), null, strawStatue);
        }
    }
}
