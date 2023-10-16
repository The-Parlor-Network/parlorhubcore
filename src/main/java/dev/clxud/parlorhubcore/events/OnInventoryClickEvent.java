package dev.clxud.parlorhubcore.events;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import dev.clxud.parlorhubcore.Plugin;
import dev.clxud.parlorhubcore.utils.ServerSelector;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class OnInventoryClickEvent implements Listener {

    private Plugin plugin;
    private final ServerSelector ss;

    public OnInventoryClickEvent(Plugin plugin) {

        this.plugin = plugin;
        this.ss = new ServerSelector(this.plugin);
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();

        if (!player.getGameMode().equals(GameMode.CREATIVE)) {
            if (item != null && Arrays.stream(ss.allowedItems).anyMatch(material -> material == item.getType())) {
                event.setCancelled(true);
                ss.handleInventoryClick(player, item);
            }
        }
    }
}
