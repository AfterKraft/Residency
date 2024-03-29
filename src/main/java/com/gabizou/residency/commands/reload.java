package com.gabizou.residency.commands;

import com.gabizou.residency.Residence;
import com.gabizou.residency.containers.CommandAnnotation;
import com.gabizou.residency.containers.ConfigReader;
import com.gabizou.residency.containers.cmd;
import com.gabizou.residency.containers.lm;
import com.gabizou.residency.itemlist.WorldItemManager;
import com.gabizou.residency.permissions.PermissionManager;
import com.gabizou.residency.protection.WorldFlagManager;
import com.gabizou.residency.text.help.HelpEntry;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class reload implements cmd {

    @Override
    @CommandAnnotation(simple = false, priority = 5800)
    public boolean perform(Residence plugin, String[] args, boolean resadmin, Command command, CommandSender sender) {
        if (!resadmin && !sender.isOp()) {
            plugin.msg(sender, lm.General_NoPermission);
            return true;
        }

        if (args.length != 2) {
            return false;
        }

        if (args[1].equalsIgnoreCase("lang")) {
            plugin.getLM().LanguageReload();
            File langFile = new File(new File(plugin.dataFolder, "Language"), plugin.getConfigManager().getLanguage() + ".yml");
            BufferedReader in = null;
            try {
                in = new BufferedReader(new InputStreamReader(new FileInputStream(langFile), "UTF8"));
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }
            if (langFile.isFile()) {
                FileConfiguration langconfig = new YamlConfiguration();
                try {
                    langconfig.load(in);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InvalidConfigurationException e) {
                    e.printStackTrace();
                }
                plugin.helppages = HelpEntry.parseHelp(langconfig, "CommandHelp");
            } else {
                System.out.println(plugin.getPrefix() + " Language file does not exist...");
            }
            sender.sendMessage(plugin.getPrefix() + " Reloaded language file.");
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return true;
        } else if (args[1].equalsIgnoreCase("config")) {
            plugin.getConfigManager().UpdateConfigFile();
            sender.sendMessage(plugin.getPrefix() + " Reloaded config file.");
            return true;
        } else if (args[1].equalsIgnoreCase("groups")) {
            plugin.getConfigManager().loadGroups();
            plugin.gmanager = new PermissionManager(plugin);
            plugin.wmanager = new WorldFlagManager(plugin);
            sender.sendMessage(plugin.getPrefix() + " Reloaded groups file.");
            return true;
        } else if (args[1].equalsIgnoreCase("flags")) {
            plugin.getConfigManager().loadFlags();
            plugin.gmanager = new PermissionManager(plugin);
            plugin.imanager = new WorldItemManager(plugin);
            plugin.wmanager = new WorldFlagManager(plugin);
            sender.sendMessage(plugin.getPrefix() + " Reloaded flags file.");
            return true;
        }
        return false;
    }

    @Override
    public void getLocale(ConfigReader c, String path) {
        c.get(path + "Description", "reload lanf or config files");
        c.get(path + "Info", Arrays.asList("&eUsage: &6/res reload [config/lang/groups/flags]"));
        Residence.getInstance().getLocaleManager().CommandTab
            .put(Arrays.asList(this.getClass().getSimpleName()), Arrays.asList("config%%lang%%groups%%flags"));
    }
}
