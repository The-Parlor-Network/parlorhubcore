package dev.clxud.parlorhubcore.events;


import dev.clxud.parlorhubcore.Plugin;
import dev.clxud.parlorhubcore.utils.ServerSelector;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

import java.util.Arrays;

public class OnItemDropEvent implements Listener {

    private Plugin plugin;
    private final ServerSelector ss;

    public OnItemDropEvent(Plugin plugin) {

        this.plugin = plugin;
        this.ss = new ServerSelector(this.plugin);
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        if (Arrays.asList(ss.allowedItems).contains(event.getItemDrop().getItemStack().getType())) {
            event.setCancelled(true);
        }

    }
}
