package com.xandone.primalcalendar;

import com.xandone.primalcalendar.api.CalendarAPI;
import com.xandone.primalcalendar.calendar.CalendarManager;
import com.xandone.primalcalendar.commands.BirthdayCommand;
import com.xandone.primalcalendar.commands.CalendarAdminCommand;
import com.xandone.primalcalendar.commands.CalendarCommand;
import com.xandone.primalcalendar.commands.HolidayCommand;
import com.xandone.primalcalendar.config.ConfigManager;
import com.xandone.primalcalendar.discord.DiscordWebhook;
import com.xandone.primalcalendar.events.EventManager;
import com.xandone.primalcalendar.hooks.PlaceholderAPIHook;
import com.xandone.primalcalendar.hooks.VaultHook;
import com.xandone.primalcalendar.language.LanguageManager;
import com.xandone.primalcalendar.listeners.PlayerJoinListener;
import com.xandone.primalcalendar.storage.DataManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class XandonePrimalCalendar extends JavaPlugin {

    private static XandonePrimalCalendar instance;
    private ConfigManager configManager;
    private LanguageManager languageManager;
    private CalendarManager calendarManager;
    private EventManager eventManager;
    private DataManager dataManager;
    private DiscordWebhook discordWebhook;
    private VaultHook vaultHook;
    private CalendarAPI calendarAPI;

    @Override
    public void onEnable() {
        instance = this;
        
        this.configManager = new ConfigManager(this);
        this.languageManager = new LanguageManager(this);
        this.dataManager = new DataManager(this);
        
        configManager.loadConfig();
        languageManager.loadLanguages();
        dataManager.loadData();
        
        this.calendarManager = new CalendarManager(this);
        this.eventManager = new EventManager(this);
        this.discordWebhook = new DiscordWebhook(this);
        this.calendarAPI = new CalendarAPI(this);
        
        // Initialize calendar
        calendarManager.initialize();
        
        // Register commands
        registerCommands();
        
        registerListeners();
        
        // Hook into external plugins
        hookExternalPlugins();
        
        // Start calendar ticker
        calendarManager.startTicker();
        
        getLogger().info("XandonePrimalCalendar has been enabled!");
    }

    @Override
    public void onDisable() {
        if (calendarManager != null) {
            calendarManager.stopTicker();
        }
        if (dataManager != null) {
            dataManager.saveData();
        }
        getLogger().info("XandonePrimalCalendar has been disabled!");
    }

    private void registerCommands() {
        getCommand("calendar").setExecutor(new CalendarCommand(this));
        getCommand("caladmin").setExecutor(new CalendarAdminCommand(this));
        getCommand("holiday").setExecutor(new HolidayCommand(this));
        getCommand("birthday").setExecutor(new BirthdayCommand(this));
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
    }

    private void hookExternalPlugins() {
        // Hook into Vault
        if (getServer().getPluginManager().getPlugin("Vault") != null) {
            vaultHook = new VaultHook(this);
            if (vaultHook.setupEconomy()) {
                getLogger().info("Hooked into Vault!");
            }
        }
        
        // Hook into PlaceholderAPI
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderAPIHook(this).register();
            getLogger().info("Hooked into PlaceholderAPI!");
        }
    }

    public static XandonePrimalCalendar getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public LanguageManager getLanguageManager() {
        return languageManager;
    }

    public CalendarManager getCalendarManager() {
        return calendarManager;
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public DiscordWebhook getDiscordWebhook() {
        return discordWebhook;
    }

    public VaultHook getVaultHook() {
        return vaultHook;
    }

    public CalendarAPI getCalendarAPI() {
        return calendarAPI;
    }
}
