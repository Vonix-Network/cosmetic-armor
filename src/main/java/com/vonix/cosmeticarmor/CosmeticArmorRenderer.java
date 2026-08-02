package com.vonix.cosmeticarmor;

import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import java.util.IdentityHashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = CosmeticArmorMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class CosmeticArmorRenderer {
    private static final Map<Player, ItemStack[]> RESTORE = new IdentityHashMap<>();
    private CosmeticArmorRenderer() {}
    @SubscribeEvent public static void before(RenderPlayerEvent.Pre event) { Player p=event.getPlayer(); if(!ClientState.data().enabled()||RESTORE.containsKey(p))return; ItemStack[] old=new ItemStack[4]; for(int i=0;i<4;i++){old[i]=p.getInventory().getArmor(i).copy();p.getInventory().armor.set(i,ClientState.data().get(i).copy());} RESTORE.put(p,old); }
    @SubscribeEvent public static void after(RenderPlayerEvent.Post event) { ItemStack[] old=RESTORE.remove(event.getPlayer()); if(old!=null)for(int i=0;i<4;i++)event.getPlayer().getInventory().armor.set(i,old[i]); }
}
