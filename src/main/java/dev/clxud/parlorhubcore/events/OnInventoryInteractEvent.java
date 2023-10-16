package dev.clxud.parlorhubcore.events;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryInteractEvent;

public class OnInventoryInteractEvent implements Listener {
    @EventHandler
    public void onInventoryInteract(InventoryInteractEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (!player.getGameMode().equals(GameMode.CREATIVE)) {
            if (player.getItemOnCursor().getType() == Material.COMPASS) {
                event.setCancelled(true);
            }
        }

    }
}
