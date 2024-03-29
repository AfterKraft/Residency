package com.gabizou.residency.commands;

import com.gabizou.residency.Residence;
import com.gabizou.residency.containers.CommandAnnotation;
import com.gabizou.residency.containers.ConfigReader;
import com.gabizou.residency.containers.Flags;
import com.gabizou.residency.containers.cmd;
import com.gabizou.residency.containers.lm;
import com.gabizou.residency.gui.SetFlag;
import com.gabizou.residency.protection.ClaimedResidence;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class pset implements cmd {

    @Override
    @CommandAnnotation(simple = true, priority = 800)
    public boolean perform(Residence plugin, String[] args, boolean resadmin, Command command, CommandSender sender) {
		if (!(sender instanceof Player) && args.length != 5 && args.length == 4 && !args[3].equalsIgnoreCase("removeall")) {
			return false;
		}

        if (args.length == 3 && args[2].equalsIgnoreCase("removeall")) {
            Player player = (Player) sender;
            ClaimedResidence area = plugin.getResidenceManager().getByLoc(player.getLocation());
            if (area != null) {
                area.getPermissions().removeAllPlayerFlags(sender, args[1], resadmin);
            } else {
                plugin.msg(sender, lm.Invalid_Residence);
            }
            return true;
        } else if (args.length == 4 && args[3].equalsIgnoreCase("removeall")) {
            ClaimedResidence area = plugin.getResidenceManager().getByName(args[1]);
            if (area != null) {
                area.getPermissions().removeAllPlayerFlags(sender, args[2], resadmin);
            } else {
                plugin.msg(sender, lm.Invalid_Residence);
            }
            return true;
        } else if (args.length == 4) {
            Player player = (Player) sender;
            ClaimedResidence res = plugin.getResidenceManager().getByLoc(player.getLocation());

			if (!plugin.isPlayerExist(sender, args[1], true)) {
				return false;
			}

            if (res == null) {
                plugin.msg(sender, lm.Invalid_Residence);
                return true;
            }

            if (!res.isOwner(player) && !resadmin && !res.getPermissions().playerHas(player, Flags.admin, false)) {
                plugin.msg(sender, lm.General_NoPermission);
                return true;
            }
            res.getPermissions().setPlayerFlag(sender, args[1], args[2], args[3], resadmin, true);

            return true;
        } else if (args.length == 5) {
            ClaimedResidence res = plugin.getResidenceManager().getByName(args[1]);
			if (!plugin.isPlayerExist(sender, args[2], true)) {
				return false;
			}

            if (res == null) {
                plugin.msg(sender, lm.Invalid_Residence);
                return true;
            }

            if (!res.isOwner(sender) && !resadmin && !res.getPermissions().playerHas(sender, Flags.admin, false)) {
                plugin.msg(sender, lm.General_NoPermission);
                return true;
            }

            res.getPermissions().setPlayerFlag(sender, args[2], args[3], args[4], resadmin, true);
            return true;
        } else if ((args.length == 2 || args.length == 3) && plugin.getConfigManager().useFlagGUI()) {
            Player player = (Player) sender;
            ClaimedResidence res = null;
            String targetPlayer = null;
            if (args.length == 2) {
                res = plugin.getResidenceManager().getByLoc(player.getLocation());
                targetPlayer = args[1];
            } else {
                res = plugin.getResidenceManager().getByName(args[1]);
                targetPlayer = args[2];
            }

            if (res == null) {
                plugin.msg(sender, lm.Invalid_Residence);
                return true;
            }

			if (!plugin.isPlayerExist(player, targetPlayer, true)) {
				return false;
			}
            if (!res.isOwner(player) && !resadmin && !res.getPermissions().playerHas(player, Flags.admin, false)) {
                plugin.msg(sender, lm.General_NoPermission);
                return true;
            }
            SetFlag flag = new SetFlag(res, player, resadmin);
            flag.setTargetPlayer(targetPlayer);
            flag.recalculatePlayer(res);
            player.closeInventory();
            plugin.getPlayerListener().getGUImap().put(player.getName(), flag);
            player.openInventory(flag.getInventory());

            return true;
        }
        return false;
    }

    @Override
    public void getLocale(ConfigReader c, String path) {
        c.get(path + "Description", "Set flags on a specific player for a Residence.");
        c.get(path + "Info", Arrays.asList("&eUsage: &6/res pset <residence> [player] [flag] [true/false/remove]",
            "&eUsage: &6/res pset <residence> [player] removeall", "To see a list of flags, use /res flags ?"));
        Residence.getInstance().getLocaleManager().CommandTab
            .put(Arrays.asList(this.getClass().getSimpleName()), Arrays.asList("[residence]%%[playername]", "[playername]%%[flag]",
                "[flag]%%true%%false%%remove",
                "true%%false%%remove"));
    }
}
