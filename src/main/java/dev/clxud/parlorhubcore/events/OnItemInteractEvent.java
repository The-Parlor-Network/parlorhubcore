package dev.clxud.parlorhubcore.events;

import dev.clxud.parlorhubcore.Plugin;
import dev.clxud.parlorhubcore.utils.ServerSelector;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class OnItemInteractEvent implements Listener {

    private Plugin plugin;
    private final ServerSelector ss;

    public OnItemInteractEvent(Plugin plugin) {

        this.plugin = plugin;
        this.ss = new ServerSelector(this.plugin);
    }


    @EventHandler
    public void onItemInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (!player.getGameMode().equals(GameMode.CREATIVE)) {
            if (item != null && item.getType() == Material.COMPASS) {
                if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    event.setCancelled(true);
                    ss.openGUI(player);
                }
            }
        }


    }
}
