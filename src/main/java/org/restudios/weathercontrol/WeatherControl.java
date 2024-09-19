package org.restudios.weathercontrol;

import org.bukkit.plugin.java.JavaPlugin;

public final class WeatherControl extends JavaPlugin {

    private WeatherManager weatherManager;
    private TimeManager timeManager;
    private VoteManager voteManager;
    private HelpMenu helpMenu;
    private ReloadCommand reloadCommand;

    @Override
    public void onEnable() {
        saveDefaultConfig();  // Создание config.yml, если его нет

        // Инициализация менеджеров
        weatherManager = new WeatherManager(this);
        timeManager = new TimeManager(this);
        voteManager = new VoteManager(this);
        helpMenu = new HelpMenu(this);
        reloadCommand = new ReloadCommand(this);

        // Регистрация команд
        getCommand("weather").setExecutor(weatherManager);
        getCommand("time").setExecutor(timeManager);
        getCommand("votetime").setExecutor(voteManager);
        getCommand("voteweather").setExecutor(voteManager);
        getCommand("castvote").setExecutor(voteManager);
        getCommand("weathercontrolhelp").setExecutor(helpMenu);
        getCommand("weathercontrolreload").setExecutor(reloadCommand);  // Регистрация команды перезагрузки

        // Автоматическая смена погоды
        weatherManager.startAutoWeatherCycle();
    }

    @Override
    public void onDisable() {
        // Логика при отключении плагина
    }

    public WeatherManager getWeatherManager() {
        return weatherManager;
    }

    public TimeManager getTimeManager() {
        return timeManager;
    }

    public VoteManager getVoteManager() {
        return voteManager;
    }
    public HelpMenu getHelpMenu() {
        return helpMenu;
    }
}
