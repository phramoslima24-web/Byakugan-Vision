package com.sharinganaddon;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
        DeferredRegister.create(ForgeRegistries.ITEMS, SharinganAddon.MODID);

    public static final RegistryObject<Item> TRANSPLANT_TOOL = ITEMS.register("transplant_tool",
        () -> new TransplantToolItem(new Item.Properties().stacksTo(1)));
}
