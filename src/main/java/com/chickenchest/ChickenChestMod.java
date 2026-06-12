package com.chickenchest;

import java.util.Set;
import java.util.function.Function;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.flag.FeatureFlagSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChickenChestMod implements ModInitializer {
    public static final String MOD_ID = "chicken_chest";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static Item CHICKEN_BONES;
    public static Block CHICKEN_CHEST;
    public static BlockEntityType<ChickenChestBlockEntity> CHICKEN_CHEST_BLOCK_ENTITY;
    public static MenuType<ChickenChestMenu> CHICKEN_CHEST_MENU;

    public static SoundEvent OPEN_SOUND = SoundEvent.createVariableRangeEvent(
            Identifier.fromNamespaceAndPath(MOD_ID, "open"));
    public static SoundEvent CLOSE_SOUND = SoundEvent.createVariableRangeEvent(
            Identifier.fromNamespaceAndPath(MOD_ID, "close"));
    public static SoundEvent PLACE_SOUND = SoundEvent.createVariableRangeEvent(
            Identifier.fromNamespaceAndPath(MOD_ID, "chickenchestplace"));

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Chicken Chest Mod!");

        Registry.register(BuiltInRegistries.SOUND_EVENT, Identifier.fromNamespaceAndPath(MOD_ID, "open"), OPEN_SOUND);
        Registry.register(BuiltInRegistries.SOUND_EVENT, Identifier.fromNamespaceAndPath(MOD_ID, "close"), CLOSE_SOUND);
        Registry.register(BuiltInRegistries.SOUND_EVENT, Identifier.fromNamespaceAndPath(MOD_ID, "chickenchestplace"), PLACE_SOUND);

        CHICKEN_BONES = registerItem("chicken_bones", Item::new, new Item.Properties());

        CHICKEN_CHEST = registerBlock("chicken_chest", ChickenChestBlock::new, BlockBehaviour.Properties.of()
                .mapColor(MapColor.WOOD)
                .strength(2.0f, 3.0f)
                .sound(SoundType.WOOD)
                .pushReaction(net.minecraft.world.level.material.PushReaction.BLOCK));

        registerItem("chicken_chest", settings -> new BlockItem(CHICKEN_CHEST, settings), new Item.Properties());

        CHICKEN_CHEST_BLOCK_ENTITY = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE,
                Identifier.fromNamespaceAndPath(MOD_ID, "chicken_chest"),
                new BlockEntityType<>(ChickenChestBlockEntity::new, Set.of(CHICKEN_CHEST)));

        CHICKEN_CHEST_MENU = Registry.register(BuiltInRegistries.MENU,
                Identifier.fromNamespaceAndPath(MOD_ID, "chicken_chest"),
                new MenuType<>(ChickenChestMenu::new, FeatureFlagSet.of()));

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS)
                .register((output) -> {
                    output.accept(new ItemStack(CHICKEN_CHEST));
                });

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS)
                .register((output) -> {
                    output.accept(new ItemStack(CHICKEN_BONES));
                });

        ResourceKey<LootTable> chickenLootTable = ResourceKey.create(
                Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath("minecraft", "entities/chicken"));

        LootTableEvents.MODIFY.register((resourceKey, lootTableBuilder, source, registries) -> {
            if (source.isBuiltin() && chickenLootTable.equals(resourceKey)) {
                lootTableBuilder.pool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(CHICKEN_BONES)
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))))
                        .when(LootItemRandomChanceCondition.randomChance(0.05f))
                        .build());
            }
        });

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            LOGGER.info("Chicken Chest Mod loaded successfully!");
        });
    }

    public static <T extends Item> T registerItem(String name, Function<Item.Properties, T> factory, Item.Properties settings) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(MOD_ID, name));
        T item = factory.apply(settings.setId(key));
        Registry.register(BuiltInRegistries.ITEM, key, item);
        return item;
    }

    public static Block registerBlock(String name, Function<BlockBehaviour.Properties, Block> factory, BlockBehaviour.Properties properties) {
        ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(MOD_ID, name));
        Block block = factory.apply(properties.setId(key));
        return Registry.register(BuiltInRegistries.BLOCK, key, block);
    }
}
