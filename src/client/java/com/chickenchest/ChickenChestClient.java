package com.chickenchest;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screens.MenuScreens;

public class ChickenChestClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        MenuScreens.register(ChickenChestMod.CHICKEN_CHEST_MENU, ChickenChestScreen::new);
    }
}
