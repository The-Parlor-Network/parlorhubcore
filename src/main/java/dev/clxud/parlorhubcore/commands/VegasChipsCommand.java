package dev.clxud.parlorhubcore.commands;

import dev.clxud.parlorhubcore.Plugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class VegasChipsCommand implements CommandExecutor {

    private final Plugin plugin;

    public VegasChipsCommand(Plugin plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            if (args.length == 0) {
                return false;
            }

            // get player and amount from args
            if (args.length == 2) {
                Player player = plugin.getServer().getPlayer(args[0]);
                int amount = Integer.parseInt(args[1]);
                if (player != null) {
                    int balance =  Math.toIntExact((long) plugin.econ.getBalance(player));
                    if (balance < amount) {
                        player.sendMessage("§cYou do not have enough money to do this!");
                        return true;
                    }
                    switch (amount) {
                        case 100 -> {
                            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "vegas give " + player.getName() + " 100 1");
                            plugin.econ.withdrawPlayer(player, 100);
                        }
                        case 1000 -> {
                            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "vegas give " + player.getName() + " 1000 1");
                            plugin.econ.withdrawPlayer(player, 1000);
                        }
                        case 10000 -> {
                            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "vegas give " + player.getName() + " 10000 1");
                            plugin.econ.withdrawPlayer(player, 10000);
                        }
                    }
                } else {
                    sender.sendMessage("§cPlayer not found!");
                }
                return true;
            }





        }
        return false;

    }
}
