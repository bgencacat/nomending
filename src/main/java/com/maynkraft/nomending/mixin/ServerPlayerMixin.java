package com.maynkraft.nomending.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.maynkraft.nomending.util.Helper.checkHasMending;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {
    @Inject(method = "initMenu", at = @At("RETURN"))
    private void sweepMendingOnContainerOpen(AbstractContainerMenu menu, CallbackInfo ci) {
        for (int i = 0; i < menu.slots.size(); i++) {
            Slot slot = menu.slots.get(i);
            if (slot.hasItem()) {
                ItemStack stack = slot.getItem();
                if (checkHasMending(stack)) {
                    slot.set(ItemStack.EMPTY);
                }
            }
        }
    }


}
