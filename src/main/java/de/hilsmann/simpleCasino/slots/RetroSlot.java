package de.hilsmann.simpleCasino.slots;

import de.hilsmann.simpleCasino.util.SlotTheme;
import org.bukkit.Material;

import java.util.LinkedHashMap;
import java.util.Map;

public class RetroSlot extends SlotTheme {

    @Override
    public Map<Material, Integer> getWinningMultipliers() {
        Map<Material, Integer> multipliers = new LinkedHashMap<>();
        multipliers.put(Material.RED_BED, 10);
        multipliers.put(Material.MAP, 6);
        multipliers.put(Material.CLOCK, 4);
        multipliers.put(Material.COMPASS, 3);
        multipliers.put(Material.COOKED_BEEF, 1);
        multipliers.put(Material.BONE, 0);
        return multipliers;
    }

    @Override
    public String getDisplayName(Material material) {
        return switch (material) {
            case RED_BED -> "§cAlter Bett-Gewinn!";
            case MAP -> "§6Geheimnisvolle Karte!";
            case CLOCK -> "§eZeitloser Jackpot!";
            case COMPASS -> "§bRichtungsweiser Gewinn!";
            case COOKED_BEEF -> "§7Trostpreis: Steak!";
            case BONE -> "§8Toter Knochen";
            default -> "§7Unbekannter Gewinn";
        };
    }

    @Override
    public Material[] getReelItems() {
        return new Material[]{
                Material.RED_BED,
                Material.MAP,
                Material.CLOCK,
                Material.COMPASS,
                Material.COOKED_BEEF
        };
    }
}
