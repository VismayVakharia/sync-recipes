package com.sync_recipes;

import net.minecraft.recipe.RecipeEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerRecipeBook;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static com.sync_recipes.SyncRecipesMod.LOGGER;

public class RecipeSyncManager {
    public static MinecraftServer server;
    public static Collection<RecipeEntry<?>> allRecipes;

    public static Set<RecipeEntry<?>> globallyUnlockedRecipes = new HashSet<>();

    public static final void registerRecipeToGlobalSet(RecipeEntry<?> recipeEntry) {
        // not being used!
        globallyUnlockedRecipes.add(recipeEntry);
    }

    public static final void syncGlobalSetToPlayer(ServerPlayerEntity player) {
        player.unlockRecipes(globallyUnlockedRecipes);
    }

    public static final boolean registerAllRecipesToGlobalSet(Collection<RecipeEntry<?>> recipeEntries) {
        return globallyUnlockedRecipes.addAll(recipeEntries);
    }

    public static final void registerPlayerRecipesToGlobalSet(ServerPlayerEntity player) {
        // LOGGER.info(String.format("Syncing player %s recipes to the global set!",
        // player.getGameProfile().getName()));
        ServerRecipeBook recipeBook = player.getRecipeBook();
        Set<RecipeEntry<?>> knownRecipes = allRecipes.stream().filter(recipe -> recipeBook.isUnlocked(recipe.id()))
                .collect(Collectors.toSet());

        registerAllRecipesToGlobalSet(knownRecipes);
        LOGGER.info(String.format("%d recipes synced from player %s to the global set!", knownRecipes.size(),
                player.getGameProfile().getName()));
    }
}
