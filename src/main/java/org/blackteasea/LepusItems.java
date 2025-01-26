package org.blackteasea;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class LepusItems {
    public static Item register(Item item, RegistryKey<Item> key) {
       Item registeredItem = Registry.register(Registries.ITEM, key.getValue(), item);
         return registeredItem;
    }

    public static final RegistryKey<Item> SUMMON_ITEM_KEY = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Lepus.MOD_ID, "summon_item"));
    public static final Item SUMMON_ITEM = register(new Item(new Item.Settings().registryKey(SUMMON_ITEM_KEY)), SUMMON_ITEM_KEY);

    public static void initialize() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register((itemGroup) -> itemGroup.add(LepusItems.SUMMON_ITEM));
    }

}
