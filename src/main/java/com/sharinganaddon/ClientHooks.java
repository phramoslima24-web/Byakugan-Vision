package com.sharinganaddon;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ClientHooks {
    @OnlyIn(Dist.CLIENT)
    public static void openEyeScreen() {
        Minecraft.getInstance().setScreen(new EyeSurgeryScreen());
    }
}
