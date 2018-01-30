package com.gabizou.residency.commands;

import com.gabizou.residency.Residence;
import com.gabizou.residency.containers.CommandAnnotation;
import com.gabizou.residency.containers.ConfigReader;
import com.gabizou.residency.containers.cmd;
import com.gabizou.residency.containers.lm;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class tool implements cmd {

    @SuppressWarnings("deprecation")
    @Override
    @CommandAnnotation(simple = true, priority = 1600)
    public boolean perform(Residence plugin, String[] args, boolean resadmin, Command command, CommandSender sender) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        plugin.msg(player, lm.General_Separator);
        plugin.msg(player, lm.Select_Tool, Material.getMaterial(plugin.getConfigManager().getSelectionTooldID()));
        plugin.msg(player, lm.General_InfoTool, Material.getMaterial(plugin.getConfigManager().getInfoToolID()));
        plugin.msg(player, lm.General_Separator);
        return true;
    }

    @Override
    public void getLocale(ConfigReader c, String path) {
        c.get(path + "Description", "Shows residence selection and info tool names");
        c.get(path + "Info", Arrays.asList("&eUsage: &6/res tool"));
    }
}
