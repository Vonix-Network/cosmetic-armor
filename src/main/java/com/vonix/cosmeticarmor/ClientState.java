package com.vonix.cosmeticarmor;

import net.minecraft.nbt.CompoundTag;
public final class ClientState {
    private static final CosmeticData DATA = new CosmeticData();
    private ClientState() {}
    public static CosmeticData data() { return DATA; }
    public static void load(CompoundTag tag) { DATA.deserializeNBT(tag); }
}
