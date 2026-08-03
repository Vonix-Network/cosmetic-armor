package com.vonix.cosmeticarmor;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;

/** Server-owned four-slot cosmetic wardrobe. Slot order is feet, legs, chest, head. */
public final class CosmeticData implements INBTSerializable<CompoundTag> {
    public static final int SLOT_COUNT = 4;
    private final ItemStack[] slots = {ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY};
    private final boolean[] showCosmetic = {true, true, true, true};
    private boolean enabled = true;

    public boolean enabled() { return enabled; }
    public void setEnabled(boolean value) { enabled = value; }
    public boolean showCosmetic(int slot) { return valid(slot) && showCosmetic[slot]; }
    public void setShowCosmetic(int slot, boolean value) { if (valid(slot)) showCosmetic[slot] = value; }
    public ItemStack get(int slot) { return valid(slot) ? slots[slot].copy() : ItemStack.EMPTY; }
    public void set(int slot, ItemStack stack) { if (valid(slot)) { slots[slot] = stack.copy(); slots[slot].setCount(Math.min(1, slots[slot].getCount())); } }
    public void clear(int slot) { if (valid(slot)) slots[slot] = ItemStack.EMPTY; }

    /** Copies, but does not consume, one valid armor item from the player's main hand. */
    public boolean setFromHeld(Player player, int slot) {
        if (!valid(slot) || player == null) return false;
        ItemStack held = player.getMainHandItem();
        if (held.isEmpty() || !held.canEquip(equipmentSlot(slot), player)) return false;
        set(slot, held);
        return true;
    }

    public static EquipmentSlot equipmentSlot(int slot) {
        switch (slot) {
            case 0: return EquipmentSlot.FEET;
            case 1: return EquipmentSlot.LEGS;
            case 2: return EquipmentSlot.CHEST;
            case 3: return EquipmentSlot.HEAD;
            default: throw new IllegalArgumentException("Invalid cosmetic slot: " + slot);
        }
    }

    private static boolean valid(int slot) { return slot >= 0 && slot < SLOT_COUNT; }

    @Override public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("Enabled", enabled);
        for (int i = 0; i < SLOT_COUNT; i++) {
            tag.putBoolean("Show" + i, showCosmetic[i]);
            if (!slots[i].isEmpty()) tag.put("Slot" + i, slots[i].save(new CompoundTag()));
        }
        return tag;
    }

    @Override public void deserializeNBT(CompoundTag tag) {
        enabled = !tag.contains("Enabled") || tag.getBoolean("Enabled");
        for (int i = 0; i < SLOT_COUNT; i++) {
            showCosmetic[i] = !tag.contains("Show" + i) || tag.getBoolean("Show" + i);
            slots[i] = tag.contains("Slot" + i) ? ItemStack.of(tag.getCompound("Slot" + i)) : ItemStack.EMPTY;
            if (!slots[i].isEmpty()) slots[i].setCount(1);
        }
    }

    public void copyFrom(CosmeticData other) {
        enabled = other.enabled;
        for (int i = 0; i < SLOT_COUNT; i++) { slots[i] = other.slots[i].copy(); showCosmetic[i] = other.showCosmetic[i]; }
    }
}
