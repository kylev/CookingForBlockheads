package net.blay09.mods.cookingforblockheads.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import net.blay09.mods.balm.mixin.AbstractContainerScreenAccessor;
import net.blay09.mods.cookingforblockheads.CookingForBlockheads;
import net.blay09.mods.cookingforblockheads.CookingForBlockheadsConfig;
import net.blay09.mods.cookingforblockheads.client.gui.CraftableScroller;
import net.blay09.mods.cookingforblockheads.client.gui.SortButton;
import net.blay09.mods.cookingforblockheads.crafting.RecipeWithStatus;
import net.blay09.mods.cookingforblockheads.menu.KitchenMenu;
import net.blay09.mods.cookingforblockheads.menu.slot.CraftMatrixFakeSlot;
import net.blay09.mods.cookingforblockheads.menu.slot.CraftableListingFakeSlot;
import net.blay09.mods.cookingforblockheads.registry.CookingForBlockheadsRegistry;
import net.blay09.mods.cookingforblockheads.tag.ModItemTags;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.FittingMultiLineTextWidget;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.ArrayList;
import java.util.List;

public class KitchenScreen extends AbstractContainerScreen<KitchenMenu> {

    private static final int SCROLLBAR_COLOR = 0xFFAAAAAA;
    private static final int SCROLLBAR_X1 = 160;
    private static final int SCROLLBAR_X2 = SCROLLBAR_X1 + 7;
    private static final int SCROLLBAR_Y1 = 18;
    private static final int SCROLLBAR_Y_SIZE = 77;

    private static final int SCROLLBAR_HEIGHT = 77;

    private static final ResourceLocation guiTexture = ResourceLocation.fromNamespaceAndPath(CookingForBlockheads.MOD_ID, "textures/gui/gui.png");
    private static final int VISIBLE_ROWS = 4;
    private static final int VISIBLE_COLS = 3;

    private static final Component ARROW_PREV = Component.literal("<");
    private static final Component ARROW_NEXT = Component.literal(">");
    private static final Component NO_INGREDIENTS = Component.translatable("gui.cookingforblockheads.no_ingredients");
    private static final Component NO_SELECTION = Component.translatable("gui.cookingforblockheads.no_selection");

    private static final org.slf4j.Logger LOGGER = LogUtils.getLogger();

    private int scrollBarScaledHeight;
    private int scrollBarYPos;
    private int currentOffset;

    private double mouseClickY = -1;
    private int indexWhenClicked;
    private int lastNumberOfMoves;

    private Button btnNextRecipe;
    private Button btnPrevRecipe;
    private FittingMultiLineTextWidget textNoIngredients;
    private FittingMultiLineTextWidget textNoSelection;
    private CraftableScroller craftableScroller;

    private EditBox searchBar;

    private final List<SortButton> sortButtons = new ArrayList<>();

    public KitchenScreen(KitchenMenu menu, Inventory playerInventory, Component displayName) {
        super(menu, playerInventory, displayName);
        this.imageHeight = 184;
        this.imageWidth = 176;
    }

    @Override
    protected void init() {
        super.init();

        btnPrevRecipe = Button.builder(ARROW_PREV, it -> menu.nextRecipe(-1))
                .pos(leftPos + 9, topPos + 44).size(13, 24).build();
        addRenderableWidget(btnPrevRecipe);

        btnNextRecipe = Button.builder(ARROW_NEXT, it -> menu.nextRecipe(1))
                .pos(leftPos + 78, topPos + 44).size(13, 24).build();
        addRenderableWidget(btnNextRecipe);

        searchBar = new EditBox(minecraft.font, leftPos + 97, topPos + 5, 72, 10, Component.literal(""));
        searchBar.setResponder(s -> menu.search(s));
        addRenderableWidget(searchBar);

        textNoSelection = new FittingMultiLineTextWidget(leftPos + 10, topPos + 20, 80, 73, NO_SELECTION, minecraft.font);
        addRenderableWidget(textNoSelection);

        textNoIngredients = new FittingMultiLineTextWidget(leftPos + 100, topPos + 20, 66, 73, NO_INGREDIENTS, minecraft.font);
        addRenderableWidget(textNoIngredients);

        craftableScroller = new CraftableScroller(leftPos + 98, topPos + 18, 62, 77, Component.literal(""));
        addRenderableOnly(craftableScroller);

        int yOffset = 10;
        for (final var sortButton : CookingForBlockheadsRegistry.getSortButtons()) {
            SortButton button = new SortButton(leftPos + imageWidth - 1, topPos + yOffset, sortButton, it -> {
                menu.setSortComparator(sortButton.getComparator(Minecraft.getInstance().player));
            });
            addRenderableWidget(button);
            sortButtons.add(button);

            yOffset += 20;
        }
    }

    @Override
    protected void setInitialFocus() {
        super.setInitialFocus(this.searchBar);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double deltaX, double deltaY) {
        LOGGER.warn("mouseScrolled: {} {} {} {}", mouseX, mouseY, deltaX, deltaY);
    //     if (deltaY == 0) {
    //         return false;
    //     }

    //     if (menu.getSelectedRecipe() != null && mouseX >= leftPos + 24 && mouseY >= topPos + 20 && mouseX < leftPos + 78 && mouseY < topPos + 74) {
    //         Slot slot = ((AbstractContainerScreenAccessor) this).getHoveredSlot();
    //         if (slot instanceof CraftMatrixFakeSlot fakeSlot && fakeSlot.getVisibleStacks().size() > 1) {
    //             final var lockedInput = fakeSlot.scrollDisplayListAndLock(deltaY > 0 ? -1 : 1);
    //             menu.setLockedInput(fakeSlot.getIngredientIndex(), lockedInput);
    //         }
    //     } else {
        //     }
        if (craftableScroller.mouseScrolled(mouseX, mouseY, deltaX, deltaY)) {
            return true;
        }
        setCurrentOffset(deltaY > 0 ? currentOffset - 1 : currentOffset + 1);

        return super.mouseScrolled(mouseX, mouseY, deltaX, deltaY);
    }

    // @Override
    // public boolean mouseReleased(double mouseX, double mouseY, int state) {
    //     boolean result = super.mouseReleased(mouseX, mouseY, state);

    //     if (state != -1 && mouseClickY != -1) {
    //         mouseClickY = -1;
    //         indexWhenClicked = 0;
    //         lastNumberOfMoves = 0;
    //     }

    //     return result;
    // }

    // @Override
    // public boolean mouseClicked(double mouseX, double mouseY, int button) {
    //     LOGGER.warn("mouseClicked: {} {} {}", mouseX, mouseY, button);

    //     if (mouseX >= SCROLLBAR_X1 && mouseX <= SCROLLBAR_X2 && mouseY >= scrollBarYPos && mouseY <= scrollBarYPos + scrollBarScaledHeight) {
    //         mouseClickY = mouseY;
    //         indexWhenClicked = currentOffset;
    //     }

    //     Slot mouseSlot = ((AbstractContainerScreenAccessor) this).getHoveredSlot();
    //     if (mouseSlot instanceof CraftMatrixFakeSlot fakeSlot) {
    //         if (button == 0) {
    //             ItemStack itemStack = mouseSlot.getItem();
    //             RecipeWithStatus recipe = menu.findRecipeForResultItem(itemStack);
    //             if (recipe != null) {
    //                 menu.selectCraftable(recipe);
    //                 // setCurrentOffset(menu.getRecipesForSelectionIndex());
    //             }
    //         } else if (button == 1) {
    //             final var lockedInput = fakeSlot.toggleLock();
    //             menu.setLockedInput(fakeSlot.getIngredientIndex(), lockedInput);
    //         }
    //         return true;
    //     }

    //     return super.mouseClicked(mouseX, mouseY, button);
    // }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        guiGraphics.blit(guiTexture, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        btnPrevRecipe.visible = menu.selectionHasRecipeVariants();
        btnPrevRecipe.active = menu.selectionHasPreviousRecipe();
        btnNextRecipe.visible = menu.selectionHasRecipeVariants();
        btnNextRecipe.active = menu.selectionHasNextRecipe();

        boolean hasRecipes = menu.getItemListCount() > 0;
        textNoIngredients.visible = !hasRecipes;
        for (Button sortButton : sortButtons) {
            sortButton.active = hasRecipes;
        }

        final var selection = menu.getSelectedRecipe();
        textNoSelection.visible = selection == null;

        super.render(guiGraphics, mouseX, mouseY, partialTicks);

        // Add our parts
        // renderScrollbar(guiGraphics);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        if (selection != null) {
            if (selection.recipe(Minecraft.getInstance().player).value().getType() == RecipeType.SMELTING) {
                guiGraphics.blit(guiTexture, leftPos + 23, topPos + 29, 54, 184, 54, 54);
            } else {
                guiGraphics.blit(guiTexture, leftPos + 23, topPos + 29, 0, 184, 54, 54);
            }

            for (CraftMatrixFakeSlot slot : menu.getMatrixSlots()) {
                if (slot.isLocked() && slot.getVisibleStacks().size() > 1) {
                    guiGraphics.blit(guiTexture, leftPos + slot.x, topPos + slot.y, 176, 60, 16, 16);
                }
            }
        }

        if (!hasRecipes) {
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
            guiGraphics.fill(leftPos + 97, topPos + 17, leftPos + 168, topPos + 95, 0xAA222222);
        }
    }

    private void renderScrollbar(GuiGraphics guiGraphics) {
        // TODO Calculate correctly.
        int listingRows = 1 + (menu.getItemListCount() / 3);
        float visiblePct = Math.min(1f, VISIBLE_ROWS / (float)listingRows);
        int scrollBarTop = SCROLLBAR_Y1 + (int) (currentOffset / (float)listingRows * SCROLLBAR_HEIGHT);
        int scrollBarBottom = scrollBarTop + Math.max(1, (int) (visiblePct * SCROLLBAR_HEIGHT));

        guiGraphics.fill(leftPos + SCROLLBAR_X1, topPos + scrollBarTop, leftPos + SCROLLBAR_X2, topPos + scrollBarBottom, SCROLLBAR_COLOR);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.setColor(1f, 1f, 1f, 1f);
        if (CookingForBlockheadsConfig.getActive().showIngredientIcon) {
            var poseStack = guiGraphics.pose();
            poseStack.pushPose();
            poseStack.translate(0, 0, 300);
            for (Slot slot : menu.slots) {
                if (slot instanceof CraftableListingFakeSlot fakeSlot) {
                    if (slot.getItem().is(ModItemTags.INGREDIENTS)) {
                        guiGraphics.blit(guiTexture, slot.x, slot.y, 176, 76, 16, 16);
                    }

                    final var recipe = fakeSlot.getCraftable();
                    if (recipe != null && recipe.isMissingUtensils()) {
                        guiGraphics.blit(guiTexture, slot.x, slot.y, 176, 92, 16, 16);
                    }
                }
            }

            poseStack.popPose();
        }
    }

    private void setCurrentOffset(int currentOffset) {
        this.currentOffset = Math.max(0, Math.min(currentOffset, (int) Math.ceil(menu.getItemListCount() / (float) VISIBLE_COLS) - VISIBLE_ROWS));

        menu.setScrollOffset(this.currentOffset);
    }

    public List<Button> getSortingButtons() {
        return new ArrayList<>(sortButtons);
    }

}
