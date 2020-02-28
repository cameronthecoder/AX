package us.cameron.xraysuspicion;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.*;

public class Main extends JavaPlugin {
    private Connection connection;
    private String host, database, username, password;
    private int port;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new Events(this), this);
        getCommand("ax").setExecutor(new Commands(this));
        mysqlSetup();
    }

    @Override
    public void onDisable() {
        getServer().getConsoleSender().sendMessage("");

    }

    public void mysqlSetup() {
        host = "localhost";
        port = 3306;
        database = "mcserver";
        username = "cameron";
        password = "mydogisapoodle";
        try {
            synchronized (this) {
                if (getConnection() != null && !getConnection().isClosed()) {
                    return;
                }

                Class.forName("com.mysql.jdbc.Driver");
                setConnection(DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database + "?useSSL=false&allowPublicKeyRetrieval=true&autoReconnect=true", this.username, this.password));
                this.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "MySQL Connected! Database: " + this.database);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection (Connection connection) {
        this.connection = connection;
    }

}
