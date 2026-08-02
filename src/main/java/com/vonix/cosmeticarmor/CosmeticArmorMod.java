package com.vonix.cosmeticarmor;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(CosmeticArmorMod.MODID)
public final class CosmeticArmorMod {
    public static final String MODID = "cosmeticarmor";
    public CosmeticArmorMod() { FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup); FMLJavaModLoadingContext.get().getModEventBus().addListener(CosmeticCapability::register); MinecraftForge.EVENT_BUS.register(this); }
    private void commonSetup(FMLCommonSetupEvent event) { Network.register(); }
    @SubscribeEvent public void login(PlayerEvent.PlayerLoggedInEvent event) { if(event.getPlayer() instanceof ServerPlayer p) Network.sync(p); }
    @SubscribeEvent public void respawn(PlayerEvent.PlayerRespawnEvent event) { if(event.getPlayer() instanceof ServerPlayer p) Network.sync(p); }
    @SubscribeEvent public void changedDimension(PlayerEvent.PlayerChangedDimensionEvent event) { if(event.getPlayer() instanceof ServerPlayer p) Network.sync(p); }
}
