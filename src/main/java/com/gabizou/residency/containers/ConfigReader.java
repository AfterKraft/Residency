package com.gabizou.residency.containers;

import com.gabizou.residency.CommentedYamlConfiguration;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.List;

public class ConfigReader {

    YamlConfiguration config;
    CommentedYamlConfiguration writer;

    public ConfigReader(YamlConfiguration config, CommentedYamlConfiguration writer) {
        this.config = config;
        this.writer = writer;
    }

    public CommentedYamlConfiguration getW() {
        return this.writer;
    }

    public YamlConfiguration getC() {
        return this.config;
    }

    public Boolean get(String path, Boolean boo) {
        this.config.addDefault(path, boo);
        copySetting(path);
        return this.config.getBoolean(path);
    }

    public synchronized void copySetting(String path) {
        this.writer.set(path, this.config.get(path));
    }

    public int get(String path, int boo) {
        this.config.addDefault(path, boo);
        copySetting(path);
        return this.config.getInt(path);
    }

    public List<Integer> getIntList(String path, List<Integer> list) {
        this.config.addDefault(path, list);
        copySetting(path);
        return this.config.getIntegerList(path);
    }

    public List<String> get(String path, List<String> list, boolean colorize) {
        this.config.addDefault(path, list);
        copySetting(path);
        if (colorize) {
            return ColorsArray(this.config.getStringList(path));
        }
        return this.config.getStringList(path);
    }

    private static List<String> ColorsArray(List<String> text) {
        List<String> temp = new ArrayList<String>();
        for (String part : text) {
            temp.add(Colors(part));
        }
        return temp;
    }

    private static String Colors(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public List<String> get(String path, List<String> list) {
        this.config.addDefault(path, list);
        copySetting(path);
        return this.config.getStringList(path);
    }

    public String get(String path, String boo) {
        this.config.addDefault(path, boo);
        copySetting(path);
        return get(path, boo, true);
    }

    public String get(String path, String boo, boolean colorize) {
        this.config.addDefault(path, boo);
        copySetting(path);
        if (colorize) {
            return ChatColor.translateAlternateColorCodes('&', this.config.getString(path));
        }
        return this.config.getString(path);
    }

    public Double get(String path, Double boo) {
        this.config.addDefault(path, boo);
        copySetting(path);
        return this.config.getDouble(path);
    }
}
