package com.vonix.cosmeticarmor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RenderArmEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import java.util.IdentityHashMap;
import java.util.Map;

/** Saves and restores actual equipment around every player/hand/arm render path. */
@Mod.EventBusSubscriber(modid = CosmeticArmorMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class CosmeticArmorRenderer {
    private static final EquipmentSlot[] SLOTS = {EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD};
    private static final Map<Player, ItemStack[]> RESTORE = new IdentityHashMap<>();
    private CosmeticArmorRenderer() {}

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void beforePlayer(RenderPlayerEvent.Pre event) { apply(event.getPlayer()); }
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void afterPlayer(RenderPlayerEvent.Post event) { restore(event.getPlayer()); }
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void beforeHand(RenderHandEvent event) { if (Minecraft.getInstance().player != null) apply(Minecraft.getInstance().player); }
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void afterHand(RenderHandEvent event) { if (Minecraft.getInstance().player != null) restore(Minecraft.getInstance().player); }
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void beforeArm(RenderArmEvent event) { if (event.getPlayer() != null) apply(event.getPlayer()); }
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void afterArm(RenderArmEvent event) { if (event.getPlayer() != null) restore(event.getPlayer()); }
    @SubscribeEvent public static void logout(ClientPlayerNetworkEvent.LoggedOutEvent event) { RESTORE.clear(); ClientState.clear(); }

    private static void apply(Player player) {
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

    private static void restore(Player player) {
        ItemStack[] old = RESTORE.remove(player);
        if (old == null) return;
        for (int i = 0; i < SLOTS.length; i++) player.setItemSlot(SLOTS[i], old[i]);
    }
}
