package com.sharinganaddon;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraftforge.items.ItemHandlerHelper;
import net.mcreator.narutoto.init.NarutotoModItems;
import net.mcreator.narutoto.network.NarutotoModVariables;

public final class EyeLogic {
    private static final String LEFT = "sharinganaddon_left_eye";
    private static final String RIGHT = "sharinganaddon_right_eye";
    private EyeLogic() {}

    // operation: 0 removes the selected eye; 1/2 explicitly implant Sharingan/Byakugan.
    public static void operate(ServerPlayer player, int side, int operation) {
        String key = side == 0 ? LEFT : RIGHT;
        initialize(player);
        String current = player.getPersistentData().getString(key);
        if (operation == 0) {
            if ("none".equals(current)) {
                player.displayClientMessage(Component.literal("Este olho já está vazio."), true);
                return;
            }
            Item item = "byakugan".equals(current) ? NarutotoModItems.BYAKUGAN_EYEBALL.get() : NarutotoModItems.SHARINGAN_EYEBALL.get();
            ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(item));
            player.getPersistentData().putString(key, "none");
            player.displayClientMessage(Component.literal("Olho " + sideName(side) + " retirado: " + current), true);
        } else {
            String wanted = operation == 1 ? "sharingan" : "byakugan";
            Item item = operation == 1 ? NarutotoModItems.SHARINGAN_EYEBALL.get() : NarutotoModItems.BYAKUGAN_EYEBALL.get();
            ItemStack stack = find(player, item);
            if (stack.isEmpty()) {
                player.displayClientMessage(Component.literal("Você não possui um olho " + wanted + " no inventário."), true);
                return;
            }
            if (!"none".equals(current)) {
                Item old = "byakugan".equals(current) ? NarutotoModItems.BYAKUGAN_EYEBALL.get() : NarutotoModItems.SHARINGAN_EYEBALL.get();
                ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(old));
            }
            stack.shrink(1);
            player.getPersistentData().putString(key, wanted);
            player.displayClientMessage(Component.literal(wanted + " implantado no olho " + sideName(side) + "."), true);
        }
        syncFlags(player);
        updateBlindness(player);
    }

    private static String sideName(int side) { return side == 0 ? "esquerdo" : "direito"; }
    private static void initialize(ServerPlayer player) {
        var cap = player.getCapability(NarutotoModVariables.PLAYER_VARIABLES_CAPABILITY, null);
        if (!cap.isPresent()) return;
        var vars = cap.orElseThrow(IllegalStateException::new);
        // O Narutoto legado guarda apenas o dojutsu/clã, não o lado.
        // Migra o estado antigo para o olho esquerdo uma única vez; nunca duplica nos dois lados.
        if (!player.getPersistentData().contains(LEFT) && !player.getPersistentData().contains(RIGHT)) {
            String nativeEye = vars.if_show_uchiha || vars.sharingan_transplant
                ? "sharingan"
                : (vars.if_show_hyuga || vars.byakugan_transplant ? "byakugan" : "none");
            player.getPersistentData().putString(LEFT, nativeEye);
            player.getPersistentData().putString(RIGHT, "none");
        } else {
            if (!player.getPersistentData().contains(LEFT)) player.getPersistentData().putString(LEFT, "none");
            if (!player.getPersistentData().contains(RIGHT)) player.getPersistentData().putString(RIGHT, "none");
        }
    }
    private static void syncFlags(ServerPlayer player) {
        var cap = player.getCapability(NarutotoModVariables.PLAYER_VARIABLES_CAPABILITY, null);
        if (!cap.isPresent()) return;
        var vars = cap.orElseThrow(IllegalStateException::new);
        String left = player.getPersistentData().getString(LEFT), right = player.getPersistentData().getString(RIGHT);
        boolean sharingan = "sharingan".equals(left) || "sharingan".equals(right);
        boolean byakugan = "byakugan".equals(left) || "byakugan".equals(right);
        vars.if_show_uchiha = sharingan;
        vars.sharingan_transplant = sharingan;
        // if_sharigan é o sinal realmente consultado pelo renderizador original.
        // Não forçamos a ativação quando os olhos existem: a tecla B continua controlando o toggle.
        if (!sharingan) vars.if_sharigan = 0.0D;
        vars.if_show_hyuga = byakugan;
        vars.byakugan_transplant = byakugan;
        vars.if_byakugan = byakugan ? 1.0D : 0.0D;
        vars.syncPlayerVariables(player);
    }
    private static ItemStack find(ServerPlayer player, Item item) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.is(item)) return stack;
        }
        return ItemStack.EMPTY;
    }
    private static void updateBlindness(ServerPlayer player) {
        boolean left = !"none".equals(player.getPersistentData().getString(LEFT));
        boolean right = !"none".equals(player.getPersistentData().getString(RIGHT));
        if (!left && !right) player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, Integer.MAX_VALUE, 0, false, false, true));
        else player.removeEffect(MobEffects.BLINDNESS);
    }
    public static String eye(ServerPlayer player, int side) { return player.getPersistentData().getString(side == 0 ? LEFT : RIGHT); }
}
