package de.hilsmann.simpleCasino.util;

import de.hilsmann.coinAPI.API.CoinAPI;
import de.hilsmann.simpleCasino.SimpleCasino;
import de.hilsmann.simpleCasino.slots.BerrySlot;
import de.hilsmann.simpleCasino.slots.RetroSlot;
import de.hilsmann.simpleCasino.slots.RubySlot;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SlotMachineLogic {
    private final Player player;
    private final Inventory inventory;
    private SlotMachineCalculator calculator;
    private int betAmount;
    private int multiplier = 0;
    private ItemStack[][] slots;

    private static final Map<String, SlotTheme> THEMES = new HashMap<>();

    static {
        THEMES.put("Ruby", new RubySlot());
        THEMES.put("Berry", new BerrySlot());
        THEMES.put("Retro", new RetroSlot());
    }

    private enum GameState {
        THEME_SELECTION,
        PLAYING
    }

    private GameState currentState = GameState.THEME_SELECTION;


    public SlotMachineLogic(Player player, int betAmount) {
        this.player = player;
        this.betAmount = betAmount;
        this.inventory = Bukkit.createInventory(null, 54, "§3§lSlots");
        setupThemeSelection();
    }

    private void setupThemeSelection() {
        inventory.setItem(11, createItem(Material.REDSTONE, "§cRubin-Slot", "Spiele mit Edelsteinen!"));
        inventory.setItem(13, createItem(Material.GLOW_BERRIES, "§6Berry-Slot", "Früchtespaß & mehr!"));
        inventory.setItem(15, createItem(Material.RED_BED, "§6Retro-Slot", "Nostalgische Items!"));
        ItemStack glass = createItem(Material.BLACK_STAINED_GLASS_PANE, "§0");
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, glass);
            }
        }
        player.openInventory(inventory);
    }

    public void startGame(String themeName) {
        SlotTheme theme = THEMES.get(themeName);
        if (theme == null) {
            player.sendMessage("§cUngültiges Slot-Theme!");
            return;
        }

        this.calculator = new SlotMachineCalculator(theme);
        this.inventory.clear();
        setupGameInventory(theme);
        currentState = GameState.PLAYING;
        player.sendMessage("🎰 Du spielst am " + themeName + "-Slot!");
    }

    private void setupGameInventory(SlotTheme theme) {
        ItemStack blank = createItem(Material.GREEN_WOOL, "§aLeer");
        for (int i = 0; i < 3; i++) {
            for (int k = 11; k < 16; k += 2) {
                inventory.setItem(k + i * 9, blank);
            }
        }

        ItemStack glass = createItem(Material.BLACK_STAINED_GLASS_PANE, "§0");
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, glass);
            }
        }

        ItemStack lever = createItem(Material.LEVER, "§a§lHebel", "Kosten: " + betAmount + " Jetons.");
        inventory.setItem(53, lever);

        // Gewinnübersicht hinzufügen
        for (Map.Entry<Integer, ItemStack> entry : theme.getWinOverview().entrySet()) {
            inventory.setItem(entry.getKey(), entry.getValue());
        }

        player.openInventory(inventory);
    }

    public void spin() {
        if (CoinAPI.getCoins(player.getUniqueId().toString()) < betAmount) {
            player.sendMessage("§c§lDu hast nicht genug Coins, um zu spielen.");
            return;
        }

        CoinAPI.removeCoins(player.getUniqueId().toString(), betAmount);
        calculator.calculateSlots();
        slots = calculator.getSlots();
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BANJO, 1, 1f);
        inventory.setItem(53, new ItemStack(Material.AIR));

        animateSpin(0);
    }

    private void animateSpin(int step) {
        if (step >= 10) {
            evaluateResults();
            return;
        }

        Bukkit.getScheduler().runTaskLater(SimpleCasino.getInstance(), () -> {
            for (int col = 0; col < 3; col++) {
                for (int row = 9; row > 0; row--) {
                    slots[col][row] = slots[col][row - 1];
                }
                slots[col][0] = new ItemStack(calculator.getRandomSlotItem());
            }

            updateInventory();
            animateSpin(step + 1);
        }, 4);
    }

    private void updateInventory() {
        for (int col = 0; col < 3; col++) {
            for (int row = 0; row < 3; row++) {
                int slotIndex = 11 + row * 9 + col * 2;
                inventory.setItem(slotIndex, slots[col][row]);
            }
        }
        player.updateInventory();
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.8f, 2f);
    }

    private void evaluateResults() {
        multiplier = 0;
        checkWin(11, 13, 15);
        checkWin(20, 22, 24);
        checkWin(29, 31, 33);
        checkWin(11, 20, 29);
        checkWin(13, 22, 31);
        checkWin(15, 24, 33);
        finalizeResults();
    }

    private void checkWin(int slot1, int slot2, int slot3) {
        ItemStack first = inventory.getItem(slot1);
        ItemStack second = inventory.getItem(slot2);
        ItemStack third = inventory.getItem(slot3);

        if (first != null && second != null && third != null) {
            // Überprüfen, ob es sich um Knochen handelt – Falls ja, ignoriere diesen "Gewinn"
            if (first.getType() == Material.BONE && second.getType() == Material.BONE && third.getType() == Material.BONE) {
                player.playSound(player.getLocation(), Sound.ENTITY_GLOW_SQUID_HURT, 1, 1.2f);
                return; // Kein Gewinn für Knochen!
            }

            if (first.getType() == second.getType() && first.getType() == third.getType()) {
                multiplier += calculator.getMultiplierForMaterial(first);
                highlightWinningSlots(first.getType(), slot1, slot2, slot3);
            }
        }
    }


    private void highlightWinningSlots(Material material, int... slots) {
        ItemStack highlighted = new ItemStack(material);
        highlighted.addUnsafeEnchantment(org.bukkit.enchantments.Enchantment.AQUA_AFFINITY, 1);

        for (int slot : slots) {
            inventory.setItem(slot, highlighted);
        }

        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_SNARE, 1, 1f);
        player.updateInventory();
    }

    private void finalizeResults() {
        Bukkit.getScheduler().runTaskLater(SimpleCasino.getInstance(), () -> {
            if (multiplier == 0) {
                player.sendMessage("§cDu hast §e" + betAmount + " §cCoins verloren.");
                CoinAPI.addCoins("e0349cab-b6f2-4465-9084-4df6631ae4d0", betAmount);
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 1, 1f);
            } else {
                int winnings = betAmount * multiplier;
                CoinAPI.addCoins(player.getUniqueId().toString(), winnings);
                player.sendMessage("§3Du hast §e" + winnings + " §3gewonnen!");
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1f);
            }

            inventory.setItem(53, createItem(Material.LEVER, "§a§lHebel", "Kosten: " + betAmount + " Jetons."));
            multiplier = 0;
        }, 2);
    }

    private ItemStack createItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        if (lore.length > 0) {
            meta.setLore(Arrays.asList(lore));
        }
        item.setItemMeta(meta);
        return item;
    }

    public boolean isThemeSelectionActive() {
        return currentState == GameState.THEME_SELECTION;
    }

    public boolean isGameActive() {
        return currentState == GameState.PLAYING;
    }

}
