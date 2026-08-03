package com.vonix.cosmeticarmor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Mirrors the reference mod's render-state technique for Forge 1.18.2: save every
 * affected armor slot before rendering and restore it afterward, even when the
 * player has empty cosmetic slots or the feature is disabled.
 */
@Mod.EventBusSubscriber(modid = CosmeticArmorMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class CosmeticArmorRenderer {
    private static final EquipmentSlot[] SLOTS = {EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD};
    private static final Map<Player, ItemStack[]> RESTORE = new IdentityHashMap<>();
    private CosmeticArmorRenderer() {}

    @SubscribeEvent
    public static void before(RenderPlayerEvent.Pre event) {
        Player player = event.getPlayer();
        if (RESTORE.containsKey(player)) return;
        ItemStack[] old = new ItemStack[CosmeticData.SLOT_COUNT];
        for (int i = 0; i < SLOTS.length; i++) old[i] = player.getItemBySlot(SLOTS[i]).copy();
        RESTORE.put(player, old);
        CosmeticData data = ClientState.data(player.getUUID());
        if (!data.enabled()) return;
        for (int i = 0; i < SLOTS.length; i++) {
            if (!data.showCosmetic(i)) continue;
            ItemStack cosmetic = data.get(i);
            if (!cosmetic.isEmpty()) player.setItemSlot(SLOTS[i], cosmetic);
        }
    }

    @SubscribeEvent
    public static void after(RenderPlayerEvent.Post event) { restore(event.getPlayer()); }

    @SubscribeEvent
    public static void logout(ClientPlayerNetworkEvent.LoggedOutEvent event) {
        RESTORE.clear();
        ClientState.clear();
    }

    private static void restore(Player player) {
        ItemStack[] old = RESTORE.remove(player);
        if (old == null) return;
        for (int i = 0; i < SLOTS.length; i++) player.setItemSlot(SLOTS[i], old[i]);
    }
}
