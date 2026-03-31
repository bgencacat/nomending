package com.maynkraft.nomending.util;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public class Helper {
    // --- Yardımcı Metotlar ---
    public static boolean checkHasMending(ItemStack stack) {
        // Normal eşyalar için (Zırh, Kılıç vb.)
        ItemEnchantments normalEnchants = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        // Büyülü kitaplar için
        ItemEnchantments storedEnchants = stack.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);

        return hasMendingId(normalEnchants) || hasMendingId(storedEnchants);
    }

    public static boolean hasMendingId(ItemEnchantments enchantments) {
        for (Holder<Enchantment> holder : enchantments.keySet()) {
            if (holder.unwrapKey().isPresent()) {
                if (holder.unwrapKey().get().identifier().getPath().equals("mending")) {
                    return true;
                }
            }
        }
        return false;
    }
}
