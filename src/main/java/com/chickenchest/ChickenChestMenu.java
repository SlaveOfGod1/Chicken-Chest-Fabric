package com.chickenchest;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ChickenChestMenu extends AbstractContainerMenu {
	public static final int CONTAINER_ROWS = 3;
	public static final int CONTAINER_SLOTS = CONTAINER_ROWS * 9;

	private final ChickenChestDummyContainer container;

	public ChickenChestMenu(int syncId, Inventory playerInventory) {
		this(syncId, playerInventory, new ChickenChestDummyContainer(CONTAINER_SLOTS));
	}

	public ChickenChestMenu(int syncId, Inventory playerInventory, ChickenChestDummyContainer container) {
		super(ChickenChestMod.CHICKEN_CHEST_MENU, syncId);
		this.container = container;
		container.startOpen(playerInventory.player);

		int chestY = 18;
		for (int row = 0; row < CONTAINER_ROWS; row++) {
			for (int col = 0; col < 9; col++) {
				this.addSlot(new Slot(container, col + row * 9, 8 + col * 18, chestY + row * 18));
			}
		}

		int playerY = 84;
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 9; col++) {
				this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, playerY + row * 18));
			}
		}

		int hotbarY = 142;
		for (int col = 0; col < 9; col++) {
			this.addSlot(new Slot(playerInventory, col, 8 + col * 18, hotbarY));
		}
	}

	@Override
	public boolean stillValid(Player player) {
		return true;
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		ItemStack result = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		if (slot != null && slot.hasItem()) {
			ItemStack slotStack = slot.getItem();
			result = slotStack.copy();

			int containerEnd = CONTAINER_SLOTS;
			int playerEnd = containerEnd + 27;
			int hotbarEnd = playerEnd + 9;

			if (index < containerEnd) {
				if (!this.moveItemStackTo(slotStack, containerEnd, hotbarEnd, true)) {
					return ItemStack.EMPTY;
				}
			} else if (index < playerEnd) {
				if (!this.moveItemStackTo(slotStack, 0, containerEnd, false)) {
					if (!this.moveItemStackTo(slotStack, playerEnd, hotbarEnd, false)) {
						return ItemStack.EMPTY;
					}
				}
			} else if (index < hotbarEnd) {
				if (!this.moveItemStackTo(slotStack, 0, containerEnd, false)) {
					if (!this.moveItemStackTo(slotStack, containerEnd, playerEnd, false)) {
						return ItemStack.EMPTY;
					}
				}
			}

			if (slotStack.isEmpty()) {
				slot.setByPlayer(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}

			if (slotStack.getCount() == result.getCount()) {
				return ItemStack.EMPTY;
			}

			slot.onTake(player, slotStack);
		}
		return result;
	}
}
