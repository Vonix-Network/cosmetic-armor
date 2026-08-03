package com.vonix.cosmeticarmor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Draws compact cosmetic slots immediately beside the vanilla armor column. */
@Mod.EventBusSubscriber(modid = CosmeticArmorMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class InventoryCosmeticOverlay {
    private static final int SLOT = 18;
    // Vanilla armor slots are at texture x=8. Cosmetic slots sit directly beside them at x=26.
    private static final int COSMETIC_X = 26;
    private static final int COSMETIC_Y = 8;
    private static final int[] ARMOR_SLOT_TEXTURE_Y = {68, 48, 28, 8}; // feet, legs, chest, head
    private static final ResourceLocation INVENTORY_TEXTURE = new ResourceLocation("minecraft", "textures/gui/container/inventory.png");

    private InventoryCosmeticOverlay() {}

    @SubscribeEvent
    public static void addToInventory(ScreenEvent.InitScreenEvent.Post event) {
        if (!(event.getScreen() instanceof InventoryScreen screen)) return;
        int x = screen.getGuiLeft() + COSMETIC_X;
        int y = screen.getGuiTop() + COSMETIC_Y - 9;
        event.addListener(new SmallToggleButton(x, y, localData().enabled(), button -> {
            boolean enabled = !localData().enabled();
            Network.CHANNEL.sendToServer(new Network.TogglePacket(enabled));
            ((SmallToggleButton) button).enabled = enabled;
        }));
    }

    @SubscribeEvent
    public static void drawInventory(ScreenEvent.DrawScreenEvent.Post event) {
        if (!(event.getScreen() instanceof InventoryScreen screen)) return;
        PoseStack pose = event.getPoseStack();
        int x = screen.getGuiLeft() + COSMETIC_X;
        int y = screen.getGuiTop() + COSMETIC_Y;
        CosmeticData data = localData();
        ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, INVENTORY_TEXTURE);
        for (int cosmeticSlot = 0; cosmeticSlot < CosmeticData.SLOT_COUNT; cosmeticSlot++) {
            int sy = y + cosmeticSlot * SLOT;
            drawVanillaArmorSlot(pose, x, sy, ARMOR_SLOT_TEXTURE_Y[cosmeticSlot]);
            ItemStack stack = data.get(cosmeticSlot);
            if (!stack.isEmpty()) renderer.renderAndDecorateItem(stack, x + 1, sy + 1);
            drawTinyButton(pose, x + 18, sy, 5, 8, data.showCosmetic(cosmeticSlot) ? 0xFF4D8B4D : 0xFF6B3D3D);
            drawTinyButton(pose, x + 24, sy, 12, 8, 0xFF777777);
        }
    }

    @SubscribeEvent
    public static void clickInventory(ScreenEvent.MouseClickedEvent.Pre event) {
        if (!(event.getScreen() instanceof InventoryScreen screen) || event.getButton() != 0) return;
        double x = screen.getGuiLeft() + COSMETIC_X;
        double y = screen.getGuiTop() + COSMETIC_Y;
        for (int slot = 0; slot < CosmeticData.SLOT_COUNT; slot++) {
            double sy = y + slot * SLOT;
            if (inside(event.getMouseX(), event.getMouseY(), x, sy, 18, 18)) {
                // Click the cosmetic slot: copy matching armor from the player's main hand.
                Network.CHANNEL.sendToServer(new Network.HeldPacket(slot));
                event.setCanceled(true);
                return;
            }
            if (inside(event.getMouseX(), event.getMouseY(), x + 18, sy, 5, 8)) {
                Network.CHANNEL.sendToServer(new Network.SlotVisibilityPacket(slot, !localData().showCosmetic(slot)));
                event.setCanceled(true);
                return;
            }
            if (inside(event.getMouseX(), event.getMouseY(), x + 24, sy, 12, 8)) {
                Network.CHANNEL.sendToServer(new Network.ClearPacket(slot));
                event.setCanceled(true);
                return;
            }
        }
    }

    private static void drawVanillaArmorSlot(PoseStack pose, int x, int y, int textureY) {
        GuiComponent.blit(pose, x, y, 8, textureY, 18, 18, 256, 256);
    }

    private static void drawTinyButton(PoseStack pose, int x, int y, int width, int height, int color) {
        GuiComponent.fill(pose, x, y, x + width, y + height, color);
        GuiComponent.fill(pose, x + 1, y + 1, x + width - 1, y + height - 1, 0xFF303030);
    }

    private static boolean inside(double mouseX, double mouseY, double x, double y, int width, int height) { return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height; }
    private static CosmeticData localData() { return Minecraft.getInstance().player == null ? ClientState.data() : ClientState.data(Minecraft.getInstance().player.getUUID()); }

    private static final class SmallToggleButton extends net.minecraft.client.gui.components.Button {
        private boolean enabled;
        private SmallToggleButton(int x, int y, boolean enabled, OnPress press) { super(x, y, 18, 8, new TextComponent(enabled ? "ON" : "OFF"), press); this.enabled = enabled; }
        @Override public void renderButton(PoseStack pose, int mouseX, int mouseY, float partialTicks) {
            GuiComponent.fill(pose, x, y, x + width, y + height, enabled ? 0xFF4D8B4D : 0xFF6B3D3D);
            GuiComponent.drawCenteredString(pose, Minecraft.getInstance().font, enabled ? "ON" : "OFF", x + 9, y, 0xFFFFFFFF);
        }
    }
}
