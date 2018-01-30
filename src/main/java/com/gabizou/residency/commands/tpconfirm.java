package com.gabizou.residency.commands;

import com.gabizou.residency.Residence;
import com.gabizou.residency.containers.CommandAnnotation;
import com.gabizou.residency.containers.ConfigReader;
import com.gabizou.residency.containers.cmd;
import com.gabizou.residency.containers.lm;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class tpconfirm implements cmd {

    @Override
    @CommandAnnotation(simple = true, priority = 1500)
    public boolean perform(Residence plugin, String[] args, boolean resadmin, Command command, CommandSender sender) {
		if (!(sender instanceof Player)) {
			return false;
		}

        Player player = (Player) sender;
        if (args.length != 1) {
            return false;
        }
		if (plugin.getTeleportMap().containsKey(player.getName())) {
			plugin.getTeleportMap().get(player.getName()).tpToResidence(player, player, resadmin);
			plugin.getTeleportMap().remove(player.getName());
		} else {
			plugin.msg(player, lm.General_NoTeleportConfirm);
		}
        return true;
    }

    @Override
    public void getLocale(ConfigReader c, String path) {
        c.get(path + "Description", "Ignore unsafe teleportation warning");
        c.get(path + "Info", Arrays.asList("&eUsage: &6/res tpconfirm", "Teleports you to a residence, when teleportation is unsafe."));
    }
}
