package com.sharinganaddon;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;

public class EyeOperationPacket {
    private final int side;
    private final int operation; // 0 remove, 1 implant Sharingan, 2 implant Byakugan
    public EyeOperationPacket(int side, int operation) { this.side = side; this.operation = operation; }
    public static void encode(EyeOperationPacket msg, FriendlyByteBuf buf) { buf.writeVarInt(msg.side); buf.writeVarInt(msg.operation); }
    public static EyeOperationPacket decode(FriendlyByteBuf buf) { return new EyeOperationPacket(buf.readVarInt(), buf.readVarInt()); }
    public static void handle(EyeOperationPacket msg, Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player != null && msg.side >= 0 && msg.side <= 1 && msg.operation >= 0 && msg.operation <= 2)
                EyeLogic.operate(player, msg.side, msg.operation);
        });
        ctx.setPacketHandled(true);
    }
}
