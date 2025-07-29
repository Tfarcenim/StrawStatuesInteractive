package tfar.strawstatuesinteractive.client;

import com.google.common.collect.Lists;
import fuzs.strawstatues.world.entity.decoration.StrawStatue;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.BookEditScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableInt;
import org.joml.Quaternionf;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;
import tfar.strawstatuesinteractive.Dialogue;
import tfar.strawstatuesinteractive.StrawStatueDuck;
import tfar.strawstatuesinteractive.network.SetDialoguePacket;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

public class EditDialogueScreen extends AbstractConfiguringScreen {

    private static final int TEXT_WIDTH = 180;
    private static final int TEXT_HEIGHT = 176;

    private final TextFieldHelper pageEdit = new TextFieldHelper(this::getCurrentPageText, this::setCurrentPageText, this::getClipboard, this::setClipboard, (string) -> {
        return string.length() < 1024 && this.font.wordWrapHeight(string, TEXT_WIDTH) <= TEXT_HEIGHT;
    });

    /**
     * Update ticks since the gui was opened
     */
    private int frameTick;
    private int currentPage;
    private final List<String> pages = Lists.newArrayList();

    /**
     * Whether the book's title or contents has been modified since being opened
     */
    private boolean isModified;

    private long lastClickTime;
    private int lastIndex = -1;

    @Nullable
    private DisplayCache displayCache;

    private Component pageMsg = CommonComponents.EMPTY;

    private Button doneButton;


    public EditDialogueScreen(Component title,StrawStatue strawStatue) {
        super(title);
        this.displayCache = DisplayCache.EMPTY;
        this.strawStatue = strawStatue;

        loadPages();


    }

    void loadPages() {

        Dialogue dialogue = StrawStatueDuck.of(strawStatue).getDialogue();
        if (dialogue != null) {
            pages.addAll(dialogue.pages());
        }

        if (this.pages.isEmpty()) {
            this.pages.add("");
        }
    }

    private String getCurrentPageText() {
        return pages != null && this.currentPage >= 0 && this.currentPage < this.pages.size() ? this.pages.get(this.currentPage) : "";
    }

    private void setCurrentPageText(String text) {
        if (this.currentPage >= 0 && this.currentPage < this.pages.size()) {
            this.pages.set(this.currentPage, text);
            this.isModified = true;
            this.clearDisplayCache();
        }

    }

    private DisplayCache getDisplayCache() {
        if (this.displayCache == null) {
            this.displayCache = this.rebuildDisplayCache();
            this.pageMsg = Component.translatable("book.pageIndicator", this.currentPage + 1, this.getNumPages());
        }

        return this.displayCache;
    }

    private int getNumPages() {
        return this.pages.size();
    }


    private void clearDisplayCache() {
        this.displayCache = null;
    }

    private void clearDisplayCacheAfterPageChange() {
        this.pageEdit.setCursorToEnd();
        this.clearDisplayCache();
    }



    private DisplayCache rebuildDisplayCache() {
        String s = this.getCurrentPageText();
        if (s.isEmpty()) {
            return DisplayCache.EMPTY;
        } else {
            int cursorPos = this.pageEdit.getCursorPos();
            int j = this.pageEdit.getSelectionPos();
            IntList intlist = new IntArrayList();
            List<BookEditScreen.LineInfo> list = Lists.newArrayList();
            MutableInt mutableint = new MutableInt();
            MutableBoolean mutableboolean = new MutableBoolean();
            StringSplitter stringsplitter = this.font.getSplitter();
            stringsplitter.splitLines(s, TEXT_WIDTH, Style.EMPTY, true, (p_98132_, p_98133_, p_98134_) -> {
                int k3 = mutableint.getAndIncrement();
                String s2 = s.substring(p_98133_, p_98134_);
                mutableboolean.setValue(s2.endsWith("\n"));
                String s3 = StringUtils.stripEnd(s2, " \n");
                int l3 = k3 * 9;
                Vector2i bookeditscreen$pos2i1 = this.convertLocalToScreen(new Vector2i(0, l3));
                intlist.add(p_98133_);
                list.add(new BookEditScreen.LineInfo(p_98132_, s3, bookeditscreen$pos2i1.x, bookeditscreen$pos2i1.y));
            });
            int[] aint = intlist.toIntArray();
            boolean eol = cursorPos == s.length();
            Vector2i vector2i;
            if (eol && mutableboolean.isTrue()) {
                vector2i = new Vector2i(0, list.size() * 9);
            } else {
                int k = findLineFromPos(aint, cursorPos);
                int l = this.font.width(s.substring(aint[k], cursorPos));
                vector2i = new Vector2i(l, k * 9);
            }

            List<Rect2i> list1 = Lists.newArrayList();
            if (cursorPos != j) {
                int l2 = Math.min(cursorPos, j);
                int i1 = Math.max(cursorPos, j);
                int j1 = findLineFromPos(aint, l2);
                int k1 = findLineFromPos(aint, i1);
                if (j1 == k1) {
                    int l1 = j1 * 9;
                    int i2 = aint[j1];
                    list1.add(this.createPartialLineSelection(s, stringsplitter, l2, i1, l1, i2));
                } else {
                    int i3 = j1 + 1 > aint.length ? s.length() : aint[j1 + 1];
                    list1.add(this.createPartialLineSelection(s, stringsplitter, l2, i3, j1 * 9, aint[j1]));

                    for(int j3 = j1 + 1; j3 < k1; ++j3) {
                        int j2 = j3 * 9;
                        String s1 = s.substring(aint[j3], aint[j3 + 1]);
                        int k2 = (int)stringsplitter.stringWidth(s1);
                        list1.add(this.createSelection(new Vector2i(0, j2), new Vector2i(k2, j2 + 9)));
                    }

                    list1.add(this.createPartialLineSelection(s, stringsplitter, aint[k1], i1, k1 * 9, aint[k1]));
                }
            }

            return new DisplayCache(s, vector2i, eol, aint, list.toArray(new BookEditScreen.LineInfo[0]), list1.toArray(new Rect2i[0]));
        }
    }

    /**
     * Called when a keyboard key is pressed within the GUI element.
     * <p>
     * @return {@code true} if the event is consumed, {@code false} otherwise.
     *
     * @param keyCode   the key code of the pressed key.
     * @param scanCode  the scan code of the pressed key.
     * @param modifiers the keyboard modifiers.
     */
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        } else {
            boolean flag = this.bookKeyPressed(keyCode, scanCode, modifiers);
            if (flag) {
                this.clearDisplayCache();
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * Handles keypresses, clipboard functions, and page turning
     */
    private boolean bookKeyPressed(int keyCode, int scanCode, int modifiers) {
        if (Screen.isSelectAll(keyCode)) {
            this.pageEdit.selectAll();
            return true;
        } else if (Screen.isCopy(keyCode)) {
            this.pageEdit.copy();
            return true;
        } else if (Screen.isPaste(keyCode)) {
            this.pageEdit.paste();
            return true;
        } else if (Screen.isCut(keyCode)) {
            this.pageEdit.cut();
            return true;
        } else {
            TextFieldHelper.CursorStep cursorStep = Screen.hasControlDown() ? TextFieldHelper.CursorStep.WORD : TextFieldHelper.CursorStep.CHARACTER;
            return switch (keyCode) {
                case GLFW.GLFW_KEY_ENTER, GLFW.GLFW_KEY_KP_ENTER -> {
                    this.pageEdit.insertText("\n");
                    yield true;
                }
                case 259 -> {
                    this.pageEdit.removeFromCursor(-1, cursorStep);
                    yield true;
                }
                case 261 -> {
                    this.pageEdit.removeFromCursor(1, cursorStep);
                    yield true;
                }
                case 262 -> {
                    this.pageEdit.moveBy(1, Screen.hasShiftDown(), cursorStep);
                    yield true;
                }
                case 263 -> {
                    this.pageEdit.moveBy(-1, Screen.hasShiftDown(), cursorStep);
                    yield true;
                }
                case 264 -> {
                    this.keyDown();
                    yield true;
                }
                case GLFW.GLFW_KEY_UP -> {
                    this.keyUp();
                    yield true;
                }
                case 266 ->
                    //  this.backButton.onPress();
                        true;
                case 267 ->
                    //  this.forwardButton.onPress();
                        true;
                case 268 -> {
                    this.keyHome();
                    yield true;
                }
                case 269 -> {
                    this.keyEnd();
                    yield true;
                }
                default -> false;
            };
        }
    }

    private void keyUp() {
        this.changeLine(-1);
    }

    private void keyDown() {
        this.changeLine(1);
    }


    private void keyHome() {
        if (Screen.hasControlDown()) {
            this.pageEdit.setCursorToStart(Screen.hasShiftDown());
        } else {
            int i = this.pageEdit.getCursorPos();
            int j = this.getDisplayCache().findLineStart(i);
            this.pageEdit.setCursorPos(j, Screen.hasShiftDown());
        }

    }

    private void keyEnd() {
        if (Screen.hasControlDown()) {
            this.pageEdit.setCursorToEnd(Screen.hasShiftDown());
        } else {
            DisplayCache bookeditscreen$displaycache = this.getDisplayCache();
            int i = this.pageEdit.getCursorPos();
            int j = bookeditscreen$displaycache.findLineEnd(i);
            this.pageEdit.setCursorPos(j, Screen.hasShiftDown());
        }

    }

    public static class DisplayCache {
        static final DisplayCache EMPTY = new DisplayCache("", new Vector2i(0, 0), true, new int[]{0},
                new BookEditScreen.LineInfo[]{new BookEditScreen.LineInfo(Style.EMPTY, "", 0, 0)}, new Rect2i[0]);
        private final String fullText;
        final Vector2i cursor;
        final boolean cursorAtEnd;
        private final int[] lineStarts;
        final BookEditScreen.LineInfo[] lines;
        final Rect2i[] selection;

        public DisplayCache(String fullText,Vector2i cursor, boolean cursorAtEnd, int[] lineStarts, BookEditScreen.LineInfo[] lines, Rect2i[] selection) {
            this.fullText = fullText;
            this.cursor = cursor;
            this.cursorAtEnd = cursorAtEnd;
            this.lineStarts = lineStarts;
            this.lines = lines;
            this.selection = selection;
        }

        public int getIndexAtPosition(Font font, Vector2i cursorPosition) {
            int i = cursorPosition.y / 9;
            if (i < 0) {
                return 0;
            } else if (i >= this.lines.length) {
                return this.fullText.length();
            } else {
                BookEditScreen.LineInfo lineInfo = this.lines[i];
                return this.lineStarts[i] + font.getSplitter().plainIndexAtWidth(lineInfo.contents, cursorPosition.x, lineInfo.style);
            }
        }

        public int changeLine(int xChange, int yChange) {
            int i = findLineFromPos(this.lineStarts, xChange);
            int j = i + yChange;
            int k;
            if (0 <= j && j < this.lineStarts.length) {
                int l = xChange - this.lineStarts[i];
                int i1 = this.lines[j].contents.length();
                k = this.lineStarts[j] + Math.min(l, i1);
            } else {
                k = xChange;
            }

            return k;
        }

        public int findLineStart(int line) {
            int i = findLineFromPos(this.lineStarts, line);
            return this.lineStarts[i];
        }

        public int findLineEnd(int line) {
            int i = findLineFromPos(this.lineStarts, line);
            return this.lineStarts[i] + this.lines[i].contents.length();
        }
    }

    static int findLineFromPos(int[] lineStarts, int find) {
        int i = Arrays.binarySearch(lineStarts, find);
        return i < 0 ? -(i + 2) : i;
    }

    private Rect2i createPartialLineSelection(String input, StringSplitter splitter, int startPos, int endPos, int y, int lineStart) {
        String s = input.substring(lineStart, startPos);
        String s1 = input.substring(lineStart, endPos);
        Vector2i bookeditscreen$pos2i = new Vector2i((int)splitter.stringWidth(s), y);
        Vector2i bookeditscreen$pos2i1 = new Vector2i((int)splitter.stringWidth(s1), y + 9);
        return this.createSelection(bookeditscreen$pos2i, bookeditscreen$pos2i1);
    }

    private Rect2i createSelection(Vector2i corner1, Vector2i corner2) {
        Vector2i vector2i = this.convertLocalToScreen(corner1);
        Vector2i vector2i1 = this.convertLocalToScreen(corner2);
        int i = Math.min(vector2i.x, vector2i1.x);
        int j = Math.max(vector2i.x, vector2i1.x);
        int k = Math.min(vector2i.y, vector2i1.y);
        int l = Math.max(vector2i.y, vector2i1.y);
        return new Rect2i(i, k, j - i, l - k);
    }

    private Vector2i convertScreenToLocal(Vector2i screenPos) {
        return new Vector2i(screenPos.x - (this.width - 192) / 2 - 36, screenPos.y - 32);
    }

    private Vector2i convertLocalToScreen(Vector2i localScreenPos) {
        return new Vector2i(localScreenPos.x + (this.width - 192) / 2 + 36, localScreenPos.y + 32);
    }

    private void setClipboard(String clipboardValue) {
        if (this.minecraft != null) {
            TextFieldHelper.setClipboardContents(this.minecraft, clipboardValue);
        }

    }

    private String getClipboard() {
        return this.minecraft != null ? TextFieldHelper.getClipboardContents(this.minecraft) : "";
    }


    public static final Quaternionf ARMOR_STAND_ANGLE = new Quaternionf().rotationXYZ((float) Math.PI, 0.0F, 0);


    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        super.render(guiGraphics, mouseX, mouseY, partialTick);


        int j1 = this.font.width(this.pageMsg);

        int i = (this.width - 192) / 2;

        guiGraphics.drawString(this.font, this.pageMsg, i - j1 + 192 - 44, 18, 0, false);
        DisplayCache cache = this.getDisplayCache();

        for(BookEditScreen.LineInfo lineInfo : cache.lines) {
            guiGraphics.drawString(this.font, lineInfo.asComponent, lineInfo.x, lineInfo.y, 0xff000000, false);
        }

        this.renderHighlight(guiGraphics, cache.selection);
        this.renderCursor(guiGraphics, cache.cursor, cache.cursorAtEnd);

        if (strawStatue != null) {
            InventoryScreen.renderEntityInInventory(guiGraphics, leftPos+50, topPos+164, 64,
                    new Quaternionf().rotationXYZ((float) Math.PI, 0, 0), null, strawStatue);
        }
    }

    private void renderCursor(GuiGraphics guiGraphics, Vector2i cursorPos, boolean isEndOfText) {
        if (this.frameTick / 6 % 2 == 0) {
            cursorPos = this.convertLocalToScreen(cursorPos);
            if (!isEndOfText) {
                guiGraphics.fill(cursorPos.x, cursorPos.y - 1, cursorPos.x + 1, cursorPos.y + 9, 0xff000000);
            } else {
                guiGraphics.drawString(this.font, "_", cursorPos.x, cursorPos.y, 0, false);
            }
        }

    }

    private void renderHighlight(GuiGraphics guiGraphics, Rect2i[] highlightAreas) {
        for(Rect2i rect2i : highlightAreas) {
            int i = rect2i.getX();
            int j = rect2i.getY();
            int k = i + rect2i.getWidth();
            int l = j + rect2i.getHeight();
            guiGraphics.fill(RenderType.guiTextHighlight(), i, j, k, l, 0xff0000ff);
        }

    }

    @Override
    public void tick() {
        super.tick();
        ++this.frameTick;
    }

    @Override
    protected void init() {
        super.init();
        this.clearDisplayCache();

        this.doneButton = this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (p_280851_) -> {
            this.minecraft.setScreen(null);
            this.saveChanges();
        }).bounds(this.width / 2 + 2, 196, 98, 20).build());
    }

    private void eraseEmptyTrailingPages() {
        ListIterator<String> listiterator = this.pages.listIterator(this.pages.size());

        while(listiterator.hasPrevious() && listiterator.previous().isEmpty()) {
            listiterator.remove();
        }

    }

    private void saveChanges() {
        if (this.isModified ||true) {
            this.eraseEmptyTrailingPages();
            //this.updateLocalCopy(publish);
            SetDialoguePacket.updateDialogue(strawStatue,pages,null);
        }
    }

    /**
     * Called when a character is typed within the GUI element.
     * <p>
     * @return {@code true} if the event is consumed, {@code false} otherwise.
     *
     * @param codePoint the code point of the typed character.
     * @param modifiers the keyboard modifiers.
     */
    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (super.charTyped(codePoint, modifiers)) {
            return true;
        } else if (SharedConstants.isAllowedChatCharacter(codePoint)) {
            this.pageEdit.insertText(Character.toString(codePoint));
            this.clearDisplayCache();
            return true;
        } else {
            return false;
        }
    }


    /**
     * Called when a mouse button is clicked within the GUI element.
     * <p>
     * @return {@code true} if the event is consumed, {@code false} otherwise.
     *
     * @param mouseX the X coordinate of the mouse.
     * @param mouseY the Y coordinate of the mouse.
     * @param button the button that was clicked.
     */
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        } else {
            if (button == 0) {
                long i = Util.getMillis();
                DisplayCache bookeditscreen$displaycache = this.getDisplayCache();
                int j = bookeditscreen$displaycache.getIndexAtPosition(this.font, this.convertScreenToLocal(new Vector2i((int)mouseX, (int)mouseY)));
                if (j >= 0) {
                    if (j == this.lastIndex && i - this.lastClickTime < 250L) {
                        if (!this.pageEdit.isSelecting()) {
                            this.selectWord(j);
                        } else {
                            this.pageEdit.selectAll();
                        }
                    } else {
                        this.pageEdit.setCursorPos(j, Screen.hasShiftDown());
                    }

                    this.clearDisplayCache();
                }

                this.lastIndex = j;
                this.lastClickTime = i;
            }

            return true;
        }
    }

    private void selectWord(int index) {
        String s = this.getCurrentPageText();
        this.pageEdit.setSelectionRange(StringSplitter.getWordPosition(s, -1, index, false), StringSplitter.getWordPosition(s, 1, index, false));
    }


    private void changeLine(int yChange) {
        int i = this.pageEdit.getCursorPos();
        int j = this.getDisplayCache().changeLine(i, yChange);
        this.pageEdit.setCursorPos(j, Screen.hasShiftDown());
    }
}
