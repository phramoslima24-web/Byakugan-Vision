package com.sharinganaddon;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.Mth;
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

        float pulse = 0.82F + 0.18F * Mth.sin((mc.level.getGameTime() + event.getPartialTick()) * 0.18F);
        for (Entity entity : mc.level.entitiesForRendering()) {
            if (!(entity instanceof LivingEntity living) || entity == mc.player || !entity.isAlive()) continue;
            if (entity.distanceToSqr(mc.player) > 72.0D * 72.0D) continue;

            pose.pushPose();
            pose.translate(entity.getX() - camera.x, entity.getY() - camera.y, entity.getZ() - camera.z);
            pose.mulPose(mc.getEntityRenderDispatcher().cameraOrientation());
            float height = entity.getBbHeight();
            // Rede de tenketsu: cabeça, garganta, peito, ombros, abdômen,
            // cotovelos, mãos, quadril, joelhos e pés.
            chakraNode(buffer, pose, 0.0F, height * 0.91F, 0.0F, 0.095F, pulse);
            chakraNode(buffer, pose, 0.0F, height * 0.79F, 0.0F, 0.060F, pulse);
            chakraNode(buffer, pose, 0.0F, height * 0.68F, 0.0F, 0.105F, pulse);
            chakraNode(buffer, pose, -0.22F, height * 0.69F, 0.0F, 0.070F, pulse);
            chakraNode(buffer, pose, 0.22F, height * 0.69F, 0.0F, 0.070F, pulse);
            chakraNode(buffer, pose, 0.0F, height * 0.53F, 0.0F, 0.085F, pulse);
            chakraNode(buffer, pose, -0.28F, height * 0.49F, 0.0F, 0.060F, pulse);
            chakraNode(buffer, pose, 0.28F, height * 0.49F, 0.0F, 0.060F, pulse);
            chakraNode(buffer, pose, -0.34F, height * 0.31F, 0.0F, 0.055F, pulse);
            chakraNode(buffer, pose, 0.34F, height * 0.31F, 0.0F, 0.055F, pulse);
            chakraNode(buffer, pose, 0.0F, height * 0.30F, 0.0F, 0.075F, pulse);
            chakraNode(buffer, pose, -0.13F, height * 0.08F, 0.0F, 0.065F, pulse);
            chakraNode(buffer, pose, 0.13F, height * 0.08F, 0.0F, 0.065F, pulse);
            pose.popPose();
        }

        BufferUploader.drawWithShader(buffer.end());
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    private static void chakraNode(BufferBuilder buffer, PoseStack pose, float x, float y, float z,
                                   float radius, float pulse) {
        var matrix = pose.last().pose();
        // Três camadas: aura azul ampla, núcleo branco e pequeno brilho central.
        quad(buffer, matrix, x, y, z, radius * 2.8F * pulse, 45, 190, 255, 34);
        quad(buffer, matrix, x, y, z, radius * 1.55F * pulse, 95, 225, 255, 105);
        quad(buffer, matrix, x, y, z, radius * 0.48F, 245, 255, 255, 245);
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
