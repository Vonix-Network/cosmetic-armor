package com.vonix.cosmeticarmor;

import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent.InitScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CosmeticArmorMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class InventoryCosmeticOverlay {
    private static final int PANEL_X = 180, PANEL_W = 86, ROW_H = 24;
    private static final String[] NAMES = {"Boots", "Legs", "Chest", "Helm"};
    private InventoryCosmeticOverlay() {}
    @SubscribeEvent public static void addToInventory(InitScreenEvent.Post event) {
        if (!(event.getScreen() instanceof InventoryScreen screen)) return;
        int x = screen.getGuiLeft() + PANEL_X, y = screen.getGuiTop();
        event.addListener(new Button(x, y, PANEL_W, 20, new TextComponent("Cosmetics"), b -> Network.CHANNEL.sendToServer(new Network.TogglePacket(!ClientState.data().enabled()))));
        for (int i = 0; i < 4; i++) { final int slot=i; int row=y+24+i*ROW_H; event.addListener(new Button(x,row,42,20,new TextComponent(NAMES[i]),b->Network.CHANNEL.sendToServer(new Network.HeldPacket(slot)))); event.addListener(new Button(x+44,row,42,20,new TextComponent("Clear"),b->Network.CHANNEL.sendToServer(new Network.ClearPacket(slot)))); }
    }
}
