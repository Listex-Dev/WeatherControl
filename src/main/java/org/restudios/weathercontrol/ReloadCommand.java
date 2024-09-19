package org.restudios.weathercontrol;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;

public class ReloadCommand implements CommandExecutor {

    private final WeatherControl plugin;

    public ReloadCommand(WeatherControl plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("weathercontrolreload")) {
            // Перезагрузка конфигурации
            plugin.reloadConfig();

            // Получение сообщения из конфига
            String reloadMessage = plugin.getConfig().getString("messages.config_reloaded");

            // Отправка сообщения
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', reloadMessage));
            return true;
        }
        return false;
    }
}
