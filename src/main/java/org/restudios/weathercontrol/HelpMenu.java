package org.restudios.weathercontrol;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HelpMenu implements CommandExecutor {

    private final WeatherControl plugin;

    public HelpMenu(WeatherControl plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("weathercontrolhelp")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                sendHelpMenu(player);
            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.no_permission")));
            }
            return true;
        }
        return false;
    }

    private void sendHelpMenu(Player player) {
        String[] helpMenu = plugin.getConfig().getStringList("messages.help_menu").toArray(new String[0]);
        for (String line : helpMenu) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
        }
    }
}
