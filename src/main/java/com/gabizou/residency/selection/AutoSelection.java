package com.gabizou.residency.selection;

import com.gabizou.residency.Residence;
import com.gabizou.residency.containers.AutoSelector;
import com.gabizou.residency.containers.ResidencePlayer;
import com.gabizou.residency.containers.lm;
import com.gabizou.residency.permissions.PermissionGroup;
import com.gabizou.residency.protection.CuboidArea;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class AutoSelection {

    private HashMap<String, AutoSelector> list = new HashMap<String, AutoSelector>();
    private Residence plugin;

    public AutoSelection(Residence residence) {
        this.plugin = residence;
    }

    public void switchAutoSelection(Player player) {
        if (!this.list.containsKey(player.getName().toLowerCase())) {
            ResidencePlayer rPlayer = this.plugin.getPlayerManager().getResidencePlayer(player);
            PermissionGroup group = rPlayer.getGroup(player.getWorld().getName());
            this.list.put(player.getName().toLowerCase(), new AutoSelector(group, System.currentTimeMillis()));
            this.plugin.msg(player, lm.Select_AutoEnabled);
        } else {
            this.list.remove(player.getName().toLowerCase());
            this.plugin.msg(player, lm.Select_AutoDisabled);
        }
    }

    public void UpdateSelection(Player player) {

		if (!getList().containsKey(player.getName().toLowerCase())) {
			return;
		}

        AutoSelector AutoSelector = getList().get(player.getName().toLowerCase());

        int Curenttime = (int) (System.currentTimeMillis() - AutoSelector.getTime()) / 1000;

        if (Curenttime > 270) {
            this.list.remove(player.getName().toLowerCase());
            this.plugin.msg(player, lm.Select_AutoDisabled);
            return;
        }

        String name = player.getName();

        Location cloc = player.getLocation();

        Location loc1 = this.plugin.getSelectionManager().getPlayerLoc1(name);
        Location loc2 = this.plugin.getSelectionManager().getPlayerLoc2(name);

        if (loc1 == null) {
            this.plugin.getSelectionManager().placeLoc1(player, cloc, false);
            loc1 = player.getLocation();
            return;
        }

        if (loc2 == null) {
            this.plugin.getSelectionManager().placeLoc2(player, cloc, true);
            loc2 = player.getLocation();
            return;
        }

        boolean changed = false;

        CuboidArea area = new CuboidArea(loc1, loc2);
        Location hloc = area.getHighLoc();
        Location lloc = area.getLowLoc();

        if (cloc.getBlockX() < lloc.getBlockX()) {
            lloc.setX(cloc.getBlockX());
            changed = true;
        }

        if (cloc.getBlockY() <= lloc.getBlockY()) {
            lloc.setY(cloc.getBlockY() - 1);
            changed = true;
        }

        if (cloc.getBlockZ() < lloc.getBlockZ()) {
            lloc.setZ(cloc.getBlockZ());
            changed = true;
        }

        if (cloc.getBlockX() > hloc.getBlockX()) {
            hloc.setX(cloc.getBlockX());
            changed = true;
        }

        if (cloc.getBlockY() >= hloc.getBlockY()) {
            hloc.setY(cloc.getBlockY() + 1);
            changed = true;
        }

        if (cloc.getBlockZ() > hloc.getBlockZ()) {
            hloc.setZ(cloc.getBlockZ());
            changed = true;
        }

        PermissionGroup group = AutoSelector.getGroup();

        if (area.getXSize() > group.getMaxX()) {
            return;
        }

        if (area.getYSize() > group.getMaxY() && !this.plugin.getConfigManager().isSelectionIgnoreY()) {
            return;
        }

        if (area.getZSize() > group.getMaxZ()) {
            return;
        }

        if (changed) {
            this.plugin.getSelectionManager().placeLoc1(player, hloc, false);
            this.plugin.getSelectionManager().placeLoc2(player, lloc, true);
            this.plugin.getSelectionManager().showSelectionInfoInActionBar(player);
        }
    }

    public HashMap<String, AutoSelector> getList() {
        return this.list;
    }
}
