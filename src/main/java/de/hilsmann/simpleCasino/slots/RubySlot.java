package de.hilsmann.simpleCasino.slots;

import de.hilsmann.simpleCasino.util.SlotTheme;
import org.bukkit.Material;
import java.util.LinkedHashMap;
import java.util.Map;

public class RubySlot extends SlotTheme {

    @Override
    public Map<Material, Integer> getWinningMultipliers() {
        Map<Material, Integer> multipliers = new LinkedHashMap<>();
        multipliers.put(Material.DIAMOND, 12);
        multipliers.put(Material.EMERALD, 8);
        multipliers.put(Material.REDSTONE, 6);
        multipliers.put(Material.LAPIS_LAZULI, 3);
        multipliers.put(Material.QUARTZ, 1);
        multipliers.put(Material.BONE, 0);
        return multipliers;
    }

    @Override
    public String getDisplayName(Material material) {
        return switch (material) {
            case DIAMOND -> "§bDiamant Jackpot!";
            case EMERALD -> "§aSmaragde Gewinn!";
            case REDSTONE -> "§cRedstone Power!";
            case LAPIS_LAZULI -> "§9Lapislazuli Bonus!";
            case QUARTZ -> "§fQuarz Trostpreis!";
            case BONE -> "§8Toter Knochen";
            default -> "§7Unbekannter Gewinn";
        };
    }

    @Override
    public Material[] getReelItems() {
        return new Material[]{Material.DIAMOND, Material.EMERALD, Material.REDSTONE, Material.LAPIS_LAZULI, Material.QUARTZ};
    }
}
