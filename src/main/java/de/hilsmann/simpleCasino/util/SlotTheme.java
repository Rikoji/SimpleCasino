package de.hilsmann.simpleCasino.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class SlotTheme {

    public abstract Map<Material, Integer> getWinningMultipliers();

    public abstract String getDisplayName(Material material);

    public abstract Material[] getReelItems();

    /**
     * Erstellt die Gewinnübersicht für das Inventar.
     * Zeigt an, welche Items mit welchem Multiplikator gewinnen können.
     */
    public Map<Integer, ItemStack> getWinOverview() {
        Map<Integer, ItemStack> overview = new LinkedHashMap<>();
        int slotIndex = 45; // Startplatz in der untersten Inventarreihe

        for (Map.Entry<Material, Integer> entry : getWinningMultipliers().entrySet()) {
            Material material = entry.getKey();
            int multiplier = entry.getValue();

            ItemStack item = new ItemStack(material);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(getDisplayName(material));
            meta.setLore(Arrays.asList("§eGewinn: x" + multiplier));
            item.setItemMeta(meta);

            overview.put(slotIndex++, item);
        }

        return overview;
    }

    /**
     * Erstellt eine gewichtete Liste von Reel-Items basierend auf ihren Gewinnmultiplikatoren.
     * Seltene Gewinne erscheinen seltener, kleinere Gewinne öfter.
     */
    public List<Material> getWeightedReelItems() {
        List<Material> weightedReel = new ArrayList<>();

        for (Map.Entry<Material, Integer> entry : getWinningMultipliers().entrySet()) {
            Material material = entry.getKey();
            int multiplier = entry.getValue();

            // Je höher der Multiplikator, desto seltener erscheint das Item im Reel
            int weight;
            if (multiplier >= 10) weight = 4;  // Jackpot → Extrem selten
            else if (multiplier >= 8) weight = 6; // Sehr selten
            else if (multiplier >= 5) weight = 12; // Selten
            else if (multiplier >= 2) weight = 29; // Mittelhäufig
            else if (multiplier == 1) weight = 42; // Häufig Trostpreis
            else weight = 7;  // Nieten bzw. Störitems

            for (int i = 0; i < weight; i++) {
                weightedReel.add(material);
            }
        }

        return weightedReel;
    }
}

