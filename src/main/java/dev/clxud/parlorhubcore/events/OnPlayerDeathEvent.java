package dev.clxud.parlorhubcore.events;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import dev.clxud.parlorhubcore.Plugin;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Collection;

public class OnPlayerDeathEvent implements Listener {

    private final Plugin plugin;

    public OnPlayerDeathEvent(Plugin plugin) {
        this.plugin = plugin;

    }

    @EventHandler
    public void onPlayerDeath(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (player.getHealth() - event.getFinalDamage() <= 0) {
                Collection<ProtectedRegion> regions = plugin.getRegionsAt(player.getLocation());
                if (regions.stream().findFirst().get().getId().equals("pvp")) {
                    event.setCancelled(true);
                    plugin.playersDeaths.put(player.getUniqueId(), plugin.playersDeaths.get(player.getUniqueId()) + 1);
                    player.teleport(new Location(player.getWorld(), 0, 59, -167, 180, 0));
                    player.getInventory().clear();
                    plugin.handlePVPInventoryInit(player);
                    player.playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_INFECT, 1, 1.3F);

                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        // teleport to region spawn
                        player.setHealth(20);
                        player.setFoodLevel(20);
                    }, 20L);

                }
            }
        }
    }
}
