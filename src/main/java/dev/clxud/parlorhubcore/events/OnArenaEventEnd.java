package dev.clxud.parlorhubcore.events;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import dev.clxud.parlorhubcore.Plugin;
import dev.clxud.parlorhubcore.utils.ArenaEndEvent;
import dev.clxud.parlorhubcore.utils.ArenaEvent;
import dev.clxud.parlorhubcore.utils.ArenaEventEnum;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class OnArenaEventEnd implements Listener {

    private final Plugin plugin;

    public OnArenaEventEnd(Plugin plugin) {
        this.plugin = plugin;
    }

    public void spawnFireworks(Player player, int amount) {

        final int[] secondsTillEnd = {10};

        Bukkit.getScheduler().runTaskTimer(plugin, task -> {
            if (secondsTillEnd[0] < 1) {
                task.cancel();
            }

            Firework fw = (Firework) Objects.requireNonNull(player.getLocation().getWorld()).spawnEntity(player.getLocation(), EntityType.FIREWORK);
            FireworkMeta fwm = fw.getFireworkMeta();

            fwm.setPower(2);
            fwm.addEffect(FireworkEffect.builder().withColor(Color.LIME).flicker(true).build());

            fw.setFireworkMeta(fwm);
            fw.detonate();
            secondsTillEnd[0]--;

        }, 0, 20L);
    }

    public Map<Player, Integer> getWinners(ArenaEvent event) {
        Map<Player, Integer> winners = new HashMap<>();
        switch (event.getEventType()) {
            case TOP_THE_BAR -> {
                int amount = plugin.playerKillstreak.entrySet().stream().max((entry1, entry2) -> entry1.getValue() > entry2.getValue() ? 1 : -1).get().getValue();
                UUID winner = plugin.playerKillstreak.entrySet().stream().max((entry1, entry2) -> entry1.getValue() > entry2.getValue() ? 1 : -1).get().getKey();
                winners.put(Bukkit.getPlayer(winner), amount);
            }

            case BAD_BLOOD -> {
                // sort playersKilled by top value
                Map<UUID, Integer> sortedPlayersKilled = new HashMap<>();
                plugin.playersKilled.entrySet().stream().sorted(Map.Entry.<UUID, Integer>comparingByValue().reversed()).forEachOrdered(x -> sortedPlayersKilled.put(x.getKey(), x.getValue()));
                // check if we have three or more players otherwise return the remaining players
                if (sortedPlayersKilled.size() >= 3) {
                    // get the top three players
                    Map.Entry<UUID, Integer> first = sortedPlayersKilled.entrySet().stream().findFirst().get();
                    Map.Entry<UUID, Integer> second = sortedPlayersKilled.entrySet().stream().skip(1).findFirst().get();
                    Map.Entry<UUID, Integer> third = sortedPlayersKilled.entrySet().stream().skip(2).findFirst().get();

                    // add the top three players to the winners list
                    winners.put(Bukkit.getPlayer(first.getKey()), first.getValue());
                    winners.put(Bukkit.getPlayer(second.getKey()), second.getValue());
                    winners.put(Bukkit.getPlayer(third.getKey()), third.getValue());
                } else {
                    // add the remaining players to the winners list
                    for (Map.Entry<UUID, Integer> entry : sortedPlayersKilled.entrySet()) {
                        winners.put(Bukkit.getPlayer(entry.getKey()), entry.getValue());
                    }
                }
            }
        }

        return winners;
    }

    @EventHandler
    public void onArenaEventEnd(ArenaEndEvent event) {
        Map<Player, Integer> winners = getWinners(event.getEvent());
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get(BukkitAdapter.adapt(Bukkit.getWorlds().get(0)));
        ProtectedRegion region = regions.getRegion("pvp");
        region.setFlag(Flags.PVP, StateFlag.State.DENY);
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (plugin.getRegionsAt(player.getLocation()).stream().findFirst().get().getId().equals("pvp")) {
                player.sendMessage(ChatColor.GREEN + "Event has ended!");
                player.playSound(player, Sound.ENTITY_ENDER_DRAGON_DEATH, 0.5F, 1);



                plugin.playersKilled.put(player.getUniqueId(), 0);
                plugin.playersDeaths.put(player.getUniqueId(), 0);
                plugin.playerKillstreak.put(player.getUniqueId(), 0);

                if (!winners.containsKey(player)) {
                    player.setGameMode(GameMode.SPECTATOR);
                }

                String winnerString = "";

                if (winners.size() < 2) {
                    winnerString = "Winner:";
                } else {
                    winnerString = "Winners:";
                }

                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&l" + winnerString));
                winners.forEach((winner, amount) -> {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6" + winner.getName() + " &7- &6" + amount));
                });
            }
        }

        winners.forEach((winner, amount) -> {
            spawnFireworks(winner, 10);
            winner.sendMessage(ChatColor.GREEN + "You won the event!");
            winner.playSound(winner.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 1);
        });



        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (plugin.getRegionsAt(player.getLocation()).stream().findFirst().get().getId().equals("pvp")) {
                    player.setGameMode(GameMode.ADVENTURE);
                    player.teleport(new Location(player.getWorld(), 0, 59, -167, 180, 0));
                    player.getInventory().clear();
                    plugin.handlePVPInventoryInit(player);

                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        // teleport to region spawn
                        player.setHealth(20);
                        player.setFoodLevel(20);
                    }, 20L);
                }

            }

        }, 320L);
        region.setFlag(Flags.PVP, StateFlag.State.ALLOW);
    }


}
