package com.sharinganaddon;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraft.server.level.ServerPlayer;
import net.mcreator.narutoto.network.NarutotoModVariables;

public class EyeEvents {

    /** Keeps Narutoto's original B-key toggle from re-enabling a dojutsu
     * after both transplanted eyes have been removed. */
    @SubscribeEvent
    public void enforceRemovedEyes(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide) return;
        if (!(event.player instanceof ServerPlayer player)) return;
        if (!player.getPersistentData().contains("sharinganaddon_left_eye")
                && !player.getPersistentData().contains("sharinganaddon_right_eye")) return;

        String left = player.getPersistentData().getString("sharinganaddon_left_eye");
        String right = player.getPersistentData().getString("sharinganaddon_right_eye");
        boolean sharingan = "sharingan".equals(left) || "sharingan".equals(right);
        boolean byakugan = "byakugan".equals(left) || "byakugan".equals(right);
        var cap = player.getCapability(NarutotoModVariables.PLAYER_VARIABLES_CAPABILITY, null);
        if (!cap.isPresent()) return;
        var vars = cap.orElseThrow(IllegalStateException::new);
        boolean changed = vars.if_show_uchiha != sharingan || vars.if_show_hyuga != byakugan
                || vars.sharingan_transplant != sharingan || vars.byakugan_transplant != byakugan
                || vars.if_byakugan != (byakugan ? 1.0D : 0.0D)
                || (!sharingan && vars.if_sharigan != 0.0D);
        if (!changed) return;
        vars.if_show_uchiha = sharingan;
        if (!sharingan) vars.if_sharigan = 0.0D;
        vars.if_show_hyuga = byakugan;
        vars.sharingan_transplant = sharingan;
        vars.byakugan_transplant = byakugan;
        vars.if_byakugan = byakugan ? 1.0D : 0.0D;
        vars.syncPlayerVariables(player);
    }

}
