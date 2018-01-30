package com.gabizou.residency.selection;

import com.gabizou.residency.Residence;
import com.gabizou.residency.containers.Visualizer;
import com.gabizou.residency.containers.lm;
import com.gabizou.residency.protection.CuboidArea;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class WorldGuardUtil {

    private Residence plugin;

    public WorldGuardUtil(Residence residence) {
        this.plugin = residence;
    }

    public boolean isSelectionInArea(Player player) {
        if (this.plugin.getWorldGuard() == null) {
            return false;
        }

        ProtectedRegion Region = getRegion(player, this.plugin.getSelectionManager().getSelectionCuboid(player));
        if (Region == null) {
            return false;
        }

        this.plugin.msg(player, lm.Select_WorldGuardOverlap, Region.getId());
        Location
            lowLoc =
            new Location(this.plugin.getSelectionManager().getPlayerLoc1(player.getName()).getWorld(), Region.getMinimumPoint().getBlockX(),
                Region.getMinimumPoint().getBlockY(), Region.getMinimumPoint().getBlockZ());
        Location
            highLoc =
            new Location(this.plugin.getSelectionManager().getPlayerLoc1(player.getName()).getWorld(), Region.getMaximumPoint().getBlockX(),
                Region.getMaximumPoint().getBlockY(), Region.getMaximumPoint().getBlockZ());
        Visualizer v = new Visualizer(player);
        v.setAreas(this.plugin.getSelectionManager().getSelectionCuboid(player));
        v.setErrorAreas(new CuboidArea(lowLoc, highLoc));
        this.plugin.getSelectionManager().showBounds(player, v);
        return true;
    }

    public ProtectedRegion getRegion(Player player, CuboidArea area) {

        if (area == null) {
            return null;
        }

        if (this.plugin.getWorldGuard() == null) {
            return null;
        }

        if (this.plugin.getWorldEdit() == null) {
            return null;
        }

        Location loc1 = area.getLowLoc();
        Location loc2 = area.getHighLoc();

        String id = "icp__tempregion";
        try {
            BlockVector min = new BlockVector(loc1.getX(), loc1.getY(), loc1.getZ());
            BlockVector max = new BlockVector(loc2.getX(), loc2.getY(), loc2.getZ());
            ProtectedRegion region = new ProtectedCuboidRegion(id, min, max);

            RegionManager mgr = this.plugin.getWorldGuard().getRegionManager(loc1.getWorld());

            ApplicableRegionSet regions = mgr.getApplicableRegions(region);

            for (ProtectedRegion one : regions.getRegions()) {
                if (!player.hasPermission("residence.worldguard." + one.getId())) {
                    return one;
                }
            }
        } catch (Exception | IncompatibleClassChangeError e) {
        }
        return null;
    }
}
