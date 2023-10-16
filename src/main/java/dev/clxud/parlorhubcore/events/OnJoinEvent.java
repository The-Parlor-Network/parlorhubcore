package dev.clxud.parlorhubcore.events;


import dev.clxud.parlorhubcore.Plugin;
import dev.clxud.parlorhubcore.utils.ServerSelector;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class OnJoinEvent implements Listener {

    private final Plugin plugin;
    private final ServerSelector ss;

    public OnJoinEvent(Plugin plugin) {
        this.plugin = plugin;
        this.ss = new ServerSelector(this.plugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }
        ss.handleCompassInit(player);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);

        // send alpha warning
        if (plugin.isAlpha) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&l⚠ WARNING ⚠"));
                player.sendMessage(" ");
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThis server is in &c&lALPHA."));
                player.sendMessage(" ");
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cBugs/glitches &c&owill &cbe expected."));
                player.sendMessage(" ");
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cPlease report all bugs/glitches on our Discord."));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&r"));
            }, 20L);
        }

        plugin.inSpawnRegion.put(player.getUniqueId(), true);
        plugin.inPvpRegion.put(player.getUniqueId(), false);
        plugin.playersKilled.put(player.getUniqueId(), 0);
        plugin.playersDeaths.put(player.getUniqueId(), 0);
        plugin.playerKillstreak.put(player.getUniqueId(), 0);
    }
}
