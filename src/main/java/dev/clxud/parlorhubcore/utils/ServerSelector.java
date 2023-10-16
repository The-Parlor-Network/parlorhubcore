package dev.clxud.parlorhubcore.utils;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import dev.clxud.parlorhubcore.Plugin;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

public class ServerSelector {

    private Plugin plugin;

    private String survivalTitle = "&b&lSurvival";
    private String survivalDescription = "&6The classic Minecraft survival experience, revived.";
    private String skyblockTitle = "&a&lSkyblock";
    private String skyblockDescription = "&6One island, one tree, one mission. Can you survive?";

    private String miniGamesTitle = "&9&lMinigames";
    private String miniGamesDescription = "&6The finest selection of minigames, curated by our staff team.";

    public Material[] allowedItems = {
            Material.BLAZE_POWDER,
            Material.DIAMOND_PICKAXE,
            Material.OAK_SAPLING,
            Material.LIGHT_GRAY_STAINED_GLASS_PANE,
            Material.COMPASS
    };

    public ServerSelector(Plugin plugin) {
        this.plugin = plugin;
    }


    public String getPlayersOnlineLore(String server) {
        String fmtPlayerCountLore;
        switch (server) {
            case "survival":
                fmtPlayerCountLore = "&7%bungee_survival% &7players online";
                break;
            case "skyblock":
                fmtPlayerCountLore = "&7%bungee_skyblock% &7players online";
                break;
            case "minigames":
                fmtPlayerCountLore = "&7%bungee_minigames% &7players online";
                break;
            default:
                fmtPlayerCountLore = "&70 &7players online";
        }

        fmtPlayerCountLore = PlaceholderAPI.setPlaceholders(null, fmtPlayerCountLore);
        return ChatColor.translateAlternateColorCodes('&', fmtPlayerCountLore);
    }


    public void sendToServer(Player player, String server) {

        switch (server) {
            case "survival":
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Connecting to &bSurvival&7..."));
                break;

            case "skyblock":
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Connecting to &aSkyblock&7..."));
                break;

            case "minigames":
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Connecting to &9Minigames&7..."));
                break;
        }

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(server);

        player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());

    }


    public void setInventoryItem(Inventory inv, int slot, Material material, String displayName, String description, String playing, Boolean glow) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
        if (glow) {
            itemMeta.addEnchant(Enchantment.KNOCKBACK, 1, true);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        if (description != null) {
            List<String> loreList = new ArrayList<String>();
            // wrap the description if it's too long
            if (description.length() > 30) {
                String[] descriptionArray = description.split(" ");
                StringBuilder currentLine = new StringBuilder();
                for (String word : descriptionArray) {
                    if (currentLine.length() + word.length() > 30) {
                        loreList.add(ChatColor.translateAlternateColorCodes('&', "&6" + currentLine));
                        currentLine = new StringBuilder();
                    }
                    currentLine.append(word).append(" ");
                }
                loreList.add(ChatColor.translateAlternateColorCodes('&', "&6" + currentLine));
            } else {
                loreList.add(ChatColor.translateAlternateColorCodes('&', "&6" + description));
            }
            loreList.add("\n");
            // add player count using placeholder api
            loreList.add(ChatColor.translateAlternateColorCodes('&', "&aClick to join!"));
            if (playing != null) {
                loreList.add(ChatColor.translateAlternateColorCodes('&', playing));
            }

            itemMeta.setLore(loreList);
        }

        item.setItemMeta(itemMeta);
        inv.setItem(slot, item);
    }

    public void setInventoryItem(Inventory inv, int slot, Material material, String displayName, Boolean glow) {
        setInventoryItem(inv, slot, material, displayName, null, null, glow);
    }



    public void openGUI(Player player) {
        Inventory serverSelectorView = player.getServer().createInventory(null, 27, "Server Selector");
        for (int size = 0; size < serverSelectorView.getSize(); size++) {
            setInventoryItem(serverSelectorView, size, Material.LIGHT_GRAY_STAINED_GLASS_PANE, "&r", false);
        }

        setInventoryItem(serverSelectorView, 11, Material.DIAMOND_PICKAXE, survivalTitle, survivalDescription, getPlayersOnlineLore("survival"), true);
        setInventoryItem(serverSelectorView, 13, Material.OAK_SAPLING, skyblockTitle, skyblockDescription, getPlayersOnlineLore("skyblock"), true);
        setInventoryItem(serverSelectorView, 15, Material.BLAZE_POWDER, miniGamesTitle, miniGamesDescription,  getPlayersOnlineLore("minigames"), true);
        player.openInventory(serverSelectorView);
    }

    public void handleInventoryClick(Player player, ItemStack item) {

        switch (item.getType()) {
            case DIAMOND_PICKAXE:
                this.sendToServer(player, "survival");
                break;

            case OAK_SAPLING:
                this.sendToServer(player, "skyblock");
                break;

            case BLAZE_POWDER:
                this.sendToServer(player, "minigames");
                break;

        }
    }

    public void handleCompassInit(Player player) {
        player.getInventory().clear();
        this.setInventoryItem(player.getInventory(), 4, Material.COMPASS, "&bServer Selector", true);
    }
}
