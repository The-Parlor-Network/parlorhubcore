package dev.clxud.parlorhubcore.commands;


import dev.clxud.parlorhubcore.Plugin;
import dev.clxud.parlorhubcore.utils.ServerSelector;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GotoCommand implements CommandExecutor {

    private final Plugin plugin;
    private final ServerSelector ss;


    public GotoCommand(Plugin plugin) {
        this.plugin = plugin;
        this.ss = new ServerSelector(this.plugin);

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            if (args.length == 1) {
                ss.sendToServer((Player) sender, args[0]);
                return true;
            } else {
                sender.sendMessage("§cUsage: /goto <server>");
                return true;
            }
        }
        return false;

    }
}
