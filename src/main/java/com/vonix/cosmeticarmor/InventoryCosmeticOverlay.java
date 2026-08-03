package com.vonix.cosmeticarmor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent.InitScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Adds compact controls to the vanilla inventory without replacing the vanilla menu. */
@Mod.EventBusSubscriber(modid = CosmeticArmorMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class InventoryCosmeticOverlay {
    private static final int PANEL_W = 94;
    private static final int ROW_H = 22;
    private static final String[] NAMES = {"Boots", "Legs", "Chest", "Helm"};
    private InventoryCosmeticOverlay() {}

    @SubscribeEvent
    public static void addToInventory(InitScreenEvent.Post event) {
        if (!(event.getScreen() instanceof InventoryScreen screen)) return;
        int y = screen.getGuiTop() + 4;
        int x = screen.getGuiLeft() + 180;
        if (x + PANEL_W > screen.width) x = Math.max(2, screen.width - PANEL_W - 2);
        CosmeticData local = localData();
        event.addListener(new Button(x, y, PANEL_W, 18, new TextComponent(toggleLabel(local.enabled())), button -> {
            boolean enabled = !localData().enabled();
            Network.CHANNEL.sendToServer(new Network.TogglePacket(enabled));
            button.setMessage(new TextComponent(toggleLabel(enabled)));
        }));
        for (int i = 0; i < CosmeticData.SLOT_COUNT; i++) {
            final int slot = i;
            int row = y + 22 + i * ROW_H;
            CosmeticData data = localData();
            event.addListener(new Button(x, row, 38, 18, new TextComponent(NAMES[i]), button -> Network.CHANNEL.sendToServer(new Network.HeldPacket(slot))));
            event.addListener(new Button(x + 40, row, 20, 18, new TextComponent(data.showCosmetic(i) ? "V" : "H"), button -> {
                boolean visible = !localData().showCosmetic(slot);
                Network.CHANNEL.sendToServer(new Network.SlotVisibilityPacket(slot, visible));
                button.setMessage(new TextComponent(visible ? "V" : "H"));
            }));
            event.addListener(new Button(x + 62, row, 32, 18, new TextComponent("Clear"), button -> Network.CHANNEL.sendToServer(new Network.ClearPacket(slot))));
        }
    }

    private static CosmeticData localData() {
        if (Minecraft.getInstance().player == null) return ClientState.data();
        return ClientState.data(Minecraft.getInstance().player.getUUID());
    }

    private static String toggleLabel(boolean enabled) { return enabled ? "Cosmetics: ON" : "Cosmetics: OFF"; }
}
