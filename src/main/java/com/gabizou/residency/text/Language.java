package com.gabizou.residency.text;

import com.gabizou.residency.Residence;
import com.gabizou.residency.containers.Flags;
import com.gabizou.residency.containers.lm;
import com.gabizou.residency.utils.YmlMaker;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class Language {

    public FileConfiguration enlocale;
    public FileConfiguration customlocale;
    private Residence plugin;

    public Language(Residence plugin) {
        this.plugin = plugin;
    }

    /**
     * Reloads the config
     */
    public void LanguageReload() {
        this.customlocale = new YmlMaker(this.plugin, "Language/" + this.plugin.getConfigManager().getLanguage() + ".yml").getConfig();
        this.enlocale = new YmlMaker(this.plugin, "Language/English.yml").getConfig();
        if (this.customlocale == null) {
            this.customlocale = this.enlocale;
        }
    }

    /**
     * Get the message with the correct key
     *
     * @param key
     *            - the key of the message
     * @return the message
     */
//    public String getMessage2(String key) {
//	return getMessage(key, "");
//    }

    /**
     * Get the message with the correct key
     *
     * @param key - the path of the message
     * @param variables - the variables separated with %
     * @return the message
     */

    public String getMessage(String key) {
        if (!key.contains("Language.") && !key.contains("CommandHelp.")) {
            key = "Language." + key;
        }
        String missing = "Missing locale for " + key;
        String message = "";
        if (this.customlocale == null || !this.customlocale.contains(key)) {
            message = this.enlocale.contains(key) == true ? this.enlocale.getString(key) : missing;
        }
        message = this.customlocale.contains(key) == true ? this.customlocale.getString(key) : missing;
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    /**
     * Get the message with the correct key
     *
     * @param key - the path of the message
     * @param variables - the variables separated with %
     * @return the message
     */

    public String getMessage(lm lm, Object... variables) {
        String key = lm.getPath();
        if (!key.contains("Language.") && !key.contains("CommandHelp.")) {
            key = "Language." + key;
        }
        String missing = "Missing locale for " + key;
        String message = "";
        if (this.customlocale == null || !this.customlocale.contains(key)) {
            message = this.enlocale.contains(key) == true ? this.enlocale.getString(key) : missing;
        }
        message = this.customlocale.contains(key) == true ? this.customlocale.getString(key) : missing;

        for (int i = 1; i <= variables.length; i++) {
            String vr = String.valueOf(variables[i - 1]);
            if (variables[i - 1] instanceof Flags) {
                vr = ((Flags) variables[i - 1]).getName();
            }
            message = message.replace("%" + i, vr);
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    /**
     * Get the message with the correct key
     *
     * @param key - the key of the message
     * @return the message
     */
    public String getDefaultMessage(String key) {
        if (!key.contains("Language.") && !key.contains("CommandHelp.")) {
            key = "Language." + key;
        }
        String missing = "Missing locale for " + key;
        return this.enlocale.contains(key) == true ? ChatColor.translateAlternateColorCodes('&', this.enlocale.getString(key)) : missing;
    }

    /**
     * Get the message with the correct key
     *
     * @param key - the key of the message
     * @return the message
     */
    public List<String> getMessageList2(String key) {
        if (!key.contains("Language.") && !key.contains("CommandHelp.")) {
            key = "Language." + key;
        }
        String missing = "Missing locale for " + key;
        if (this.customlocale.isList(key)) {
            return ColorsArray(this.customlocale.getStringList(key));
        }
        return this.enlocale.getStringList(key).size() > 0 ? ColorsArray(this.enlocale.getStringList(key)) : Arrays.asList(missing);
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

    /**
     * Get the message with the correct key
     *
     * @param key - the key of the message
     * @return the message
     */
    public List<String> getMessageList(lm lm) {
        String key = lm.getPath();
        if (!key.contains("Language.") && !key.contains("CommandHelp.")) {
            key = "Language." + key;
        }
        String missing = "Missing locale for " + key;
        if (this.customlocale.isList(key)) {
            return ColorsArray(this.customlocale.getStringList(key));
        }
        return this.enlocale.getStringList(key).size() > 0 ? ColorsArray(this.enlocale.getStringList(key)) : Arrays.asList(missing);
    }

    /**
     * Get the message with the correct key
     *
     * @param key - the key of the message
     * @return the message
     */
    public Set<String> getKeyList(String key) {
        if (this.customlocale.isConfigurationSection(key)) {
            return this.customlocale.getConfigurationSection(key).getKeys(false);
        }
        return this.enlocale.getConfigurationSection(key).getKeys(false);
    }

    /**
     * Check if key exists
     *
     * @param key - the key of the message
     * @return true/false
     */
    public boolean containsKey(String key) {
        if (!key.contains("Language.") && !key.contains("CommandHelp.")) {
            key = "Language." + key;
        }
        if (this.customlocale == null || !this.customlocale.contains(key)) {
            return this.enlocale.contains(key);
        }
        return this.customlocale.contains(key);
    }
}
