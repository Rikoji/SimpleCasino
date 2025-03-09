package de.hilsmann.simpleCasino.slots;

import de.hilsmann.simpleCasino.util.SlotTheme;
import org.bukkit.Material;
import java.util.LinkedHashMap;
import java.util.Map;

public class BerrySlot extends SlotTheme {

    @Override
    public Map<Material, Integer> getWinningMultipliers() {
        Map<Material, Integer> multipliers = new LinkedHashMap<>();
        multipliers.put(Material.GOLDEN_APPLE, 12);
        multipliers.put(Material.GLOW_BERRIES, 8);
        multipliers.put(Material.SWEET_BERRIES, 6);
        multipliers.put(Material.CARROT, 3);
        multipliers.put(Material.BEETROOT, 1);
        multipliers.put(Material.BONE, 0);
        return multipliers;
    }

    @Override
    public String getDisplayName(Material material) {
        return switch (material) {
            case GOLDEN_APPLE -> "§6Legendärer Apfel!";
            case GLOW_BERRIES -> "§eLeuchtende Beeren!";
            case SWEET_BERRIES -> "§cSüße Überraschung!";
            case CARROT -> "§6Goldene Karotte!";
            case BEETROOT -> "§4Rote Powerfrucht!";
            case BONE -> "§8Toter Knochen";
            default -> "§7Unbekannter Gewinn";
        };
    }

    @Override
    public Material[] getReelItems() {
        return new Material[]{Material.GOLDEN_APPLE, Material.GLOW_BERRIES, Material.SWEET_BERRIES, Material.CARROT, Material.BEETROOT};
    }
}
