package com.vonix.cosmeticarmor;

import net.minecraft.nbt.CompoundTag;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Client cache mirrors the reference mod's per-player cosmetic state. */
public final class ClientState {
    private static final Map<UUID, CosmeticData> DATA = new HashMap<>();
    private static final UUID LOCAL_KEY = new UUID(0L, 0L);
    private ClientState() {}
    public static CosmeticData data() { return DATA.computeIfAbsent(LOCAL_KEY, ignored -> new CosmeticData()); }
    public static CosmeticData data(UUID playerId) { return DATA.computeIfAbsent(playerId, ignored -> new CosmeticData()); }
    public static void load(UUID playerId, CompoundTag tag) { data(playerId).deserializeNBT(tag == null ? new CompoundTag() : tag); }
    public static void clear() { DATA.clear(); }
}
