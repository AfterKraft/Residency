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

public class material implements cmd {

    @SuppressWarnings("deprecation")
    @Override
    @CommandAnnotation(simple = true, priority = 4300)
    public boolean perform(Residence plugin, String[] args, boolean resadmin, Command command, CommandSender sender) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;
        if (args.length != 2) {
            return false;
        }
        try {
            plugin.msg(player, lm.General_MaterialGet, args[1], Material.getMaterial(Integer.parseInt(args[1])).name());
        } catch (Exception ex) {
            plugin.msg(player, lm.Invalid_Material);
        }
        return true;
    }

    @Override
    public void getLocale(ConfigReader c, String path) {
        c.get(path + "Description", "Check if material exists by its id");
        c.get(path + "Info", Arrays.asList("&eUsage: &6/res material [material]"));
        Residence.getInstance().getLocaleManager().CommandTab.put(Arrays.asList(this.getClass().getSimpleName()), Arrays.asList("[materialId]"));
    }
}
