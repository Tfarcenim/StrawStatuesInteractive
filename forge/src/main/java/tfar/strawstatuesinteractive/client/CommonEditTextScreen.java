package tfar.strawstatuesinteractive.client;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableInt;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public abstract class CommonEditTextScreen extends Screen {

    protected static final int IMAGE_WIDTH = 320;
    protected static final int IMAGE_HEIGHT = 240;

    /**
     * Whether the book's title or contents has been modified since being opened
     */
    protected boolean isModified;
    /**
     * In milliseconds
     */
    protected long lastClickTime;
    protected int lastIndex = -1;
    @Nullable
    protected EditDialogueScreen.DisplayCache displayCache = DisplayCache.EMPTY;
    /**
     * Update ticks since the gui was opened
     */
    protected int frameTick;

    protected int textHeight = 128;
    protected int textWidth = 114;

    protected final TextFieldHelper pageEdit = new TextFieldHelper(this::getCurrentPageText, this::setCurrentPageText, this::getClipboard, this::setClipboard, (p_280853_) -> {
        return p_280853_.length() < 2048 && this.font.wordWrapHeight(p_280853_, textWidth) <= textHeight;
    });

    protected CommonEditTextScreen(Component title,int textWidth,int textHeight) {
        super(title);
        this.textWidth = textWidth;
        this.textHeight = textHeight;
    }

    static int findLineFromPos(int[] lineStarts, int find) {
        int i = Arrays.binarySearch(lineStarts, find);
        return i < 0 ? -(i + 2) : i;
    }

    protected Rect2i createSelection(Pos2i corner1, Pos2i corner2) {
        Pos2i bookeditscreen$pos2i = this.convertLocalToScreen(corner1);
        Pos2i bookeditscreen$pos2i1 = this.convertLocalToScreen(corner2);
        int i = Math.min(bookeditscreen$pos2i.x, bookeditscreen$pos2i1.x);
        int j = Math.max(bookeditscreen$pos2i.x, bookeditscreen$pos2i1.x);
        int k = Math.min(bookeditscreen$pos2i.y, bookeditscreen$pos2i1.y);
        int l = Math.max(bookeditscreen$pos2i.y, bookeditscreen$pos2i1.y);
        return new Rect2i(i, k, j - i, l - k);
    }

    protected void clearDisplayCache() {
        this.displayCache = null;
    }

    protected DisplayCache getDisplayCache() {
        if (this.displayCache == null) {
            this.displayCache = this.rebuildDisplayCache();
        }

        return this.displayCache;
    }

    private DisplayCache rebuildDisplayCache() {
        String s = this.getCurrentPageText();
        if (s.isEmpty()) {
            return DisplayCache.EMPTY;
        } else {
            int i = this.pageEdit.getCursorPos();
            int j = this.pageEdit.getSelectionPos();
            IntList intlist = new IntArrayList();
            List<LineInfo> list = Lists.newArrayList();
            MutableInt mutableint = new MutableInt();
            MutableBoolean mutableboolean = new MutableBoolean();
            StringSplitter stringsplitter = this.font.getSplitter();
            stringsplitter.splitLines(s, textWidth, Style.EMPTY, true, (style, p_98133_, p_98134_) -> {
                int k3 = mutableint.getAndIncrement();
                String s2 = s.substring(p_98133_, p_98134_);
                mutableboolean.setValue(s2.endsWith("\n"));
                String s3 = StringUtils.stripEnd(s2, " \n");
                int l3 = k3 * 9;
                Pos2i bookeditscreen$pos2i1 = this.convertLocalToScreen(new Pos2i(0, l3));
                intlist.add(p_98133_);
                list.add(new LineInfo(style, s3, bookeditscreen$pos2i1.x, bookeditscreen$pos2i1.y));
            });
            int[] aint = intlist.toIntArray();
            boolean flag = i == s.length();
            Pos2i bookeditscreen$pos2i;
            if (flag && mutableboolean.isTrue()) {
                bookeditscreen$pos2i = new Pos2i(0, list.size() * 9);
            } else {
                int k = EditNPCCommandsScreen.findLineFromPos(aint, i);
                int l = this.font.width(s.substring(aint[k], i));
                bookeditscreen$pos2i = new Pos2i(l, k * 9);
            }

            List<Rect2i> list1 = Lists.newArrayList();
            if (i != j) {
                int l2 = Math.min(i, j);
                int i1 = Math.max(i, j);
                int j1 = EditNPCCommandsScreen.findLineFromPos(aint, l2);
                int k1 = EditNPCCommandsScreen.findLineFromPos(aint, i1);
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
                        list1.add(this.createSelection(new Pos2i(0, j2), new Pos2i(k2, j2 + 9)));
                    }

                    list1.add(this.createPartialLineSelection(s, stringsplitter, aint[k1], i1, k1 * 9, aint[k1]));
                }
            }

            return new DisplayCache(s, bookeditscreen$pos2i, flag, aint, list.toArray(new LineInfo[0]), list1.toArray(new Rect2i[0]));
        }
    }

    protected Rect2i createPartialLineSelection(String input, StringSplitter splitter, int startPos, int endPos, int y, int lineStart) {
        String s = input.substring(lineStart, startPos);
        String s1 = input.substring(lineStart, endPos);
        Pos2i bookeditscreen$pos2i = new Pos2i((int)splitter.stringWidth(s), y);
        Pos2i bookeditscreen$pos2i1 = new Pos2i((int)splitter.stringWidth(s1), y + 9);
        return this.createSelection(bookeditscreen$pos2i, bookeditscreen$pos2i1);
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
                int j = bookeditscreen$displaycache.getIndexAtPosition(this.font, this.convertScreenToLocal(new Pos2i((int)mouseX, (int)mouseY)));
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

    private void setClipboard(String clipboardValue) {
        if (this.minecraft != null) {
            TextFieldHelper.setClipboardContents(this.minecraft, clipboardValue);
        }

    }

    private String getClipboard() {
        return this.minecraft != null ? TextFieldHelper.getClipboardContents(this.minecraft) : "";
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
         * Renders the graphical user interface (GUI) element.
         *
         * @param guiGraphics the GuiGraphics object used for rendering.
         * @param mouseX      the x-coordinate of the mouse cursor.
         * @param mouseY      the y-coordinate of the mouse cursor.
         * @param partialTick the partial tick time.
         */
        @Override
        public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            this.renderBackground(guiGraphics);

            int i = (this.width - IMAGE_WIDTH) / 2;
            renderBg(guiGraphics, mouseX, mouseY, partialTick);
            guiGraphics.drawString(this.font, this.title,i +8,6, 0x404040, false);
            this.setFocused(null);
            DisplayCache bookeditscreen$displaycache = this.getDisplayCache();

            for(LineInfo bookeditscreen$lineinfo : bookeditscreen$displaycache.lines) {
                guiGraphics.drawString(this.font, bookeditscreen$lineinfo.asComponent, bookeditscreen$lineinfo.x, bookeditscreen$lineinfo.y, -16777216, false);
            }

            this.renderHighlight(guiGraphics, bookeditscreen$displaycache.selection);
            this.renderCursor(guiGraphics, bookeditscreen$displaycache.cursor, bookeditscreen$displaycache.cursorAtEnd);
            super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    public void renderBg(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        int i = (this.width - IMAGE_WIDTH) / 2;
        guiGraphics.blitNineSlicedSized(AbstractScreen.BACKGROUND,i,0,
                IMAGE_WIDTH,IMAGE_HEIGHT,4,4,12,12,0,0,12,12);
    }

    protected abstract String getCurrentPageText();
    protected abstract void setCurrentPageText(String text);

    protected void renderHighlight(GuiGraphics guiGraphics, Rect2i[] highlightAreas) {
        for(Rect2i rect2i : highlightAreas) {
            int i = rect2i.getX();
            int j = rect2i.getY();
            int k = i + rect2i.getWidth();
            int l = j + rect2i.getHeight();
            guiGraphics.fill(RenderType.guiTextHighlight(), i, j, k, l, -16776961);
        }
    }

    @Override
    protected void init() {
        this.clearDisplayCache();
    }

    @Override
    public void tick() {
        super.tick();
        ++this.frameTick;
    }

    protected void renderCursor(GuiGraphics guiGraphics, Pos2i cursorPos, boolean isEndOfText) {
        if (this.frameTick / 6 % 2 == 0) {
            cursorPos = this.convertLocalToScreen(cursorPos);
            if (!isEndOfText) {
                guiGraphics.fill(cursorPos.x, cursorPos.y - 1, cursorPos.x + 1, cursorPos.y + 9, -16777216);
            } else {
                guiGraphics.drawString(this.font, "_", cursorPos.x, cursorPos.y, 0, false);
            }
        }
    }

    /**
     * Handles keypresses, clipboard functions, and page turning
     */
    protected boolean bookKeyPressed(int keyCode, int scanCode, int modifiers) {
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
            TextFieldHelper.CursorStep textfieldhelper$cursorstep = Screen.hasControlDown() ? TextFieldHelper.CursorStep.WORD : TextFieldHelper.CursorStep.CHARACTER;
            return switch (keyCode) {
                case 257, 335 -> {
                    this.pageEdit.insertText("\n");
                    yield true;
                }
                case 259 -> {
                    this.pageEdit.removeFromCursor(-1, textfieldhelper$cursorstep);
                    yield true;
                }
                case 261 -> {
                    this.pageEdit.removeFromCursor(1, textfieldhelper$cursorstep);
                    yield true;
                }
                case 262 -> {
                    this.pageEdit.moveBy(1, Screen.hasShiftDown(), textfieldhelper$cursorstep);
                    yield true;
                }
                case 263 -> {
                    this.pageEdit.moveBy(-1, Screen.hasShiftDown(), textfieldhelper$cursorstep);
                    yield true;
                }
                case GLFW.GLFW_KEY_DOWN -> {
                    this.keyDown();
                    yield true;
                }
                case GLFW.GLFW_KEY_UP -> {
                    this.keyUp();
                    yield true;
                }
                // case 266:
                //this.backButton.onPress();
                //   return true;
                // case 267:
                // this.forwardButton.onPress();
                //  return true;
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

    private void changeLine(int yChange) {
        int i = this.pageEdit.getCursorPos();
        int j = this.getDisplayCache().changeLine(i, yChange);
        this.pageEdit.setCursorPos(j, Screen.hasShiftDown());
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

    protected abstract Pos2i convertScreenToLocal(Pos2i screenPos);

    protected abstract Pos2i convertLocalToScreen(Pos2i localScreenPos);

    /**
     * Called when the mouse is dragged within the GUI element.
     * <p>
     * @return {@code true} if the event is consumed, {@code false} otherwise.
     *
     * @param mouseX the X coordinate of the mouse.
     * @param mouseY the Y coordinate of the mouse.
     * @param button the button that is being dragged.
     * @param dragX  the X distance of the drag.
     * @param dragY  the Y distance of the drag.
     */
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (super.mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
            return true;
        } else {
            if (button == 0) {
                DisplayCache bookeditscreen$displaycache = this.getDisplayCache();
                int i = bookeditscreen$displaycache.getIndexAtPosition(this.font, this.convertScreenToLocal(new Pos2i((int)mouseX, (int)mouseY)));
                this.pageEdit.setCursorPos(i, true);
                this.clearDisplayCache();
            }

            return true;
        }
    }

    public static class DisplayCache {
        public static final DisplayCache EMPTY = new DisplayCache("", new EditDialogueScreen.Pos2i(0, 0), true, new int[]{0}, new EditDialogueScreen.LineInfo[]{new EditDialogueScreen.LineInfo(Style.EMPTY, "", 0, 0)}, new Rect2i[0]);
        protected final String fullText;
        final EditDialogueScreen.Pos2i cursor;
        final boolean cursorAtEnd;
        private final int[] lineStarts;
        final EditDialogueScreen.LineInfo[] lines;
        final Rect2i[] selection;

        public DisplayCache(String fullText, Pos2i cursor, boolean cursorAtEnd, int[] lineStarts, EditDialogueScreen.LineInfo[] lines, Rect2i[] selection) {
            this.fullText = fullText;
            this.cursor = cursor;
            this.cursorAtEnd = cursorAtEnd;
            this.lineStarts = lineStarts;
            this.lines = lines;
            this.selection = selection;
        }

        public int getIndexAtPosition(Font font, Pos2i cursorPosition) {
            int i = cursorPosition.y / 9;
            if (i < 0) {
                return 0;
            } else if (i >= this.lines.length) {
                return this.fullText.length();
            } else {
                EditDialogueScreen.LineInfo bookeditscreen$lineinfo = this.lines[i];
                return this.lineStarts[i] + font.getSplitter().plainIndexAtWidth(bookeditscreen$lineinfo.contents, cursorPosition.x, bookeditscreen$lineinfo.style);
            }
        }

        public int changeLine(int xChange, int yChange) {
            int i = CommonEditTextScreen.findLineFromPos(this.lineStarts, xChange);
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
            int i = CommonEditTextScreen.findLineFromPos(this.lineStarts, line);
            return this.lineStarts[i];
        }

        public int findLineEnd(int line) {
            int i = CommonEditTextScreen.findLineFromPos(this.lineStarts, line);
            return this.lineStarts[i] + this.lines[i].contents.length();
        }
    }

    public static class LineInfo {
        public final Style style;
        public final String contents;
        public final Component asComponent;
        public final int x;
        public final int y;

        public LineInfo(Style style, String contents, int x, int y) {
            this.style = style;
            this.contents = contents;
            this.x = x;
            this.y = y;
            this.asComponent = Component.literal(contents).setStyle(style);
        }
    }

    protected record Pos2i(int x, int y) {}
}
