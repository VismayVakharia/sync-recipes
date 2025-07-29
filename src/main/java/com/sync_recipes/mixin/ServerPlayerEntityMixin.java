package com.sync_recipes.mixin;

import java.util.Collection;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.sync_recipes.RecipeSyncManager;

import net.minecraft.recipe.RecipeEntry;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {

	@Unique
	private static final Logger LOGGER = LoggerFactory.getLogger("SyncRecipes -> Mixin");

	@Inject(method = "unlockRecipes(Ljava/util/Collection;)I", at = @At("TAIL"))
	private void onUnlockRecipe(Collection<RecipeEntry<?>> recipes, CallbackInfoReturnable<Integer> cir) {

		ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

		LOGGER.info("Syncing just unlocked {} recipes by {} to global set!", recipes.size(), player.getGameProfile().getName());
		RecipeSyncManager.registerAllRecipesToGlobalSet(recipes);

		for (ServerPlayerEntity p : RecipeSyncManager.server.getPlayerManager().getPlayerList()) {
			if (!p.equals(player)) {
				LOGGER.info("Syncing just unlocked {} recipes to online player: {}!", recipes.size(), p.getGameProfile().getName());
				for (RecipeEntry<?> recipe : recipes) {
					if (!p.getRecipeBook().isUnlocked(recipe.id())) {
						p.getRecipeBook().unlockRecipes(Collections.singleton(recipe), p);
					}
				}
			}
		}
	}
}
