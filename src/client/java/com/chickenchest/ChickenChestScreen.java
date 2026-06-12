package com.chickenchest;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class ChickenChestScreen extends AbstractContainerScreen<ChickenChestMenu> {
	private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("chicken_chest", "textures/gui/chicken_chest_gui.png");

	public ChickenChestScreen(ChickenChestMenu menu, Inventory playerInventory, Component title) {
		super(menu, playerInventory, title, 176, 166);
	}

	@Override
	public void extractBackground(net.minecraft.client.gui.GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
		graphics.fill(0, 0, this.width, this.height, -1072689136);
		graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, this.leftPos, this.topPos, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
	}

	@Override
	protected void extractLabels(net.minecraft.client.gui.GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
		graphics.text(this.font, this.title, this.titleLabelX, this.titleLabelY, -12566464, false);
		graphics.text(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, -12566464, false);
	}
}
