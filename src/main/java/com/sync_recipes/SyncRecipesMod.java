package com.sync_recipes;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SyncRecipesMod implements ModInitializer {
	private static final Logger LOGGER = LoggerFactory.getLogger("SyncRecipes -> Init");

	@Override
	public void onInitialize() {

		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            LOGGER.info("Server started: {}", server.getName());
			RecipeSyncManager.server = server;
			RecipeSyncManager.allRecipes = server.getRecipeManager().values();
		});

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			ServerPlayerEntity player = handler.getPlayer();

			LOGGER.info("{} joined, syncing recipes...", player.getGameProfile().getName());

			RecipeSyncManager.registerPlayerRecipesToGlobalSet(player);
			RecipeSyncManager.syncGlobalSetToPlayer(player);

			List<ServerPlayerEntity> player_list = server.getPlayerManager().getPlayerList();

			LOGGER.info("Syncing global set with {} many other players on {}'s arrival!", player_list.size(), player.getGameProfile().getName());
			for (ServerPlayerEntity p : player_list) {
				LOGGER.info("Syncing global set to {}", p.getGameProfile().getName());
				RecipeSyncManager.syncGlobalSetToPlayer(p);
			}
		});

		LOGGER.info("Mod loaded!");
	}
}
