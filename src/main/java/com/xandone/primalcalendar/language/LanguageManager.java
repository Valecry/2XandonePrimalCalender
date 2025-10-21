package com.xandone.primalcalendar.language;

import com.xandone.primalcalendar.XandonePrimalCalendar;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class LanguageManager {

    private final XandonePrimalCalendar plugin;
    private FileConfiguration language;
    private String currentLanguage;

    public LanguageManager(XandonePrimalCalendar plugin) {
        this.plugin = plugin;
    }

    public void loadLanguages() {
        currentLanguage = plugin.getConfig().getString("language", "en_US");
        
        File langFolder = new File(plugin.getDataFolder(), "languages");
        if (!langFolder.exists()) {
            langFolder.mkdirs();
        }

        File langFile = new File(langFolder, currentLanguage + ".yml");
        if (!langFile.exists()) {
            try {
                InputStream in = plugin.getResource("languages/" + currentLanguage + ".yml");
                if (in != null) {
                    Files.copy(in, langFile.toPath());
                } else {
                    plugin.getLogger().warning("Language file not found: " + currentLanguage + ".yml, using default");
                    currentLanguage = "en_US";
                    langFile = new File(langFolder, "en_US.yml");
                    in = plugin.getResource("languages/en_US.yml");
                    if (in != null) {
                        Files.copy(in, langFile.toPath());
                    }
                }
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create language file: " + e.getMessage());
            }
        }

        language = YamlConfiguration.loadConfiguration(langFile);
    }

    public String getMessage(String path) {
        String message = language.getString(path, "Message not found: " + path);
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public String getMessage(String path, String... replacements) {
        String message = getMessage(path);
        for (int i = 0; i < replacements.length; i += 2) {
            if (i + 1 < replacements.length) {
                message = message.replace(replacements[i], replacements[i + 1]);
            }
        }
        return message;
    }
}
