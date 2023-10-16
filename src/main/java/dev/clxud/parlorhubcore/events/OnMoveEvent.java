package dev.clxud.parlorhubcore.events;

import dev.clxud.parlorhubcore.Plugin;
import dev.clxud.parlorhubcore.utils.ServerSelector;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;


public class OnMoveEvent implements Listener {

    private final Plugin plugin;
    private final ServerSelector serverSelector;


    public OnMoveEvent(Plugin plugin) {

        this.plugin = plugin;
        this.serverSelector = new ServerSelector(this.plugin);
    }

    @EventHandler
    public void onMoveEvent(PlayerMoveEvent event) {
        Player player = event.getPlayer();


        // check if player enters the pvp region
        if (plugin.getRegionsAt(player.getLocation()).stream().findFirst().get().getId().equals("pvp")) {
            if (player.getGameMode() != GameMode.CREATIVE && !plugin.inPvpRegion.get(player.getUniqueId())) {
                plugin.inPvpRegion.put(player.getUniqueId(), true);
                plugin.inSpawnRegion.put(player.getUniqueId(), false);
                player.setAllowFlight(false);
                player.getInventory().clear();
                plugin.handlePVPInventoryInit(player);

                // put health meter below player name
                ScoreboardManager manager = Bukkit.getScoreboardManager();
                Scoreboard board = manager.getNewScoreboard();

                Objective objective = board.registerNewObjective("showhealth", "health");
                objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
                objective.setDisplayName(ChatColor.RED + "❤");

                player.setScoreboard(board);
                player.setHealth(player.getHealth());
            }
        }

        // check if player enters the spawn region
        if (plugin.getRegionsAt(player.getLocation()).stream().findFirst().get().getId().equals("spawn")) {
            if (player.getGameMode() != GameMode.CREATIVE && plugin.inPvpRegion.get(player.getUniqueId())) {
                plugin.inSpawnRegion.put(player.getUniqueId(), true);
                plugin.inPvpRegion.put(player.getUniqueId(), false);
                player.getInventory().clear();
                serverSelector.handleCompassInit(player);
                plugin.playersKilled.remove(player.getUniqueId());
                plugin.playersDeaths.remove(player.getUniqueId());
                plugin.playerKillstreak.remove(player.getUniqueId());
                plugin.playersKilled.put(player.getUniqueId(), 0);
                plugin.playersDeaths.put(player.getUniqueId(), 0);
                plugin.playerKillstreak.put(player.getUniqueId(), 0);

                //remove health meter below player name
                if (player.getScoreboard().getObjective(DisplaySlot.BELOW_NAME) != null) {
                    player.getScoreboard().clearSlot(DisplaySlot.BELOW_NAME);
                }


            }
        }
    }
}
