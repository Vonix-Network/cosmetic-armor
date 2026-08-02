package com.vonix.cosmeticarmor;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CosmeticArmorMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class CosmeticCapability {
    public static final Capability<CosmeticData> COSMETICS = CapabilityManager.get(new CapabilityToken<CosmeticData>() {});
    private static final ResourceLocation KEY = new ResourceLocation(CosmeticArmorMod.MODID, "cosmetics");
    private CosmeticCapability() {}
    public static void register(net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent event) { event.register(CosmeticData.class); }
    @SubscribeEvent public static void attach(AttachCapabilitiesEvent<net.minecraft.world.entity.Entity> event) { if (event.getObject() instanceof Player) event.addCapability(KEY, new Provider()); }
    @SubscribeEvent public static void clone(PlayerEvent.Clone event) { event.getOriginal().reviveCaps(); event.getOriginal().getCapability(COSMETICS).ifPresent(old -> event.getPlayer().getCapability(COSMETICS).ifPresent(now -> now.copyFrom(old))); event.getOriginal().invalidateCaps(); }
    public static final class Provider implements net.minecraftforge.common.capabilities.ICapabilityProvider, net.minecraftforge.common.util.INBTSerializable<CompoundTag> {
        private final CosmeticData data = new CosmeticData(); private final LazyOptional<CosmeticData> optional = LazyOptional.of(() -> data);
        @Override public <T> LazyOptional<T> getCapability(Capability<T> cap, net.minecraft.core.Direction side) { return cap == COSMETICS ? optional.cast() : LazyOptional.empty(); }
        @Override public CompoundTag serializeNBT() { return data.serializeNBT(); }
        @Override public void deserializeNBT(CompoundTag nbt) { data.deserializeNBT(nbt); }
    }
}
