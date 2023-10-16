package dev.clxud.parlorhubcore.hooks;

import dev.clxud.parlorhubcore.Plugin;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;

public class PlaceholderHook extends PlaceholderExpansion {

    private final Plugin plugin;

    public PlaceholderHook(Plugin plugin) {
        this.plugin = plugin;
        
    }
    @Override
    public @NotNull String getIdentifier() {
        return "parlor";
    }

    @Override
    public @NotNull String getAuthor() {
        return "clxud";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if (params.equalsIgnoreCase("kills")){
            return plugin.playersKilled.get(player.getUniqueId()).toString();
        }

        if (params.equalsIgnoreCase("deaths")){
            return plugin.playersDeaths.get(player.getUniqueId()).toString();
        }

        if (params.equalsIgnoreCase("killstreak")){
            return plugin.playerKillstreak.get(player.getUniqueId()).toString();
        }

        if (params.equalsIgnoreCase("top_killstreak_name")) {
            if (plugin.playerKillstreak.size() == 0|| plugin.playerKillstreak.entrySet().stream().max((entry1, entry2) -> entry1.getValue() > entry2.getValue() ? 1 : -1).get().getValue() == 0) {
                return "None";
            }

            return plugin.getServer().getPlayer(plugin.playerKillstreak.entrySet().stream().max((entry1, entry2) -> entry1.getValue() > entry2.getValue() ? 1 : -1).get().getKey()).getName();

        }

        if (params.equalsIgnoreCase("top_killstreak_amount")) {
            // if no one has a killstreak
            if (plugin.playerKillstreak.size() == 0 || plugin.playerKillstreak.entrySet().stream().max((entry1, entry2) -> entry1.getValue() > entry2.getValue() ? 1 : -1).get().getValue() == 0) {
                return "0";
            }

            return plugin.playerKillstreak.entrySet().stream().max((entry1, entry2) -> entry1.getValue() > entry2.getValue() ? 1 : -1).get().getValue().toString();

        }

        if (params.equalsIgnoreCase("top_kills_name")) {
            if (plugin.playersKilled.size() == 0 || plugin.playersKilled.entrySet().stream().max((entry1, entry2) -> entry1.getValue() > entry2.getValue() ? 1 : -1).get().getValue() == 0) {
                return "None";
            }

            return plugin.getServer().getPlayer(plugin.playersKilled.entrySet().stream().max((entry1, entry2) -> entry1.getValue() > entry2.getValue() ? 1 : -1).get().getKey()).getName();

        }

        if (params.equalsIgnoreCase("top_kills_amount")) {
            if (plugin.playersKilled.size() == 0 || plugin.playersKilled.entrySet().stream().max((entry1, entry2) -> entry1.getValue() > entry2.getValue() ? 1 : -1).get().getValue() == 0) {
                return "0";
            }

            return plugin.playersKilled.entrySet().stream().max((entry1, entry2) -> entry1.getValue() > entry2.getValue() ? 1 : -1).get().getValue().toString();

        }

        if (params.equalsIgnoreCase("kdr")) {
            if (plugin.playersDeaths.get(player.getUniqueId()) == 0) {
                return plugin.playersKilled.get(player.getUniqueId()).toString();
            } else {
                // round to one decimal place
                double kdr = Math.round((double) plugin.playersKilled.get(player.getUniqueId()) / (double) plugin.playersDeaths.get(player.getUniqueId()) * 10.0) / 10.0;
                return String.valueOf(kdr);
            }
        }

        if (params.equalsIgnoreCase("event_type")) {
            return switch (plugin.arenaEvent.getEventType()) {
                case TOP_THE_BAR -> "0";
                case KING_OF_THE_HILL -> "1";
                case BAD_BLOOD -> "2";
            };
        }

        if (params.equalsIgnoreCase("event_type_formatted")) {
            return switch (plugin.arenaEvent.getEventType()) {
                case TOP_THE_BAR -> "Top The Bar";
                case KING_OF_THE_HILL -> "King Of The Hill";
                case BAD_BLOOD -> "Bad Blood";
            };
        }

        if (params.equalsIgnoreCase("event_duration")) {
            return plugin.arenaEvent.getFormattedDuration();
        }

        if (params.equalsIgnoreCase("event_isrunning")) {
            return plugin.arenaEvent.isRunning().toString();
        }

        if (params.equalsIgnoreCase("cooldown_gapple")) {
            if (plugin.goldenAppleCooldown.containsKey(player.getUniqueId())) {
                Duration duration = Duration.between(Instant.now(), plugin.goldenAppleCooldown.get(player.getUniqueId()));
                return duration.getSeconds() + "s";
            } else {
                return null;
            }
        }

        if (params.equalsIgnoreCase("is_cooldown_gapple")) {
            if (plugin.goldenAppleCooldown.containsKey(player.getUniqueId())) {
                return "true";
            } else {
                return "false";
            }
        }

        return null; // Placeholder is unknown by the Expansion
    }
}
