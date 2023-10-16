package dev.clxud.parlorhubcore.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EventCmdTabCompleter implements TabCompleter {
    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (strings.length == 1) {
            return List.of("start", "stop");
        } else if (strings.length == 2) {
            return List.of("TopTheBar", "BadBlood");
        } else if (strings.length == 3) {
            return List.of("5", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55", "60", "120");
        } else {
            return null;
        }
    }
}
