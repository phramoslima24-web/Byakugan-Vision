package com.sharinganaddon;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraft.resources.ResourceLocation;

@Mod(SharinganAddon.MODID)
public class SharinganAddon {
    public static final String MODID = "sharinganaddon";
    public static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(MODID);
    private static final String PROTOCOL = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
        new ResourceLocation(MODID, "main"), () -> PROTOCOL, PROTOCOL::equals, PROTOCOL::equals
    );

    public SharinganAddon() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModItems.ITEMS.register(bus);
        bus.register(new ModEvents());
        CHANNEL.registerMessage(0, EyeOperationPacket.class,
            EyeOperationPacket::encode, EyeOperationPacket::decode, EyeOperationPacket::handle);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(new EyeEvents());
    }
}
