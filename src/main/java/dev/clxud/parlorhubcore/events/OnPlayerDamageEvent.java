package dev.clxud.parlorhubcore.events;

import dev.clxud.parlorhubcore.Plugin;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class OnPlayerDamageEvent implements Listener {


    private final Plugin plugin;

    public OnPlayerDamageEvent(Plugin plugin) {
        this.plugin = plugin;

    }


    @EventHandler
    public void onPlayerDamageByPlayer(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Firework) {
            event.setCancelled(true);
            return;
        }

        if (event.getEntity() instanceof Player victim && !event.getDamager().equals(event.getEntity())) {
            Player attacker;

            if (event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
                Projectile projectile = (Projectile) event.getDamager();
                attacker = (Player) projectile.getShooter();
                attacker.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7" + victim.getName() + " &7is on &c" + Math.round(victim.getHealth() * 10) / 10 + " &c❤"));
                attacker.playSound(attacker.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
            } else {
                attacker = (Player) event.getDamager();
            }


            if (victim.getHealth() - event.getFinalDamage() <= 0) {
                plugin.playersKilled.put(attacker.getUniqueId(), plugin.playersKilled.get(attacker.getUniqueId()) + 1);
                attacker.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7You killed &c" + victim.getName() + "&7!"));
                attacker.playSound(attacker, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                victim.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7You were killed by &c" + attacker.getName() + "&7!"));
                if (plugin.playerKillstreak.get(victim.getUniqueId()) >= 3) {
                    plugin.getServer().getOnlinePlayers().forEach(player -> {
                        if (plugin.getRegionsAt(player.getLocation()).stream().findFirst().get().getId().equals("pvp")) {
                            // check if players name ends with an s
                            if (victim.getName().endsWith("s")) {
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7" + victim.getName() + "' &7killstreak of &c" + plugin.playerKillstreak.get(victim.getUniqueId()) +  " &7was ended by &c" + attacker.getName() + "&7!"));
                            } else {
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7" + victim.getName() + "'s &7killstreak of &c" + plugin.playerKillstreak.get(victim.getUniqueId()) +  " &7was ended by &c" + attacker.getName() + "&7!"));
                            }
                            player.playSound(player, Sound.ENTITY_BLAZE_DEATH, 1, 0.5F);
                            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                plugin.playerKillstreak.put(victim.getUniqueId(), 0);
                            }, 20L);


                        }
                    });

                }
                if (plugin.playersKilled.get(attacker.getUniqueId()) >= 3) {
                    plugin.playerKillstreak.put(attacker.getUniqueId(), plugin.playerKillstreak.get(attacker.getUniqueId()) + 1);

                    // check if killstreak is a multiple of 5
                    if (plugin.playerKillstreak.get(attacker.getUniqueId()) >= 3 && plugin.playerKillstreak.get(attacker.getUniqueId()) % 5 == 0) {
                        // send message to all players in region
                        plugin.getServer().getOnlinePlayers().forEach(player -> {
                            if (plugin.getRegionsAt(player.getLocation()).stream().findFirst().get().getId().equals("pvp")) {
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7" + attacker.getName() + " &7is on a &c" + plugin.playerKillstreak.get(attacker.getUniqueId()) + " &7player killstreak!"));
                                player.playSound(player, Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);
                            }
                        });
                    }
                }
            }
        }
    }
}

