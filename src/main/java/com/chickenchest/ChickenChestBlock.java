package com.chickenchest;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class ChickenChestBlock extends BaseEntityBlock {
	public static final MapCodec<ChickenChestBlock> CODEC = simpleCodec(ChickenChestBlock::new);
	public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty OPEN = BlockStateProperties.OPEN;

	private static final VoxelShape SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 14.0, 15.0);

	public ChickenChestBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(OPEN, false));
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {
		return CODEC;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, OPEN);
	}

	@Override
	public VoxelShape getShape(BlockState state, net.minecraft.world.level.BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	public BlockState getStateForPlacement(net.minecraft.world.item.context.BlockPlaceContext context) {
		return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rotation) {
		return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new ChickenChestBlockEntity(pos, state);
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		super.setPlacedBy(level, pos, state, placer, stack);
		if (!level.isClientSide()) {
			level.playSound(null, pos, ChickenChestMod.PLACE_SOUND, SoundSource.BLOCKS, 1.0f, 1.0f);
		}
	}

	@Override
	protected MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
		BlockEntity blockEntity = level.getBlockEntity(pos);
		if (blockEntity instanceof ChickenChestBlockEntity chest) {
			return new SimpleMenuProvider(
					(syncId, playerInventory, player) -> {
						level.setBlock(pos, state.setValue(OPEN, true), 3);
						ChickenChestDummyContainer container = new ChickenChestDummyContainer(27);
						for (int i = 0; i < 27; i++) {
							container.setItem(i, playerInventory.getItem(i + 9).copy());
						}
						return new ChickenChestMenu(syncId, playerInventory, container) {
							@Override
							public void removed(Player p) {
								super.removed(p);
								Level lv = chest.getLevel();
								if (lv != null) {
									BlockPos blockPos = chest.getBlockPos();
									BlockState blockState = lv.getBlockState(blockPos);
									lv.setBlock(blockPos, blockState.setValue(OPEN, false), 3);
									lv.playSound(null, blockPos, ChickenChestMod.CLOSE_SOUND, SoundSource.BLOCKS, 1.0f, 1.0f);
								}
							}
						};
					},
					Component.translatable("block.chicken_chest.chicken_chest")
			);
		}
		return null;
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		if (level.isClientSide()) {
			return InteractionResult.SUCCESS;
		}
		MenuProvider menuProvider = state.getMenuProvider(level, pos);
		if (menuProvider != null) {
			player.openMenu(menuProvider);
			level.playSound(null, pos, ChickenChestMod.OPEN_SOUND, SoundSource.BLOCKS, 1.0f, 1.0f);
		}
		return InteractionResult.CONSUME;
	}
}
