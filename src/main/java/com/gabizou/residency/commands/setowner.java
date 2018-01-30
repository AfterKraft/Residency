package com.gabizou.residency.commands;

import com.gabizou.residency.Residence;
import com.gabizou.residency.containers.CommandAnnotation;
import com.gabizou.residency.containers.ConfigReader;
import com.gabizou.residency.containers.cmd;
import com.gabizou.residency.containers.lm;
import com.gabizou.residency.protection.ClaimedResidence;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class setowner implements cmd {

    @Override
    @CommandAnnotation(simple = false, priority = 5500)
    public boolean perform(Residence plugin, String[] args, boolean resadmin, Command command, CommandSender sender) {

        if (args.length < 3) {
            return false;
        }

        if (!resadmin) {
            plugin.msg(sender, lm.General_NoPermission);
            return true;
        }

        ClaimedResidence area = plugin.getResidenceManager().getByName(args[1]);
        if (area != null) {
            area.getPermissions().setOwner(args[2], true);
            if (plugin.getRentManager().isForRent(area.getName())) {
                plugin.getRentManager().removeRentable(area.getName());
            }
            if (plugin.getTransactionManager().isForSale(area.getName())) {
                plugin.getTransactionManager().removeFromSale(area.getName());
            }
            area.getPermissions().applyDefaultFlags();

            if (area.getParent() == null) {
                plugin.msg(sender, lm.Residence_OwnerChange, args[1], args[2]);
            } else {
                plugin.msg(sender, lm.Subzone_OwnerChange, args[1].split("\\.")[args[1].split("\\.").length - 1], args[2]);
            }
        } else {
            plugin.msg(sender, lm.Invalid_Residence);
        }
        return true;
    }

    @Override
    public void getLocale(ConfigReader c, String path) {
        c.get(path + "Description", "Change owner of a residence.");
        c.get(path + "Info", Arrays.asList("&eUsage: &6/resadmin setowner [residence] [player]"));
        Residence.getInstance().getLocaleManager().CommandTab.put(Arrays.asList(this.getClass().getSimpleName()), Arrays.asList("[cresidence]"));
    }

}
