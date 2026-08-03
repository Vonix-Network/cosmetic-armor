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
    private static final String PROTOCOL = "3";
    private static int id;
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(CosmeticArmorMod.MODID, "main"), () -> PROTOCOL, PROTOCOL::equals, PROTOCOL::equals);
    private Network() {}

    public static void register() {
        CHANNEL.registerMessage(id++, SyncPacket.class, SyncPacket::encode, SyncPacket::decode, SyncPacket::handle);
        CHANNEL.registerMessage(id++, TogglePacket.class, TogglePacket::encode, TogglePacket::decode, TogglePacket::handle);
        CHANNEL.registerMessage(id++, HeldPacket.class, HeldPacket::encode, HeldPacket::decode, HeldPacket::handle);
        CHANNEL.registerMessage(id++, ClearPacket.class, ClearPacket::encode, ClearPacket::decode, ClearPacket::handle);
        CHANNEL.registerMessage(id++, SlotVisibilityPacket.class, SlotVisibilityPacket::encode, SlotVisibilityPacket::decode, SlotVisibilityPacket::handle);
    }

    /** Sends a player's cosmetics to every currently connected client. */
    public static void syncAll(ServerPlayer subject) {
        if (subject == null || subject.server == null) return;
        for (ServerPlayer recipient : subject.server.getPlayerList().getPlayers()) syncTo(subject, recipient);
    }

    /** Sends one player's cosmetics to one client, including clients observing that player. */
    public static void syncTo(ServerPlayer subject, ServerPlayer recipient) {
        if (subject == null || recipient == null) return;
        subject.getCapability(CosmeticCapability.COSMETICS).ifPresent(data -> CHANNEL.send(PacketDistributor.PLAYER.with(() -> recipient), new SyncPacket(subject.getUUID(), data)));
    }

    /** Sends all currently loaded cosmetic states to a newly connected client. */
    public static void syncAllTo(ServerPlayer recipient) {
        if (recipient == null || recipient.server == null) return;
        for (ServerPlayer subject : recipient.server.getPlayerList().getPlayers()) syncTo(subject, recipient);
    }

    public static final class SyncPacket {
        private final java.util.UUID playerId;
        private final net.minecraft.nbt.CompoundTag tag;
        public SyncPacket(java.util.UUID playerId, CosmeticData data) { this.playerId = playerId; this.tag = data.serializeNBT(); }
        private SyncPacket(java.util.UUID playerId, net.minecraft.nbt.CompoundTag tag) { this.playerId = playerId; this.tag = tag; }
        static void encode(SyncPacket packet, FriendlyByteBuf buffer) { buffer.writeUUID(packet.playerId); buffer.writeNbt(packet.tag); }
        static SyncPacket decode(FriendlyByteBuf buffer) { return new SyncPacket(buffer.readUUID(), buffer.readNbt()); }
        static void handle(SyncPacket packet, Supplier<NetworkEvent.Context> context) { context.get().enqueueWork(() -> DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> () -> ClientState.load(packet.playerId, packet.tag))); context.get().setPacketHandled(true); }
    }

    public static final class TogglePacket {
        private final boolean enabled;
        public TogglePacket(boolean enabled) { this.enabled = enabled; }
        static void encode(TogglePacket packet, FriendlyByteBuf buffer) { buffer.writeBoolean(packet.enabled); }
        static TogglePacket decode(FriendlyByteBuf buffer) { return new TogglePacket(buffer.readBoolean()); }
        static void handle(TogglePacket packet, Supplier<NetworkEvent.Context> context) { NetworkEvent.Context ctx = context.get(); ctx.enqueueWork(() -> { ServerPlayer player = ctx.getSender(); if (player != null) player.getCapability(CosmeticCapability.COSMETICS).ifPresent(data -> { data.setEnabled(packet.enabled); syncAll(player); }); }); ctx.setPacketHandled(true); }
    }

    public static final class HeldPacket {
        private final int slot;
        public HeldPacket(int slot) { this.slot = slot; }
        static void encode(HeldPacket packet, FriendlyByteBuf buffer) { buffer.writeVarInt(packet.slot); }
        static HeldPacket decode(FriendlyByteBuf buffer) { return new HeldPacket(buffer.readVarInt()); }
        static void handle(HeldPacket packet, Supplier<NetworkEvent.Context> context) { NetworkEvent.Context ctx = context.get(); ctx.enqueueWork(() -> { ServerPlayer player = ctx.getSender(); if (player != null && packet.slot >= 0 && packet.slot < CosmeticData.SLOT_COUNT && player.getCapability(CosmeticCapability.COSMETICS).map(data -> data.setFromHeld(player, packet.slot)).orElse(false)) syncAll(player); }); ctx.setPacketHandled(true); }
    }

    public static final class ClearPacket {
        private final int slot;
        public ClearPacket(int slot) { this.slot = slot; }
        static void encode(ClearPacket packet, FriendlyByteBuf buffer) { buffer.writeVarInt(packet.slot); }
        static ClearPacket decode(FriendlyByteBuf buffer) { return new ClearPacket(buffer.readVarInt()); }
        static void handle(ClearPacket packet, Supplier<NetworkEvent.Context> context) { NetworkEvent.Context ctx = context.get(); ctx.enqueueWork(() -> { ServerPlayer player = ctx.getSender(); if (player != null && packet.slot >= 0 && packet.slot < CosmeticData.SLOT_COUNT) player.getCapability(CosmeticCapability.COSMETICS).ifPresent(data -> { data.clear(packet.slot); syncAll(player); }); }); ctx.setPacketHandled(true); }
    }

    public static final class SlotVisibilityPacket {
        private final int slot;
        private final boolean visible;
        public SlotVisibilityPacket(int slot, boolean visible) { this.slot = slot; this.visible = visible; }
        static void encode(SlotVisibilityPacket packet, FriendlyByteBuf buffer) { buffer.writeVarInt(packet.slot); buffer.writeBoolean(packet.visible); }
        static SlotVisibilityPacket decode(FriendlyByteBuf buffer) { return new SlotVisibilityPacket(buffer.readVarInt(), buffer.readBoolean()); }
        static void handle(SlotVisibilityPacket packet, Supplier<NetworkEvent.Context> context) { NetworkEvent.Context ctx = context.get(); ctx.enqueueWork(() -> { ServerPlayer player = ctx.getSender(); if (player != null && packet.slot >= 0 && packet.slot < CosmeticData.SLOT_COUNT) player.getCapability(CosmeticCapability.COSMETICS).ifPresent(data -> { data.setShowCosmetic(packet.slot, packet.visible); syncAll(player); }); }); ctx.setPacketHandled(true); }
    }
}
