package org.restudios.weathercontrol;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

public class TimeManager implements CommandExecutor {

    private final WeatherControl plugin;

    public TimeManager(WeatherControl plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.no_permission")));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("weathercontrol.time.change")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.no_permission")));
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.time_usage")));
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "day":
                Bukkit.getWorlds().get(0).setTime(1000);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.day")));
                break;
            case "night":
                Bukkit.getWorlds().get(0).setTime(13000);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.night")));
                break;
            default:
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.time_usage")));
                break;
        }

        return true;
    }
}
