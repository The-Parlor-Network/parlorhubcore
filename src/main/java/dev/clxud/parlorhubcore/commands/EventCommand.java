package dev.clxud.parlorhubcore.commands;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import dev.clxud.parlorhubcore.Plugin;
import dev.clxud.parlorhubcore.utils.ArenaEventEnum;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicInteger;

public class EventCommand implements CommandExecutor {

    private final Plugin plugin;

    public EventCommand(Plugin plugin) {
        this.plugin = plugin;

    }

    public ChatColor getColorBasedOnTime(int time) {
        if (time <= 10 && time > 3) {
            return ChatColor.RED;
        } else if (time <= 3 && time > 1) {
            return ChatColor.YELLOW;
        } else {
            return ChatColor.GREEN;
        }
    }

    public void handleEventInit(ArenaEventEnum eventType, String amountOfMinutes, Player eventStarter) {


        if (StringUtils.isNumeric(amountOfMinutes)) {

            int minutes = Integer.parseInt(amountOfMinutes);
            if (minutes > 120) {
                eventStarter.sendMessage(ChatColor.RED + "The maximum duration is 120 minutes!");
                return;
            } else if (minutes < 1) {
                eventStarter.sendMessage(ChatColor.RED + "The minimum duration is 1 minute!");
                return;
            }

            eventStarter.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Now starting &6" + eventType.getFormattedName() + " &7event for &6" + minutes + " &7minute(s)... " ));

            plugin.arenaEvent.setStarting(true);
            final int[] secondsTillStart = {10};

            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionManager regions = container.get(BukkitAdapter.adapt(eventStarter.getWorld()));
            ProtectedRegion region = regions.getRegion("pvp");
            region.setFlag(Flags.PVP, StateFlag.State.DENY);

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (plugin.getRegionsAt(player.getLocation()).stream().findFirst().get().getId().equals("pvp")) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Event starting soon, PvP is &cdisabled&7!"));
                    player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                }
            }

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                Bukkit.getScheduler().runTaskTimer(plugin, task -> {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (plugin.getRegionsAt(player.getLocation()).stream().findFirst().get().getId().equals("pvp")) {
                            if (secondsTillStart[0] == 0) {
                                plugin.arenaEvent.start(eventType, minutes);
                                region.setFlag(Flags.PVP, StateFlag.State.ALLOW);
                                task.cancel();
                            } else if (secondsTillStart[0] == 10) {
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Event starting in " + this.getColorBasedOnTime(secondsTillStart[0]) + secondsTillStart[0] + " &7second(s)..."));
                                player.sendTitle(this.getColorBasedOnTime(secondsTillStart[0]) + String.valueOf(secondsTillStart[0]), "", 0, 20, 0);
                                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1, 1);
                            } else if (secondsTillStart[0] <= 5) {
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Event starting in " + this.getColorBasedOnTime(secondsTillStart[0]) + secondsTillStart[0] + " &7second(s)..."));
                                player.sendTitle(this.getColorBasedOnTime(secondsTillStart[0]) + String.valueOf(secondsTillStart[0]), "", 0, 20, 0);
                                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1, 1);
                            }
                        }
                    }
                    secondsTillStart[0]--;
                }, 0, 20L);
            }, 200L);
        } else {
            eventStarter.sendMessage(ChatColor.RED + "Invalid number!");
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (commandSender instanceof Player) {
            if (args.length == 0) {
                return false;
            }

            switch (args[0]) {
                case "start" -> {
                    if (args.length == 1) {
                        return false;
                    }

                    if (plugin.arenaEvent.isRunning()) {
                        commandSender.sendMessage(ChatColor.RED + "An event is already running!");
                        return true;
                    }

                    if (plugin.arenaEvent.isStarting()) {
                        commandSender.sendMessage(ChatColor.RED + "An event is already starting!");
                        return true;
                    }

                    switch (args[1]) {
                        case "TopTheBar" -> {
                            if (args.length == 2) {
                                return false;
                            }

                            this.handleEventInit(ArenaEventEnum.TOP_THE_BAR, args[2], (Player) commandSender);
                            return true;

                        }
                        case "KingOfTheHill" -> {
                            commandSender.sendMessage(ChatColor.RED + "This event is not implemented yet.");
                            return true;
                        }

                        case "BadBlood" -> {
                            if (args.length == 2) {
                                return false;
                            }

                            this.handleEventInit(ArenaEventEnum.BAD_BLOOD, args[2], (Player) commandSender);
                            return true;
                        }

                        default -> {
                            commandSender.sendMessage(ChatColor.RED + "Invalid event!");
                            return true;
                        }
                    }

                }
                case "stop" -> {
                    if (plugin.arenaEvent.isRunning()) {
                        plugin.arenaEvent.stop();
                        commandSender.sendMessage(ChatColor.GREEN + "Event stopped!");
                    } else {
                        commandSender.sendMessage(ChatColor.RED + "No event is running!");
                    }
                    return true;
                }
            }

        }
        return false;
    }
}
