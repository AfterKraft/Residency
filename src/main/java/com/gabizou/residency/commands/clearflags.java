package com.gabizou.residency.commands;

import com.gabizou.residency.Residence;
import com.gabizou.residency.containers.CommandAnnotation;
import com.gabizou.residency.containers.ConfigReader;
import com.gabizou.residency.containers.cmd;
import com.gabizou.residency.containers.lm;
import com.gabizou.residency.protection.ClaimedResidence;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class clearflags implements cmd {

    @Override
    @CommandAnnotation(simple = false, priority = 3600)
    public boolean perform(Residence plugin, String[] args, boolean resadmin, Command command, CommandSender sender) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        if (!resadmin) {
            plugin.msg(player, lm.General_NoPermission);
            return true;
        }
        ClaimedResidence area = plugin.getResidenceManager().getByName(args[1]);
        if (area != null) {
            area.getPermissions().clearFlags();
            plugin.msg(player, lm.Flag_Cleared);
        } else {
            plugin.msg(player, lm.Invalid_Residence);
        }
        return true;
    }

    @Override
    public void getLocale(ConfigReader c, String path) {
        c.get(path + "Description", "Remove all flags from residence");
        c.get(path + "Info", Arrays.asList("&eUsage: &6/res clearflags <residence>"));
        Residence.getInstance().getLocaleManager().CommandTab.put(Arrays.asList(this.getClass().getSimpleName()), Arrays.asList("[residence]"));
    }
}
