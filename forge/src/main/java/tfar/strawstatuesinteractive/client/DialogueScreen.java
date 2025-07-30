package tfar.strawstatuesinteractive.client;

import com.mojang.datafixers.util.Pair;
import fuzs.strawstatues.world.entity.decoration.StrawStatue;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import org.joml.Quaternionf;
import tfar.strawstatuesinteractive.Dialogue;
import tfar.strawstatuesinteractive.NPCCommandEntry;
import tfar.strawstatuesinteractive.StrawStatueDuck;
import tfar.strawstatuesinteractive.network.server.C2SDialogueButtonPacket;
import tfar.strawstatuesinteractive.platform.Services;

import javax.annotation.Nullable;
import java.util.*;

public class DialogueScreen extends AbstractScreen {

    private final StrawStatue strawStatue;
    private int currentPage;
    private int cachedPage = -1;
    private Component pageMsg = CommonComponents.EMPTY;
    private final BookViewScreen.BookAccess bookAccess;
    private List<FormattedCharSequence> cachedPageComponents = Collections.emptyList();


    protected static final int TEXT_WIDTH = 114;
    protected static final int TEXT_HEIGHT = 128;

    public DialogueScreen(Component title, StrawStatue strawStatue) {
        super(title);
        this.strawStatue = strawStatue;
        bookAccess = new EntityAccess();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        int i = (this.width - 192) / 2;

        if (this.cachedPage != this.currentPage) {
            FormattedText formattedtext = this.bookAccess.getPage(this.currentPage);
            this.cachedPageComponents = this.font.split(formattedtext, 114);
            this.pageMsg = Component.empty();//Component.translatable("book.pageIndicator", this.currentPage + 1, Math.max(this.bookAccess.getPageCount(), 1));
        }

        this.cachedPage = this.currentPage;
        int i1 = this.font.width(this.pageMsg);
        guiGraphics.drawString(this.font, this.pageMsg, i - i1 + 192 - 44, 18, 0, false);
        int k = Math.min(128 / 9, this.cachedPageComponents.size());

        for(int l = 0; l < k; ++l) {
            FormattedCharSequence formattedcharsequence = this.cachedPageComponents.get(l);
            guiGraphics.drawString(this.font, formattedcharsequence, i + 36, 32 + l * 9, 0, false);
        }

        Style style = this.getClickedComponentStyleAt(mouseX, mouseY);
        if (style != null) {
            guiGraphics.renderComponentHoverEffect(this.font, style, mouseX, mouseY);
        }

        if (strawStatue != null) {
            float yBodyRot = (float) (((ArmorStand)strawStatue).yBodyRot * Math.PI/180 + Math.PI/8);
            InventoryScreen.renderEntityInInventory(guiGraphics, leftPos+50, topPos+160, 64,
                    new Quaternionf().rotateXYZ((float) Math.PI,/*Util.getMillis() / 200f*/yBodyRot,0), null, strawStatue);
        }
    }

    @Nullable
    public Style getClickedComponentStyleAt(double mouseX, double mouseY) {
        if (this.cachedPageComponents.isEmpty()) {
            return null;
        } else {
            int i = Mth.floor(mouseX - (double)((this.width - 192) / 2) - 36.0D);
            int j = Mth.floor(mouseY - 2.0D - 30.0D);
            if (i >= 0 && j >= 0) {
                int k = Math.min(128 / 9, this.cachedPageComponents.size());
                if (i <= 114 && j < 9 * k + k) {
                    int l = j / 9;
                    if (l >= 0 && l < this.cachedPageComponents.size()) {
                        FormattedCharSequence formattedcharsequence = this.cachedPageComponents.get(l);
                        return this.minecraft.font.getSplitter().componentStyleAtWidth(formattedcharsequence, i);
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }
    }


    @Override
    protected void init() {
        super.init();

        Dialogue dialogue = StrawStatueDuck.of(strawStatue).getDialogue();

        if (dialogue != null) {
            List<NPCCommandEntry> commandEntries = dialogue.commands();

            List<Pair<Integer,NPCCommandEntry>> buttonCommands = new ArrayList<>();

            for (int i= 0; i < commandEntries.size();i++ ) {
                NPCCommandEntry entry = commandEntries.get(i);
                if (entry.buttonMode) {
                    buttonCommands.add(Pair.of(i, entry));
                }
            }

            int buttonWidth = 100;
            for (int j = 0; j < buttonCommands.size(); j++) {
                Pair<Integer, NPCCommandEntry> entry = buttonCommands.get(j);
                int i = entry.getFirst();

                int row = j / 3;
                int colum = j % 3;

                Button button = Button.builder(Component.literal(entry.getSecond().name), button1 -> pressDialogueButton(i))
                        .bounds(leftPos+5+colum*105,topPos+ 162+row * 21, buttonWidth, 20).build();
                addRenderableWidget(button);
            }
        }
    }

    void pressDialogueButton(int i) {
        Services.PLATFORM.sendToServer(new C2SDialogueButtonPacket(((ArmorStand)strawStatue).getId(),i));
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    public class EntityAccess implements BookViewScreen.BookAccess {

        @Nullable
        public Dialogue getDialogue() {
            return StrawStatueDuck.of(strawStatue).getDialogue();
        }

        @Override
        public int getPageCount() {
            return getDialogue().pages().size();
        }

        @Override
        public FormattedText getPageRaw(int index) {
            String s = getDialogue().pages().get(index);

            try {
                FormattedText formattedtext = Component.Serializer.fromJson(s);
                if (formattedtext != null) {
                    return formattedtext;
                }
            } catch (Exception exception) {
            }

            return FormattedText.of(s);
        }
    }
}
