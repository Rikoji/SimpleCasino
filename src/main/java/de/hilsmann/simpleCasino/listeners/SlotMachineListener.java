package de.hilsmann.simpleCasino.listeners;

import de.hilsmann.simpleCasino.SimpleCasino;
import de.hilsmann.simpleCasino.util.SlotMachineLogic;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;

public class SlotMachineListener implements Listener {

    // Speichert alle aktiven Slot-Maschinen pro Spieler
    private final Map<Player, SlotMachineLogic> activeMachines = new HashMap<>();

    @EventHandler
    public void onSignCreate(SignChangeEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (!block.getType().name().contains("SIGN")) return;
        if (event.getLine(0) == null) return;

        if (event.getLine(0).equalsIgnoreCase("[Slot]")) {
            if (player.hasPermission("casino.admin")) {
                event.setLine(0, ChatColor.AQUA + ChatColor.BOLD.toString() + "[Slot]");
                player.sendMessage("§e" + SimpleCasino.prefix + " §aSlot-Schild erfolgreich erstellt!");

                if (block.getState() instanceof Sign sign) {
                    sign.setWaxed(true);
                    sign.update();
                }
            } else {
                player.sendMessage("§e" + SimpleCasino.prefix + " §cDu hast keine Berechtigung, ein Slot-Schild zu erstellen.");
            }
        }
    }

    @EventHandler
    public void onSignInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block == null || !block.getType().name().contains("SIGN")) return;

        Sign sign = (Sign) block.getState();
        if (!ChatColor.stripColor(sign.getLine(0)).equalsIgnoreCase("[Slot]")) return;

        Player player = event.getPlayer();
        if (!player.hasPermission("casino.play")) {
            player.sendMessage("§e" + SimpleCasino.prefix + " §cDas Casino ist derzeit geschlossen!");
            return;
        }

        int betAmount;
        try {
            betAmount = Integer.parseInt(sign.getLine(3));
        } catch (NumberFormatException e) {
            player.sendMessage("§e" + SimpleCasino.prefix + " §cUngültiger Einsatzbetrag.");
            return;
        }

        if (betAmount < 0) betAmount = Math.abs(betAmount);

        // Erstelle eine neue SlotMachineLogic mit Theme-Auswahl
        SlotMachineLogic slotMachine = new SlotMachineLogic(player, betAmount);
        activeMachines.put(player, slotMachine);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getClickedInventory() == null || event.getCurrentItem() == null) return;
        String title = ChatColor.stripColor(event.getView().getTitle());
        if (title.equalsIgnoreCase("Slots")) {
            event.setCancelled(true); // Verhindert, dass Spieler Items herausnehmen

            SlotMachineLogic slotMachine = activeMachines.get(player);
            if (slotMachine == null) return;

            // Prüft, ob wir noch in der Theme-Auswahl sind
            if (slotMachine.isThemeSelectionActive()) {
                String theme = switch (event.getCurrentItem().getType()) {
                    case REDSTONE -> "Ruby";
                    case GLOW_BERRIES -> "Berry";
                    case RED_BED -> "Retro";
                    default -> null;
                };

                if (theme != null) {
                    slotMachine.startGame(theme);
                }
                return;
            }

            // Prüft, ob wir bereits im Spielmodus sind
            if (slotMachine.isGameActive()) {
                if (event.getCurrentItem().getType() == Material.LEVER) {
                    slotMachine.spin();
                }
            }
        }
    }
}
