package com.sharinganaddon;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.mcreator.narutoto.network.NarutotoModVariables;
import net.minecraft.resources.ResourceLocation;


@Mod.EventBusSubscriber(modid = SharinganAddon.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ByakuganVision {
    private static final ResourceLocation SHADER = new ResourceLocation(SharinganAddon.MODID, "shaders/post/byakugan.json");
    private static boolean active;

    private ByakuganVision() {}

    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) {
            unload(mc);
            return;
        }

        boolean enabled = mc.player.getCapability(NarutotoModVariables.PLAYER_VARIABLES_CAPABILITY, null)
            .map(data -> data.if_byakugan == 1.0D).orElse(false);

        if (enabled && !active) {
            try {
                mc.gameRenderer.loadEffect(SHADER);
                active = true;
            } catch (RuntimeException exception) {
                active = false;
                SharinganAddon.LOGGER.error("Não foi possível carregar o filtro visual do Byakugan.", exception);
            }
        } else if (!enabled && active) {
            unload(mc);
        }
    }

    private static void unload(Minecraft mc) {
        if (active) {
            mc.gameRenderer.shutdownEffect();
            active = false;
        }
    }

    @SubscribeEvent
    public static void renderChakraPoints(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;
        boolean enabled = mc.player.getCapability(NarutotoModVariables.PLAYER_VARIABLES_CAPABILITY, null)
            .map(data -> data.if_byakugan == 1.0D).orElse(false);
        if (!enabled) return;

        Vec3 camera = event.getCamera().getPosition();
        PoseStack pose = event.getPoseStack();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        for (Entity entity : mc.level.players()) {
            if (!(entity instanceof Player) || entity == mc.player || !entity.isAlive()) continue;
            if (entity.distanceToSqr(mc.player) > 96.0D * 96.0D) continue;

            pose.pushPose();
            pose.translate(entity.getX() - camera.x, entity.getY() - camera.y, entity.getZ() - camera.z);
            pose.mulPose(mc.getEntityRenderDispatcher().cameraOrientation());
            float height = entity.getBbHeight();
            // Pontos de chakra estilizados ao longo dos principais centros de energia.
            point(buffer, pose, 0.0F, height * 0.88F, 0.0F, 0.105F);
            point(buffer, pose, 0.0F, height * 0.70F, 0.0F, 0.085F);
            point(buffer, pose, -0.17F, height * 0.68F, 0.0F, 0.065F);
            point(buffer, pose, 0.17F, height * 0.68F, 0.0F, 0.065F);
            point(buffer, pose, 0.0F, height * 0.48F, 0.0F, 0.075F);
            point(buffer, pose, 0.0F, height * 0.27F, 0.0F, 0.065F);
            point(buffer, pose, 0.0F, height * 0.08F, 0.0F, 0.065F);
            pose.popPose();
        }

        BufferUploader.drawWithShader(buffer.end());
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    private static void point(BufferBuilder buffer, PoseStack pose, float x, float y, float z, float radius) {
        var matrix = pose.last().pose();
        // Halo externo e núcleo claro para dar aparência de ponto brilhante.
        quad(buffer, matrix, x, y, z, radius * 1.9F, 105, 225, 255, 105);
        quad(buffer, matrix, x, y, z, radius * 0.62F, 235, 255, 255, 255);
    }

    private static void quad(BufferBuilder buffer, org.joml.Matrix4f matrix,
                             float x, float y, float z, float radius,
                             int r, int g, int b, int a) {
        buffer.vertex(matrix, x - radius, y - radius, z).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, x - radius, y + radius, z).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, x + radius, y + radius, z).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, x + radius, y - radius, z).color(r, g, b, a).endVertex();
    }
}
