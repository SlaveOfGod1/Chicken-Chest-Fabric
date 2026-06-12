package com.chickenchest;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

public class ChickenChestBlockEntity extends BlockEntity implements MenuProvider {
	private NonNullList<ItemStack> items = NonNullList.withSize(27, ItemStack.EMPTY);

	public ChickenChestBlockEntity(BlockPos pos, BlockState state) {
		super(ChickenChestMod.CHICKEN_CHEST_BLOCK_ENTITY, pos, state);
	}

	@Override
	public Component getDisplayName() {
		return Component.translatable("block.chicken_chest.chicken_chest");
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int syncId, net.minecraft.world.entity.player.Inventory playerInventory, Player player) {
		ChickenChestDummyContainer container = new ChickenChestDummyContainer(27);
		for (int i = 0; i < 27; i++) {
			container.setItem(i, playerInventory.getItem(i + 9).copy());
		}
		return new ChickenChestMenu(syncId, playerInventory, container);
	}

	public int getContainerSize() {
		return 27;
	}

	public ItemStack getItem(int index) {
		return this.items.get(index);
	}

	public void setItem(int index, ItemStack stack) {
		this.items.set(index, stack);
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		this.items = NonNullList.withSize(27, ItemStack.EMPTY);
		net.minecraft.world.ContainerHelper.loadAllItems(input, this.items);
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		net.minecraft.world.ContainerHelper.saveAllItems(output, this.items);
	}
}
