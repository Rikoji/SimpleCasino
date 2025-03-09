package de.hilsmann.simpleCasino.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;

public class SlotMachineCalculator {
    private final ItemStack[][] slots = new ItemStack[3][10];
    private final Random random = new Random();
    private final SlotTheme theme;

    public SlotMachineCalculator(SlotTheme theme) {
        this.theme = theme;
    }

    public void calculateSlots() {
        for (int col = 0; col < 3; col++) {
            for (int row = 0; row < 10; row++) {
                slots[col][row] = new ItemStack(getRandomSlotItem());
            }
        }
    }

    public ItemStack[][] getSlots() {
        return slots;
    }

    public ItemStack getRandomSlotItem() {
        List<Material> reelItems = theme.getWeightedReelItems();
        return new ItemStack(reelItems.get(random.nextInt(reelItems.size())));
    }

    public int getMultiplierForMaterial(ItemStack item) {
        return theme.getWinningMultipliers().getOrDefault(item.getType(), 1);
    }
}
