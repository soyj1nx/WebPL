package dev.j1nx.commands;

import dev.j1nx.Web;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

public class ResetPassword implements CommandExecutor {

    private final Web plugin;

    public ResetPassword(Web plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Este comando solo puede ser usado por jugadores.");
            return true;
        }

        Player player = (Player) sender;
        String nick = player.getName();
        String uuid = player.getUniqueId().toString();

        boolean isPremium = Bukkit.getOnlineMode();

        String password = generarPassword();

        try {
            Connection connection = plugin.getConnection();
            PreparedStatement stmt = connection.prepareStatement(
                    "REPLACE INTO usuarios_web (nickname, uuid, premium, password) VALUES (?, ?, ?, ?)"
            );
            stmt.setString(1, nick);
            stmt.setString(2, uuid);
            stmt.setBoolean(3, isPremium);
            stmt.setString(4, password);
            stmt.executeUpdate();

            player.sendMessage("§aTu contraseña web ha sido reiniciada.");
            player.sendMessage("§eContraseña temporal: §f" + password);

        } catch (SQLException e) {
            player.sendMessage("§cError al guardar la nueva contraseña.");
            plugin.getLogger().severe("❌ Error en /resetpassword: " + e.getMessage());
        }

        return true;
    }

    private String generarPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 10; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }

        return sb.toString();
    }
}