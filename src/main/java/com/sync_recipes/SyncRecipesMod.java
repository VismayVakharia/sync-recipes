package com.sync_recipes;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.network.ServerPlayerEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SyncRecipesMod implements ModInitializer {
	public static final String MOD_ID = "SyncRecipes";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			LOGGER.info("Server started: " + server.getName());
			RecipeSyncManager.server = server;
			RecipeSyncManager.allRecipes = server.getRecipeManager().values();
		});

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			ServerPlayerEntity player = handler.getPlayer();

			LOGGER.info(String.format("Player %s joined, syncing recipes...", player.getGameProfile().getName()));

			RecipeSyncManager.registerPlayerRecipesToGlobalSet(player);
			RecipeSyncManager.syncGlobalSetToPlayer(player);
		});

		LOGGER.info("Mod loaded!");
	}
}
