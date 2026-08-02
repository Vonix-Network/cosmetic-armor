package com.vonix.cosmeticarmor;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraft.resources.ResourceLocation;
import java.util.function.Supplier;

public final class Network {
    private static final String PROTOCOL = "1"; private static int id;
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(CosmeticArmorMod.MODID, "main"), () -> PROTOCOL, PROTOCOL::equals, PROTOCOL::equals);
    private Network() {}
    public static void register() { CHANNEL.registerMessage(id++, SyncPacket.class, SyncPacket::encode, SyncPacket::decode, SyncPacket::handle); CHANNEL.registerMessage(id++, TogglePacket.class, TogglePacket::encode, TogglePacket::decode, TogglePacket::handle); CHANNEL.registerMessage(id++, HeldPacket.class, HeldPacket::encode, HeldPacket::decode, HeldPacket::handle); CHANNEL.registerMessage(id++, ClearPacket.class, ClearPacket::encode, ClearPacket::decode, ClearPacket::handle); }
    public static void sync(ServerPlayer player) { player.getCapability(CosmeticCapability.COSMETICS).ifPresent(data -> CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new SyncPacket(data))); }
    public static final class SyncPacket { private final net.minecraft.nbt.CompoundTag tag; public SyncPacket(CosmeticData data){tag=data.serializeNBT();} private SyncPacket(net.minecraft.nbt.CompoundTag tag){this.tag=tag;} static void encode(SyncPacket p,FriendlyByteBuf b){b.writeNbt(p.tag);} static SyncPacket decode(FriendlyByteBuf b){return new SyncPacket(b.readNbt());} static void handle(SyncPacket p,Supplier<NetworkEvent.Context> c){c.get().enqueueWork(()->DistExecutor.safeRunWhenOn(Dist.CLIENT,()->()->ClientState.load(p.tag)));c.get().setPacketHandled(true);} }
    public static final class TogglePacket { private final boolean enabled; public TogglePacket(boolean e){enabled=e;} static void encode(TogglePacket p,FriendlyByteBuf b){b.writeBoolean(p.enabled);} static TogglePacket decode(FriendlyByteBuf b){return new TogglePacket(b.readBoolean());} static void handle(TogglePacket p,Supplier<NetworkEvent.Context> c){NetworkEvent.Context x=c.get();x.enqueueWork(()->{ServerPlayer q=x.getSender();if(q!=null)q.getCapability(CosmeticCapability.COSMETICS).ifPresent(d->{d.setEnabled(p.enabled);sync(q);});});x.setPacketHandled(true);} }
    public static final class HeldPacket { private final int slot; public HeldPacket(int s){slot=s;} static void encode(HeldPacket p,FriendlyByteBuf b){b.writeVarInt(p.slot);} static HeldPacket decode(FriendlyByteBuf b){return new HeldPacket(b.readVarInt());} static void handle(HeldPacket p,Supplier<NetworkEvent.Context> c){NetworkEvent.Context x=c.get();x.enqueueWork(()->{ServerPlayer q=x.getSender();if(q!=null&&p.slot>=0&&p.slot<4&&q.getCapability(CosmeticCapability.COSMETICS).map(d->d.setFromHeld(q,p.slot)).orElse(false))sync(q);});x.setPacketHandled(true);} }
    public static final class ClearPacket { private final int slot; public ClearPacket(int s){slot=s;} static void encode(ClearPacket p,FriendlyByteBuf b){b.writeVarInt(p.slot);} static ClearPacket decode(FriendlyByteBuf b){return new ClearPacket(b.readVarInt());} static void handle(ClearPacket p,Supplier<NetworkEvent.Context> c){NetworkEvent.Context x=c.get();x.enqueueWork(()->{ServerPlayer q=x.getSender();if(q!=null&&p.slot>=0&&p.slot<4)q.getCapability(CosmeticCapability.COSMETICS).ifPresent(d->{d.clear(p.slot);sync(q);});});x.setPacketHandled(true);} }
}
