package com.sharinganaddon;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraftforge.network.PacketDistributor;

public class EyeSurgeryScreen extends Screen {
    public EyeSurgeryScreen() { super(Component.literal("Transplante ocular")); }
    @Override protected void init() {
        int c = width / 2;
        int top = height / 2 - 48;
        addOperation(c - 154, top + 38, 0, 0, "Retirar esquerdo");
        addOperation(c - 154, top + 62, 0, 1, "Implantar Sharingan");
        addOperation(c - 154, top + 86, 0, 2, "Implantar Byakugan");
        addOperation(c + 6, top + 38, 1, 0, "Retirar direito");
        addOperation(c + 6, top + 62, 1, 1, "Implantar Sharingan");
        addOperation(c + 6, top + 86, 1, 2, "Implantar Byakugan");
        addRenderableWidget(Button.builder(Component.literal("Fechar"), b -> onClose())
            .bounds(c - 40, top + 116, 80, 20).build());
    }
    private void addOperation(int x, int y, int side, int operation, String label) {
        addRenderableWidget(Button.builder(Component.literal(label), b ->
            SharinganAddon.CHANNEL.send(PacketDistributor.SERVER.noArg(), new EyeOperationPacket(side, operation)))
            .bounds(x, y, 148, 20).build());
    }
    @Override public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        renderBackground(g);
        int c = width / 2, top = height / 2 - 48;
        g.drawCenteredString(font, title, c, top - 25, 0xFFFFFF);
        g.drawCenteredString(font, "Escolha separadamente o tipo de cada olho", c, top - 10, 0xCFCFCF);
        g.drawCenteredString(font, "OLHO ESQUERDO", c - 80, top + 20, 0xFFFFFF);
        g.drawCenteredString(font, "OLHO DIREITO", c + 80, top + 20, 0xFFFFFF);
        super.render(g, mouseX, mouseY, partialTick);
    }
    @Override public boolean isPauseScreen() { return false; }
}
