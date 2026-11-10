package com.sharinganaddon;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.mcreator.narutoto.network.NarutotoModVariables;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Visão visual do Byakugan. Não altera transplantes nem o sistema de Sharingan. */
@Mod.EventBusSubscriber(modid = SharinganAddon.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ByakuganVision {
    private static final ResourceLocation SHADER = new ResourceLocation(SharinganAddon.MODID, "shaders/post/byakugan.json");
    private static final double RANGE = 64.0D;
    private static boolean shaderActive;
    private static boolean shaderFailed;

    private ByakuganVision() {}

    private static boolean isByakuganActive(Minecraft mc) {
        if (mc.player == null) return false;
        return mc.player.getCapability(NarutotoModVariables.PLAYER_VARIABLES_CAPABILITY, null)
                .map(vars -> vars.if_byakugan == 1.0D)
                .orElse(false);
    }

    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getInstance();
        boolean enabled = mc.player != null && mc.level != null && isByakuganActive(mc);
        if (enabled && !shaderActive && !shaderFailed) {
            try {
                mc.gameRenderer.loadEffect(SHADER);
                shaderActive = true;
            } catch (RuntimeException ex) {
                shaderActive = false;
                shaderFailed = true;
                // O efeito de pontos continua funcionando mesmo se um shader externo falhar.
                System.err.println("[SharinganAddon] Não foi possível carregar o shader do Byakugan: " + ex.getMessage());
            }
        } else if (!enabled && shaderActive) {
            mc.gameRenderer.shutdownEffect();
            shaderActive = false;
            shaderFailed = false;
        }
    }

    @SubscribeEvent
    public static void renderChakraPoints(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null || !isByakuganActive(mc)) return;

        Vec3 camera = event.getCamera().getPosition();
        PoseStack pose = event.getPoseStack();
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        for (Entity entity : mc.level.entitiesForRendering()) {
            if (!(entity instanceof LivingEntity living) || entity == mc.player || !living.isAlive()) continue;
            if (living.distanceToSqr(mc.player) > RANGE * RANGE) continue;

            pose.pushPose();
            pose.translate(entity.getX() - camera.x, entity.getY() - camera.y, entity.getZ() - camera.z);
            pose.mulPose(mc.getEntityRenderDispatcher().cameraOrientation());
            float h = entity.getBbHeight();
            // Silhueta simplificada dos tenketsu: cabeça, ombros, peito, abdômen e pernas.
            chakraPoint(buffer, pose, 0.00F, h * 0.91F, 0.00F, 0.075F);
            chakraPoint(buffer, pose, 0.00F, h * 0.76F, 0.00F, 0.085F);
            chakraPoint(buffer, pose, -0.18F, h * 0.72F, 0.00F, 0.060F);
            chakraPoint(buffer, pose,  0.18F, h * 0.72F, 0.00F, 0.060F);
            chakraPoint(buffer, pose, 0.00F, h * 0.53F, 0.00F, 0.080F);
            chakraPoint(buffer, pose, 0.00F, h * 0.34F, 0.00F, 0.065F);
            chakraPoint(buffer, pose, -0.10F, h * 0.12F, 0.00F, 0.055F);
            chakraPoint(buffer, pose,  0.10F, h * 0.12F, 0.00F, 0.055F);
            pose.popPose();
        }

        BufferUploader.drawWithShader(buffer.end());
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    private static void chakraPoint(BufferBuilder buffer, PoseStack pose, float x, float y, float z, float radius) {
        var matrix = pose.last().pose();
        // Halo azul-claro e núcleo branco para destacar os pontos através de blocos.
        quad(buffer, matrix, x, y, z, radius * 2.0F, 80, 205, 255, 95);
        quad(buffer, matrix, x, y, z, radius * 0.58F, 240, 255, 255, 255);
    }

    private static void quad(BufferBuilder buffer, org.joml.Matrix4f matrix, float x, float y, float z,
                             float radius, int r, int g, int b, int a) {
        buffer.vertex(matrix, x - radius, y - radius, z).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, x - radius, y + radius, z).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, x + radius, y + radius, z).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, x + radius, y - radius, z).color(r, g, b, a).endVertex();
    }
}
