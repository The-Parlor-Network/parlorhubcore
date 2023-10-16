package dev.clxud.parlorhubcore.events;

import dev.clxud.parlorhubcore.Plugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class OnLeaveEvent implements Listener {

    private final Plugin plugin;

    public OnLeaveEvent(Plugin plugin) {
        this.plugin = plugin;

    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        plugin.inventoryMap.remove(player.getUniqueId());
        plugin.playersKilled.put(player.getUniqueId(), 0);
        plugin.playersDeaths.put(player.getUniqueId(), 0);
        plugin.playerKillstreak.put(player.getUniqueId(), 0);
    }


}
