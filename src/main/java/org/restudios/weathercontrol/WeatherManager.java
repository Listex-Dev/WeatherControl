package org.restudios.weathercontrol;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

public class WeatherManager implements CommandExecutor {

    private final WeatherControl plugin;

    public WeatherManager(WeatherControl plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.no_permission")));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("weathercontrol.weather.change")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.no_permission")));
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.weather_usage")));
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "sunny":
                Bukkit.getWorlds().get(0).setStorm(false);
                Bukkit.getWorlds().get(0).setThundering(false);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.sunny")));
                break;
            case "rain":
                Bukkit.getWorlds().get(0).setStorm(true);
                Bukkit.getWorlds().get(0).setThundering(false);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.rain")));
                break;
            case "thunder":
                Bukkit.getWorlds().get(0).setStorm(true);
                Bukkit.getWorlds().get(0).setThundering(true);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.thunder")));
                break;
            default:
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.weather_usage")));
                break;
        }

        return true;
    }

    public void startAutoWeatherCycle() {
        long interval = plugin.getConfig().getLong("weather.auto_change_interval");
        Bukkit.getScheduler().runTaskTimer(plugin, this::randomizeWeather, 0L, interval * 20L);
    }

    private void randomizeWeather() {
        int random = (int) (Math.random() * 3);
        switch (random) {
            case 1:
                Bukkit.getWorlds().get(0).setStorm(false);
                Bukkit.getWorlds().get(0).setThundering(false);
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.sunny")));
                break;
            case 2:
                Bukkit.getWorlds().get(0).setStorm(true);
                Bukkit.getWorlds().get(0).setThundering(false);
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.rain")));
                break;
            case 3:
                Bukkit.getWorlds().get(0).setStorm(true);
                Bukkit.getWorlds().get(0).setThundering(true);
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.thunder")));
                break;
        }
    }
}
