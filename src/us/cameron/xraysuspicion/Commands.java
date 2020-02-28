package us.cameron.xraysuspicion;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Commands implements CommandExecutor {
    Main plugin;
    public Commands(Main instance) {
        plugin = instance;
    }

    public boolean onCommand(CommandSender cs, Command cms, String lbl, String[] args) {
        if(lbl.equalsIgnoreCase("ax")) {
            if(args[0].equalsIgnoreCase("clearlogs")) {
                if (cs instanceof Player) {
                    Player player = (Player) cs;
                    if (player.isOp()) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                try {
                                    Statement statement = plugin.getConnection().createStatement();
                                    statement.executeUpdate("DELETE FROM logs");
                                    player.sendMessage(ChatColor.GREEN + "Successfully deleted all logs.");

                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            }

                        }.runTaskAsynchronously(plugin);
                    } else {
                        cs.sendMessage(ChatColor.RED + "You do not have permission to execute this command!");
                    }
                } else {
                    cs.sendMessage(ChatColor.RED + "You must be a player to execute this command!");
                }
            }else if (args[0].equalsIgnoreCase("view")) {
                if (cs instanceof Player) {
                    Player player = (Player) cs;
                    new BukkitRunnable () {
                        @Override
                        public void run() {
                            try {
                                Statement statement = plugin.getConnection().createStatement();
                                ResultSet result = statement.executeQuery("SELECT * FROM logs");
                                List<String> logs = new ArrayList<String>();

                                while (result.next()) {
                                    String name = result.getString("playerName");
                                    logs.add(name);
                                }

                                player.sendMessage("Logs: ");
                                for (String log : logs) {
                                    player.sendMessage(log);
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    }.runTaskAsynchronously(plugin);
                } else {
                    cs.sendMessage(ChatColor.RED + "You must be a player to execute this command!");
                }
            } else if (args[0].equalsIgnoreCase("players")) {
                ArrayList<String> players = new ArrayList<String>();
                String totalOnline = Integer.toString(plugin.getServer().getMaxPlayers());
                String online = Integer.toString(plugin.getServer().getOnlinePlayers().size());
                for (Player player :Bukkit.getOnlinePlayers()) {
                    players.add(player.getDisplayName() + ", ");
                }
                cs.sendMessage(ChatColor.GREEN + "Online Players (" + online + "/" + totalOnline + "): "  + players.toString());
            } else {
                cs.sendMessage("That sub-command does not exist.");
                return true;
            }
        }
        return true;
    }
}
