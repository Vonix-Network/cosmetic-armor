package com.vonix.cosmeticarmor;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;

public final class CosmeticData implements INBTSerializable<CompoundTag> {
    private final ItemStack[] slots = {ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY};
    private boolean enabled = true;
    public boolean enabled() { return enabled; }
    public void setEnabled(boolean value) { enabled = value; }
    public ItemStack get(int slot) { return slot >= 0 && slot < 4 ? slots[slot] : ItemStack.EMPTY; }
    public void set(int slot, ItemStack stack) { if (slot >= 0 && slot < 4) slots[slot] = stack.copy(); }
    public void clear(int slot) { if (slot >= 0 && slot < 4) slots[slot] = ItemStack.EMPTY; }
    public boolean setFromHeld(Player player, int slot) { ItemStack held = player.getMainHandItem(); if (!(held.getItem() instanceof ArmorItem)) return false; set(slot, held); return true; }
    @Override public CompoundTag serializeNBT() { CompoundTag tag = new CompoundTag(); tag.putBoolean("Enabled", enabled); for (int i = 0; i < 4; i++) if (!slots[i].isEmpty()) tag.put("Slot" + i, slots[i].save(new CompoundTag())); return tag; }
    @Override public void deserializeNBT(CompoundTag tag) { enabled = !tag.contains("Enabled") || tag.getBoolean("Enabled"); for (int i = 0; i < 4; i++) slots[i] = tag.contains("Slot" + i) ? ItemStack.of(tag.getCompound("Slot" + i)) : ItemStack.EMPTY; }
    public void copyFrom(CosmeticData other) { enabled = other.enabled; for (int i = 0; i < 4; i++) slots[i] = other.slots[i].copy(); }
}
