package dev.clxud.parlorhubcore.commands;


import dev.clxud.parlorhubcore.Plugin;
import dev.clxud.parlorhubcore.utils.ServerSelector;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ServersCommand implements CommandExecutor {

    private final Plugin plugin;
    private final ServerSelector ss;


    public ServersCommand(Plugin plugin) {
        this.plugin = plugin;
        this.ss = new ServerSelector(this.plugin);

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            ss.openGUI((Player) sender);
            return true;
        }
        return false;
    }
}
