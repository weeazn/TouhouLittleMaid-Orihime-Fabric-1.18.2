package cn.sh1rocu.touhoulittlemaid.mixin.common;

import com.github.tartaricacid.touhoulittlemaid.init.InitTrigger;
import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.RecipeHolder;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ResultSlot.class)
public abstract class MaidCraftingResultMixin {
    @Shadow @Final private Player player;

    @Inject(method = "checkTakeAchievements", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/inventory/RecipeHolder;awardUsedRecipes(Lnet/minecraft/world/entity/player/Player;)V"))
    private void tlm$craftAdvancement(ItemStack result, CallbackInfo callback) {
        if (player instanceof ServerPlayer server && ((Slot)(Object)this).container instanceof RecipeHolder holder) {
            var recipe = holder.getRecipeUsed();
            if (recipe != null && recipe.getId().getNamespace().equals(TouhouLittleMaid.MOD_ID)) {
                InitTrigger.MAID_EVENT.trigger(server, "craft_" + recipe.getId().getPath());
            }
        }
    }
}
