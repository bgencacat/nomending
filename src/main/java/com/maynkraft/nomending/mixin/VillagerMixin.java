package com.maynkraft.nomending.mixin;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.SequencedSet;

@Mixin(Villager.class)
public abstract class VillagerMixin extends AbstractVillager {

	public VillagerMixin(EntityType<? extends AbstractVillager> type, Level level) {
		super(type, level);
	}

	@Inject(at = @At("RETURN"), method = "updateTrades")
	private void modifyAllMendingTrades(CallbackInfo ci) {
		MerchantOffers merchantOffers = this.getOffers();

		if(offers == null) return;

		System.out.println("asdasdsadasd");
		level().getServer().getPlayerList().broadcastSystemMessage(Component.literal("Teklifler tetiklendi."), true);

		Registry<Enchantment> registry = this.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
		Holder<Enchantment> mending = registry.getOrThrow(Enchantments.MENDING);
		Holder<Enchantment> vanishingCurse = registry.getOrThrow(Enchantments.VANISHING_CURSE);

		for (int i = 0; i < merchantOffers.size(); i++) {
			MerchantOffer offer = merchantOffers.get(i);
			ItemStack item = offer.getResult().copy();

			level().getServer().getPlayerList().broadcastSystemMessage(Component.translatable(item.getDisplayName().toString()), false);

			if(!item.is(Items.ENCHANTED_BOOK)) continue;

			ItemEnchantments enchantments = item.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);
			level().getServer().sendSystemMessage(Component.literal("Enchant kitabı var."));

			if(!enchantments.keySet().contains(mending)) continue;
			level().getServer().sendSystemMessage(Component.literal("Mending içeriyor."));


			ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
			mutable.set(vanishingCurse, 1);

			Component name = Component.translatable("item.minecraft.enchanted_book")
					.withStyle(ChatFormatting.AQUA)
					.withStyle(style -> style.withItalic(false));

			ItemLore lore = new ItemLore(List.of(
					Component.translatable("enchantment.minecraft.mending")
							.withStyle(ChatFormatting.GRAY)
							.withStyle(style -> style.withItalic(false))
			));

			SequencedSet<DataComponentType<?>> hidden = new LinkedHashSet<>();
			hidden.add(DataComponents.STORED_ENCHANTMENTS);

			CustomData data = item.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
			CompoundTag tag = data.copyTag();
			tag.putBoolean("is_troll_book", true);

			item.set(DataComponents.STORED_ENCHANTMENTS, mutable.toImmutable());
			item.set(DataComponents.CUSTOM_NAME, name);
			item.set(DataComponents.LORE, lore);
			item.set(DataComponents.TOOLTIP_DISPLAY, new TooltipDisplay(false, hidden));
			item.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));

			MerchantOffer newMerchantOffer = new MerchantOffer(
					offer.getItemCostA(),
					offer.getItemCostB(),
					item,
					offer.getUses(),
					offer.getMaxUses(),
					offer.getXp(),
					offer.getPriceMultiplier(),
					offer.getDemand()
			);

            offers.set(i, newMerchantOffer);
		}
	}
}