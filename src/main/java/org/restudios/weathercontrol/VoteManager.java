package org.restudios.weathercontrol;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class VoteManager implements CommandExecutor {

    private final WeatherControl plugin;


    public VoteManager(WeatherControl plugin) {
        this.plugin = plugin;
    }
    private final Map<Player, Boolean> weatherVotes = new HashMap<>();
    private final Map<Player, Boolean> timeVotes = new HashMap<>();
    private boolean isVotingActive = false;




    private String getConfigMessage(String key) {
        return ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages." + key));
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.no_permission")));
            return true;
        }


        Player player = (Player) sender;




        if (command.getName().equalsIgnoreCase("voteweather")) {
            if (!player.hasPermission("weathercontrol.vote")) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.no_permission")));
                return true;
            }
            if (isVotingActive) {
                player.sendMessage(ChatColor.RED + getConfigMessage("vote_active"));
                return true;
            }
            startWeatherVote();
        } else if (command.getName().equalsIgnoreCase("votetime")) {
            if (!player.hasPermission("weathercontrol.vote")) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.no_permission")));
                return true;
            }
            if (isVotingActive) {
                player.sendMessage(ChatColor.RED + getConfigMessage("vote_active"));
                return true;
            }
            startTimeVote();
        } else if (command.getName().equalsIgnoreCase("castvote")) {
            if (args.length < 2) {
                player.sendMessage(ChatColor.RED + getConfigMessage("invalid_vote"));
                return true;
            }

            String voteType = args[0].toLowerCase();
            String vote = args[1].toLowerCase();

            if (!vote.equals("yes") && !vote.equals("no")) {
                player.sendMessage(ChatColor.RED + getConfigMessage("vote_invalid"));
                return true;
            }

            if (!isVotingActive) {
                player.sendMessage(ChatColor.RED + getConfigMessage("no_active_vote"));
                return true;
            }

            boolean isWeatherVote = voteType.equals("weather");
            boolean voteValue = vote.equals("yes");

            if (isWeatherVote || voteType.equals("time")) {
                castVote(player, voteValue, isWeatherVote);
            } else {
                player.sendMessage(ChatColor.RED + getConfigMessage("invalid_vote_type"));
            }
        } else {
            player.sendMessage(ChatColor.RED + getConfigMessage("invalid_vote_type"));
        }

        return true;
    }

    private void startWeatherVote() {
        isVotingActive = true;
        Bukkit.broadcastMessage(getConfigMessage("vote_start_weather"));

        for (Player player : Bukkit.getOnlinePlayers()) {
            TextComponent message = new TextComponent(getConfigMessage("vote")+" " +
                    "");

            TextComponent yesButton = new TextComponent("[✓]");
            yesButton.setColor(net.md_5.bungee.api.ChatColor.GREEN);
            yesButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/castvote weather yes"));
            yesButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(getConfigMessage("vote_success")).create()));

            TextComponent noButton = new TextComponent("[✗]");
            noButton.setColor(net.md_5.bungee.api.ChatColor.RED);
            noButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/castvote weather no"));
            noButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(getConfigMessage("vote_invalid")).create()));

            message.addExtra(yesButton);
            message.addExtra(" ");
            message.addExtra(noButton);

            player.spigot().sendMessage(message);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                tallyWeatherVotes();
                isVotingActive = false;
            }
        }.runTaskLater(plugin, plugin.getConfig().getLong("vote_duration") * 20L);
    }

    private void startTimeVote() {
        isVotingActive = true;
        Bukkit.broadcastMessage(getConfigMessage("vote_start_time"));

        for (Player player : Bukkit.getOnlinePlayers()) {
            TextComponent message = new TextComponent(getConfigMessage("vote")+" ");

            TextComponent yesButton = new TextComponent("[✓]");
            yesButton.setColor(net.md_5.bungee.api.ChatColor.GREEN);
            yesButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/castvote time yes"));
            yesButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(getConfigMessage("vote_success")).create()));

            TextComponent noButton = new TextComponent("[✗]");
            noButton.setColor(net.md_5.bungee.api.ChatColor.RED);
            noButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/castvote time no"));
            noButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(getConfigMessage("vote_invalid")).create()));

            message.addExtra(yesButton);
            message.addExtra(" ");
            message.addExtra(noButton);

            player.spigot().sendMessage(message);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                tallyTimeVotes();
                isVotingActive = false;
            }
        }.runTaskLater(plugin, plugin.getConfig().getLong("vote_duration") * 20L);
    }

    public void castVote(Player player, boolean vote, boolean isWeatherVote) {
        if (isVotingActive) {
            if (isWeatherVote) {
                weatherVotes.put(player, vote);
            } else {
                timeVotes.put(player, vote);
            }
            player.sendMessage(getConfigMessage("vote_accepted"));
        } else {
            player.sendMessage(getConfigMessage("vote_inactive"));
        }
    }

    private void tallyWeatherVotes() {
        long yesVotes = weatherVotes.values().stream().filter(vote -> vote).count();
        long noVotes = weatherVotes.size() - yesVotes;

        if (yesVotes > noVotes) {
            applyWeatherChange("sunny");
        } else {
            Bukkit.broadcastMessage(getConfigMessage("vote_rejected"));
        }

        weatherVotes.clear();
    }

    private void tallyTimeVotes() {
        long yesVotes = timeVotes.values().stream().filter(vote -> vote).count();
        long noVotes = timeVotes.size() - yesVotes;

        if (yesVotes > noVotes) {
            applyTimeChange("day");
        } else {
            Bukkit.broadcastMessage(getConfigMessage("time_vote_rejected"));
        }

        timeVotes.clear();
    }

    private void applyWeatherChange(String weather) {
        switch (weather) {
            case "sunny":
                Bukkit.getWorlds().get(0).setStorm(false);
                Bukkit.getWorlds().get(0).setThundering(false);
                Bukkit.broadcastMessage(getConfigMessage("sunny"));
                break;
            case "rain":
                Bukkit.getWorlds().get(0).setStorm(true);
                Bukkit.getWorlds().get(0).setThundering(false);
                Bukkit.broadcastMessage(getConfigMessage("rain"));
                break;
            case "thunder":
                Bukkit.getWorlds().get(0).setStorm(true);
                Bukkit.getWorlds().get(0).setThundering(true);
                Bukkit.broadcastMessage(getConfigMessage("hunder"));
                break;
        }
    }

    private void applyTimeChange(String time) {
        switch (time) {
            case "day":
                Bukkit.getWorlds().get(0).setTime(1000);
                Bukkit.broadcastMessage(getConfigMessage("day"));
                break;
            case "night":
                Bukkit.getWorlds().get(0).setTime(13000);
                Bukkit.broadcastMessage(getConfigMessage("night"));
                break;
        }
    }
}
