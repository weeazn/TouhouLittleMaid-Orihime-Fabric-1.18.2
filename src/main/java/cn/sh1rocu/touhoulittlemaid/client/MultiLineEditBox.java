package cn.sh1rocu.touhoulittlemaid.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Mth;
import org.lwjgl.glfw.GLFW;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class MultiLineEditBox extends AbstractWidget {
    private final Font font;
    private final Component hint;
    private final TextFieldHelper editor;
    private final List<Line> lines = new ArrayList<>();
    private Consumer<String> listener = value -> {};
    private String value = "";
    private int limit = Integer.MAX_VALUE;
    private int firstLine;

    public MultiLineEditBox(Font font, int x, int y, int width, int height, Component hint, Component title) {
        super(x, y, width, height, title);
        this.font = font;
        this.hint = hint;
        Minecraft client = Minecraft.getInstance();
        editor = new TextFieldHelper(() -> value, this::setValue, TextFieldHelper.createClipboardGetter(client),
                TextFieldHelper.createClipboardSetter(client), text -> text.length() <= limit);
        layout();
    }

    public void setValue(String text) {
        value = text.substring(0, Math.min(limit, text.length()));
        layout();
        editor.setSelectionRange(Math.min(editor.getCursorPos(), value.length()), Math.min(editor.getSelectionPos(), value.length()));
        listener.accept(value);
    }

    public String getValue() { return value; }
    public int getX() { return x; }
    public int getY() { return y; }
    public void setCharacterLimit(int limit) { this.limit = limit; }
    public void setValueListener(Consumer<String> listener) { this.listener = listener; }

    private void layout() {
        lines.clear();
        font.getSplitter().splitLines(value, Math.max(1, width - 8), Style.EMPTY, true,
                (style, start, end) -> lines.add(new Line(start, end)));
        if (lines.isEmpty() || value.endsWith("\n")) lines.add(new Line(value.length(), value.length()));
        firstLine = Mth.clamp(firstLine, 0, Math.max(0, lines.size() - visibleRows()));
    }

    private int visibleRows() { return Math.max(1, (height - 8) / font.lineHeight); }
    private int rowAt(int cursor) {
        for (int row = lines.size() - 1; row > 0; row--) if (cursor >= lines.get(row).start) return row;
        return 0;
    }

    private String text(Line line) {
        int end = line.end;
        if (end > line.start && value.charAt(end - 1) == '\n') end--;
        return value.substring(line.start, end);
    }

    private void reveal() {
        int row = rowAt(editor.getCursorPos());
        firstLine = Mth.clamp(firstLine, Math.max(0, row - visibleRows() + 1), row);
    }

    private int offsetAt(double mouseX, double mouseY) {
        int row = Mth.clamp(firstLine + (int)Math.floor((mouseY - y - 4) / font.lineHeight), 0, lines.size() - 1);
        Line line = lines.get(row);
        return line.start + font.getSplitter().plainIndexAtWidth(text(line), Math.max(0, (int)mouseX - x - 4), Style.EMPTY);
    }

    @Override public void onClick(double mx, double my) {
        setFocused(true);
        editor.setCursorPos(offsetAt(mx, my), Screen.hasShiftDown());
    }
    @Override protected void onDrag(double mx, double my, double dx, double dy) {
        editor.setCursorPos(offsetAt(mx, my), true);
        reveal();
    }
    @Override public boolean mouseScrolled(double mx, double my, double dy) {
        firstLine = Mth.clamp(firstLine - (int)Math.signum(dy), 0, Math.max(0, lines.size() - visibleRows()));
        return true;
    }
    @Override public boolean charTyped(char c, int modifiers) {
        if (!isFocused() || !editor.charTyped(c)) return false;
        reveal();
        return true;
    }

    @Override public boolean keyPressed(int key, int scan, int modifiers) {
        if (!isFocused()) return false;
        if (key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_KP_ENTER) {
            editor.insertText("\n");
        } else if (key == GLFW.GLFW_KEY_UP || key == GLFW.GLFW_KEY_DOWN) {
            int pos = editor.getCursorPos();
            int row = rowAt(pos);
            int widthAtCursor = font.width(value.substring(lines.get(row).start, pos));
            Line target = lines.get(Mth.clamp(row + (key == GLFW.GLFW_KEY_UP ? -1 : 1), 0, lines.size() - 1));
            editor.setCursorPos(target.start + font.getSplitter().plainIndexAtWidth(text(target), widthAtCursor, Style.EMPTY), Screen.hasShiftDown());
        } else if (!editor.keyPressed(key)) return false;
        reveal();
        return true;
    }

    @Override public void renderButton(PoseStack pose, int mx, int my, float delta) {
        fill(pose, x, y, x + width, y + height, isFocused() ? 0xffeeeeee : 0xff999999);
        fill(pose, x + 1, y + 1, x + width - 1, y + height - 1, 0xff111111);
        int cursor = editor.getCursorPos();
        int mark = editor.getSelectionPos();
        int cursorRow = rowAt(cursor);
        if (value.isEmpty() && !isFocused()) font.draw(pose, hint, x + 4, y + 4, 0x888888);
        for (int row = firstLine; row < Math.min(lines.size(), firstLine + visibleRows()); row++) {
            Line line = lines.get(row);
            int top = y + 4 + (row - firstLine) * font.lineHeight;
            String display = text(line);
            int start = Mth.clamp(Math.min(cursor, mark) - line.start, 0, display.length());
            int end = Mth.clamp(Math.max(cursor, mark) - line.start, 0, display.length());
            if (end > start) fill(pose, x + 4 + font.width(display.substring(0,start)), top,
                    x + 4 + font.width(display.substring(0,end)), top + font.lineHeight, 0xff365688);
            font.draw(pose, display, x + 4, top, 0xffffff);
            if (isFocused() && row == cursorRow && (System.currentTimeMillis() / 500) % 2 == 0) {
                int caret = x + 4 + font.width(display.substring(0, Math.min(cursor - line.start, display.length())));
                fill(pose, caret, top, caret + 1, top + font.lineHeight, 0xffeeeeee);
            }
        }
    }

    @Override public void updateNarration(NarrationElementOutput output) { defaultButtonNarrationText(output); }
    private record Line(int start, int end) {}
}
