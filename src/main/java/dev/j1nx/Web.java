package dev.j1nx;

import dev.j1nx.commands.ResetPassword;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class Web extends JavaPlugin {

    private Connection connection;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        String host = getConfig().getString("mysql.host");
        int port = getConfig().getInt("mysql.port");
        String database = getConfig().getString("mysql.database");
        String username = getConfig().getString("mysql.username");
        String password = getConfig().getString("mysql.password");

        try {
            connection = DriverManager.getConnection(
                    "jdbc:mysql://" + host + ":" + port + "/" + database,
                    username,
                    password
            );

            Statement statement = connection.createStatement();
            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS usuarios_web (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY," +
                            "nickname VARCHAR(32) NOT NULL," +
                            "uuid VARCHAR(64) NOT NULL," +
                            "premium BOOLEAN," +
                            "password VARCHAR(64) NOT NULL" +
                            ")"
            );

            getLogger().info("✅ Conectado a MySQL y tabla creada correctamente.");

        } catch (SQLException e) {
            getLogger().severe("❌ Error conectando a la base de datos: " + e.getMessage());
            connection = null;
        }

        if (connection != null) {
            getCommand("resetpassword").setExecutor(new ResetPassword(this));
        } else {
            getLogger().severe("❌ No se registró el comando porque no hay conexión a la base de datos.");
        }
    }

    @Override
    public void onDisable() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                getLogger().info("🔌 Conexión MySQL cerrada.");
            }
        } catch (SQLException e) {
            getLogger().severe("❌ Error al cerrar conexión: " + e.getMessage());
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
