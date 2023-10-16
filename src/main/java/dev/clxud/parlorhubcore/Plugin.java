package dev.clxud.parlorhubcore;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.GlobalProtectedRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import dev.clxud.parlorhubcore.hooks.PlaceholderHook;
import dev.clxud.parlorhubcore.utils.ArenaEvent;
import dev.clxud.parlorhubcore.utils.InventorySerializer;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import dev.clxud.parlorhubcore.events.*;
import dev.clxud.parlorhubcore.commands.*;

import java.io.IOException;
import java.time.Instant;
import java.util.*;

public final class Plugin extends JavaPlugin {

    public HashMap<UUID, String> inventoryMap = new HashMap<>();
    public InventorySerializer invSerializer = new InventorySerializer();

    public final HashMap<UUID, Boolean> inPvpRegion = new HashMap<>();
    public final HashMap<UUID, Boolean> inSpawnRegion = new HashMap<>();

    public final HashMap<UUID, Integer> playersKilled = new HashMap<>();

    public final HashMap<UUID, Integer> playersDeaths = new HashMap<>();

    public final HashMap<UUID, Integer> playerKillstreak = new HashMap<>();

    public final HashMap<UUID, Instant> goldenAppleCooldown = new HashMap<>();


    public Economy econ;

    public Boolean isAlpha = false;

    public ArenaEvent arenaEvent = new ArenaEvent();

    public void serializeInv(UUID uuid, PlayerInventory inventory) {
        String serialized = invSerializer.playerInventoryToBase64(inventory);
        inventoryMap.put(uuid, serialized);
    }

    public void deserializeInv(UUID uuid, PlayerInventory inventory) throws IOException {
        String serialized = inventoryMap.get(uuid);
        inventoryMap.remove(uuid);
        inventoryMap.put(uuid, serialized);
        ItemStack[] items = invSerializer.itemStackArrayFromBase64(serialized);
        inventory.setContents(items);
    }

    public Collection<ProtectedRegion> getRegionsAt(Location location) {
        List<ProtectedRegion> regions = new ArrayList<>();

        World world = BukkitAdapter.adapt(Objects.requireNonNull(location.getWorld()));
        if (world == null) {
            return regions;
        }

        RegionManager rm = WorldGuard.getInstance().getPlatform().getRegionContainer().get(world);
        if (rm == null) {
            return regions;
        }

        for (ProtectedRegion region : rm.getApplicableRegions(BukkitAdapter.asBlockVector(location))) {
            regions.add(region);
        }

        return regions;
    }

    public void handlePVPInventoryInit(Player player) {
        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        sword.addEnchantment(org.bukkit.enchantments.Enchantment.DAMAGE_ALL, 1);
        sword.addEnchantment(Enchantment.SWEEPING_EDGE, 1);
        ItemMeta swordItemMeta = sword.getItemMeta();
        swordItemMeta.setUnbreakable(true);
        sword.setItemMeta(swordItemMeta);
        player.getInventory().addItem(sword);
        // give player bow with infinity enchantment
        ItemStack bow = new ItemStack(Material.BOW);
        bow.addEnchantment(org.bukkit.enchantments.Enchantment.ARROW_INFINITE, 1);
        // make it unbreakable
        ItemMeta bowItemMeta = bow.getItemMeta();
        bowItemMeta.setUnbreakable(true);
        bow.setItemMeta(bowItemMeta);
        player.getInventory().addItem(bow);
        player.getInventory().addItem(new ItemStack(Material.ARROW, 1));
        player.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE, 64));

        //equip player with armor
        player.getInventory().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
        player.getInventory().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
        player.getInventory().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
        player.getInventory().setBoots(new ItemStack(Material.DIAMOND_BOOTS));
    }


    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return true;
    }

    @Override
    public void onEnable() {

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            getLogger().severe("Could not find PlaceholderAPI! Disabling plugin...");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        if (Bukkit.getPluginManager().getPlugin("WorldGuard") == null) {
            getLogger().severe("Could not find WorldGuard! Disabling plugin...");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }


        new PlaceholderHook(this).register();

        WorldGuard worldGuard = WorldGuard.getInstance();
        RegionManager regions = worldGuard.getPlatform().getRegionContainer().get(BukkitAdapter.adapt(getServer().getWorlds().get(0)));
        GlobalProtectedRegion globalRegion = new GlobalProtectedRegion("global");
        regions.addRegion(globalRegion);

        if (!setupEconomy() ) {
            getLogger().severe("Could not find Vault! Disabling plugin...");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        getServer().getPluginManager().registerEvents(new OnInventoryClickEvent(this), this);
        getServer().getPluginManager().registerEvents(new OnInventoryInteractEvent(), this);
        getServer().getPluginManager().registerEvents(new OnItemInteractEvent(this), this);
        getServer().getPluginManager().registerEvents(new OnJoinEvent(this), this);
        getServer().getPluginManager().registerEvents(new OnGamemodeChange(this), this);
        getServer().getPluginManager().registerEvents(new OnLeaveEvent(this), this);
        getServer().getPluginManager().registerEvents(new OnItemDropEvent(this), this);
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        getServer().getPluginManager().registerEvents(new OnMoveEvent(this), this);
        getServer().getPluginManager().registerEvents(new OnPlayerDeathEvent(this), this);
        getServer().getPluginManager().registerEvents(new OnPlayerDamageEvent(this), this);
        getServer().getPluginManager().registerEvents(new OnArenaEventStart(this), this);
        getServer().getPluginManager().registerEvents(new OnArenaEventEnd(this), this);
        getServer().getPluginManager().registerEvents(new OnEatEvent(this), this);
        getCommand("servers").setExecutor(new ServersCommand(this));
        getCommand("goto").setExecutor(new GotoCommand(this));
        getCommand("goto").setTabCompleter(new GotoTabCompleter());
        getCommand("vegaschips").setExecutor(new VegasChipsCommand(this));
        getCommand("event").setExecutor(new EventCommand(this));
        getCommand("event").setTabCompleter(new EventCmdTabCompleter());

        getLogger().info("Parlor Hub Core has been enabled!");

        for (Player player : getServer().getOnlinePlayers()) {
            this.inSpawnRegion.put(player.getUniqueId(), true);
            this.inPvpRegion.put(player.getUniqueId(), false);
        }

    }

    @Override
    public void onDisable() {
        getLogger().info("Parlor Hub Core has been disabled!");
    }
}
