package com.sync_recipes;

import net.minecraft.recipe.RecipeEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerRecipeBook;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RecipeSyncManager {
    public static MinecraftServer server;
    public static Collection<RecipeEntry<?>> allRecipes;

    public static Set<RecipeEntry<?>> globallyUnlockedRecipes = new HashSet<>();

    private static final Logger LOGGER = LoggerFactory.getLogger("SyncRecipes -> Manager");

    public static void syncGlobalSetToPlayer(ServerPlayerEntity player) {
        LOGGER.info("{} recipes synced from global set to {}!", globallyUnlockedRecipes.size(), player.getGameProfile().getName());
        player.getRecipeBook().unlockRecipes(globallyUnlockedRecipes, player);
    }

    public static void registerAllRecipesToGlobalSet(Collection<RecipeEntry<?>> recipeEntries) {
        LOGGER.info("{} recipes synced to the global set!", recipeEntries.size());
        globallyUnlockedRecipes.addAll(recipeEntries);
    }

    public static void registerPlayerRecipesToGlobalSet(ServerPlayerEntity player) {
        ServerRecipeBook recipeBook = player.getRecipeBook();
        Set<RecipeEntry<?>> knownRecipes = allRecipes.stream().filter(recipe -> recipeBook.isUnlocked(recipe.id()))
                .collect(Collectors.toSet());

        registerAllRecipesToGlobalSet(knownRecipes);
        LOGGER.info("{} recipes synced from {} to the global set!", knownRecipes.size(), player.getGameProfile().getName());
    }
}
