package com.sync_recipes.mixin;

import java.util.Collection;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.sync_recipes.RecipeSyncManager;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.server.network.ServerRecipeBook;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(ServerRecipeBook.class)
public class ServerRecipeBookMixin {

	@Inject(method = "unlockRecipes(Ljava/util/Collection;Lnet/minecraft/server/network/ServerPlayerEntity;)I", at = @At("TAIL"))
	private void onUnlockRecipe(Collection<RecipeEntry<?>> recipes, ServerPlayerEntity player,
			CallbackInfoReturnable<Integer> cir) {

		if (RecipeSyncManager.registerAllRecipesToGlobalSet(recipes)) {
			for (ServerPlayerEntity p : player.getServer().getPlayerManager().getPlayerList()) {
				if (!p.equals(player)) {
					p.unlockRecipes(recipes);
				}
			}
		}
	}
}
