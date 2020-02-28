package us.cameron.xraysuspicion;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class Events implements Listener {
    Main plugin;
    public Events(Main instance) {
        this.plugin = instance;
    }
    private HashMap<String, Event> x = new HashMap<String, Event>();


    @EventHandler
    public void onBlockBreak (BlockBreakEvent event) {
        if (event.getPlayer().getInventory().getItemInMainHand().getType() == Material.DIAMOND_HOE && event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            event.setCancelled(true);
            new BukkitRunnable () {
                @Override
                public void run() {
                    try {
                        PreparedStatement statement = plugin.getConnection().prepareStatement("SELECT * FROM block_logs WHERE x = ? AND y = ? AND z = ? ORDER BY -id LIMIT 1");
                        statement.setInt(1, event.getBlock().getLocation().getBlockX());
                        statement.setInt(2, event.getBlock().getLocation().getBlockY());
                        statement.setInt(3, event.getBlock().getLocation().getBlockZ());
                        ResultSet rs = statement.executeQuery();
                        if (!rs.next()){
                            event.getPlayer().sendMessage("No results found.");
                        } else {
                            event.getPlayer().sendMessage(ChatColor.GREEN + "Block broken by last by: " + rs.getString(2));
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }.runTaskAsynchronously(plugin);
        }
        Material block = event.getBlock().getType();
        ArrayList<Material> blocks = new ArrayList<>();
        blocks.add(Material.DIAMOND_ORE);
        blocks.add(Material.GOLD_ORE);

        if(blocks.contains(block)) {
            if (x.containsKey(event.getPlayer().getDisplayName())) {
                Event t = x.get(event.getPlayer().getDisplayName());
                t.addDiamond();
                long fiveAgo = System.currentTimeMillis() - 5 * 60 * 1000;
                if (t.getFirstDiamondMined() > fiveAgo && t.getDiamondsMined() > 20) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            try {
                                PreparedStatement statement = plugin.getConnection().prepareStatement("INSERT INTO logs (playerName, x, y, z) VALUES (?,?,?,?)");
                                statement.setString(1, event.getPlayer().getDisplayName());
                                statement.setInt(2, event.getBlock().getLocation().getBlockX());
                                statement.setInt(3, event.getBlock().getLocation().getBlockY());
                                statement.setInt(4, event.getBlock().getLocation().getBlockZ());
                                statement.executeUpdate();

                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }

                    }.runTaskAsynchronously(plugin);
                } else if (t.getFirstDiamondMined() < fiveAgo) {
                    t.reset();
                    t.addDiamond();
                    t.setFirstDiamondMined(System.currentTimeMillis());
                }
            } else {
                x.put(event.getPlayer().getDisplayName(), new Event(event.getPlayer(), System.currentTimeMillis()));
                Event t = x.get(event.getPlayer().getDisplayName());
                t.addDiamond();
            }
        }

        if (event.getPlayer().getInventory().getItemInMainHand().getType() != Material.DIAMOND_HOE) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    try {
                        PreparedStatement statement = plugin.getConnection().prepareStatement("INSERT INTO block_logs (playerName, x, y, z) VALUES (?,?,?,?)");
                        statement.setString(1, event.getPlayer().getDisplayName());
                        statement.setInt(2, event.getBlock().getLocation().getBlockX());
                        statement.setInt(3, event.getBlock().getLocation().getBlockY());
                        statement.setInt(4, event.getBlock().getLocation().getBlockZ());
                        statement.executeUpdate();

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

            }.runTaskAsynchronously(plugin);
        }

    }

    public double sleeping = 0;
    @EventHandler
    public void onSleep(PlayerBedEnterEvent event) {
        if (event.getPlayer().getWorld().getTime() >= 11834 && event.getPlayer().getWorld().getTime() <= 22200) {
            sleeping++;
            if (sleeping / Bukkit.getOnlinePlayers().size() >= 0.5) {
                event.getPlayer().sendMessage(ChatColor.GOLD + "sleeping...");
                plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                    event.getPlayer().getWorld().setTime(23000);
                    event.getPlayer().sendMessage(ChatColor.GOLD + "Rise and shine!");
                    event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder("Hey.. wake up!").color(ChatColor.AQUA).create());
                    sleeping = 0;
                }, 20 * 5);
            }
        }
    }

    @EventHandler
    public void onPlayerGettingOutOfBed(PlayerBedLeaveEvent e) {
        if (sleeping != 0) { sleeping--; }
    }


}
