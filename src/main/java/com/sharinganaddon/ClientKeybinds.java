package com.sharinganaddon;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = SharinganAddon.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ClientKeybinds {
    public static final KeyMapping REMOVE_EYE = new KeyMapping(
        "key.sharinganaddon.remove_eye", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_P,
        "key.categories.sharinganaddon"
    );

    private ClientKeybinds() {}

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(REMOVE_EYE);
    }
}
