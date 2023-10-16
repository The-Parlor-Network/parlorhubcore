package dev.clxud.parlorhubcore.events;

import dev.clxud.parlorhubcore.Plugin;
import dev.clxud.parlorhubcore.utils.ArenaEventEnum;
import dev.clxud.parlorhubcore.utils.ArenaInitEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class OnArenaEventStart implements Listener {

    private final Plugin plugin;

    public OnArenaEventStart(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onArenaEventStart(ArenaInitEvent event) {
        String eventTitle = event.getEvent().getEventType().getFormattedName();

        String eventDesc = switch (event.getEvent().getEventType()) {
            case TOP_THE_BAR -> "The player with the highest killstreak wins!";
            case KING_OF_THE_HILL -> "The player who stays on the hill the longest wins!";
            case BAD_BLOOD -> "The top 3 players with the most kills wins!";
        };

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (plugin.getRegionsAt(player.getLocation()).stream().findFirst().get().getId().equals("pvp")) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7&m----------------------------------"));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&l" + eventTitle));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7" + eventDesc));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&r"));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aEvent starts now! Good luck!"));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Event duration: &c" + event.getEvent().getDuration().toMinutes() + " &7minutes"));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7&m----------------------------------"));
                player.playSound(player, Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);

            }
        }

    }
}
