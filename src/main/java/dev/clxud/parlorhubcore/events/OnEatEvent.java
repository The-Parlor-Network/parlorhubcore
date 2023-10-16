package dev.clxud.parlorhubcore.events;

import dev.clxud.parlorhubcore.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;

import java.time.Instant;
import java.util.HashMap;

public class OnEatEvent implements Listener {


    private Plugin plugin;

    public OnEatEvent(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEatEvent(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        if (event.getItem().getType() == Material.GOLDEN_APPLE) {
            if (plugin.goldenAppleCooldown.containsKey(player.getUniqueId())) {
                event.setCancelled(true);
                player.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 0.5F);
                player.sendMessage("§cYou cannot eat a golden apple for another §c" + (plugin.goldenAppleCooldown.get(player.getUniqueId()).getEpochSecond() - Instant.now().getEpochSecond()) + " §cseconds!");
            } else {
                plugin.goldenAppleCooldown.put(player.getUniqueId(), Instant.now().plusSeconds(5));
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    plugin.goldenAppleCooldown.remove(player.getUniqueId());
                }, 80L);
            }
        }


    }

}
