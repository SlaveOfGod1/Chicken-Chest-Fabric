package com.chickenchest;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.SimpleContainer;

public class ChickenChestDummyContainer extends SimpleContainer {
	public ChickenChestDummyContainer(int size) {
		super(size);
	}

	@Override
	public boolean stillValid(Player player) {
		return true;
	}
}
