package net.blay09.mods.cookingforblockheads.client.gui;

import com.mojang.logging.LogUtils;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractScrollWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class CraftableScroller extends AbstractScrollWidget {

    private static final org.slf4j.Logger LOGGER = LogUtils.getLogger();
    private int rowCount;

    public CraftableScroller(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    @Override
    protected void setScrollAmount(double scrollAmount) {
        LOGGER.warn("new setScrollAmount: {}", scrollAmount);
        super.setScrollAmount(scrollAmount);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        LOGGER.warn("new mouseScrolled: {}, {}, {}, {}", mouseX, mouseY, scrollX, scrollY);
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        LOGGER.warn("new mouseClicked: {}, {}, {}", mouseX, mouseY, button);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected int getInnerHeight() {
        return rowCount * 18;
    }

    @Override
    protected void renderContents(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // NOOP We're a facade for the slot scroller.
    }

    @Override
    protected void renderBackground(GuiGraphics guiGraphics) {
        // NOOP Texture shines through.
    }

    @Override
    protected double scrollRate() {
        return 18;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        // NOOP
    }
}
