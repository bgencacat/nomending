package com.maynkraft.nomending.mixin;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.maynkraft.nomending.util.Helper.checkHasMending;

@Mixin(LootTable.class)
public class LootTableMixin {
    @Inject(
            method = "getRandomItems(Lnet/minecraft/world/level/storage/loot/LootContext;)Lit/unimi/dsi/fastutil/objects/ObjectArrayList;",
            at = @At("RETURN")
    )
    private void nukeMendingFromOrbit(LootContext context, CallbackInfoReturnable<ObjectArrayList<ItemStack>> cir) {
        ObjectArrayList<ItemStack> generatedLoot = cir.getReturnValue();

        generatedLoot.removeIf(stack -> {
            if (stack == null || stack.isEmpty()) return false;
            return checkHasMending(stack);
        });
    }
}