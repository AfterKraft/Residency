package com.gabizou.residency.selection;

import com.gabizou.residency.Residence;
import com.gabizou.residency.containers.ResidencePlayer;
import com.gabizou.residency.containers.SelectionSides;
import com.gabizou.residency.containers.Visualizer;
import com.gabizou.residency.containers.lm;
import com.gabizou.residency.permissions.PermissionGroup;
import com.gabizou.residency.protection.ClaimedResidence;
import com.gabizou.residency.protection.CuboidArea;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SelectionManager {

    public static final int MIN_HEIGHT = 0;
    protected Map<UUID, Selection> selections;
    protected Server server;
    protected Residence plugin;
    Permission ignoreyPermission = new Permission("residence.bypass.ignorey", PermissionDefault.FALSE);
    Permission ignoreyinsubzonePermission = new Permission("residence.bypass.ignoreyinsubzone", PermissionDefault.FALSE);
    private HashMap<UUID, Visualizer> vMap = new HashMap<UUID, Visualizer>();

    public SelectionManager(Server server, Residence plugin) {
        this.plugin = plugin;
        this.server = server;
        this.selections = Collections.synchronizedMap(new HashMap<UUID, Selection>());
    }

    private static Direction getDirection(Player player) {

        int yaw = (int) player.getLocation().getYaw();

        if (yaw < 0) {
            yaw += 360;
        }

        yaw += 45;
        yaw %= 360;

        int facing = yaw / 90;

        float pitch = player.getLocation().getPitch();
        if (pitch < -50) {
            return Direction.UP;
        }
        if (pitch > 50) {
            return Direction.DOWN;
        }
        if (facing == 1) // east
        {
            return Direction.MINUSX;
        }
        if (facing == 3) // west
        {
            return Direction.PLUSX;
        }
        if (facing == 2) // north
        {
            return Direction.MINUSZ;
        }
        if (facing == 0) // south
        {
            return Direction.PLUSZ;
        }
        return null;
    }

    public void updateLocations(Player player) {
        Selection s = this.selections.get(player.getUniqueId());
        if (s != null) {
            updateLocations(player, this.getSelection(player).getBaseLoc1(), this.getSelection(player).getBaseLoc2(), true);
        }
    }

    public void updateLocations(Player player, Location loc1, Location loc2) {
        updateLocations(player, loc1, loc2, false);
    }

    public void updateLocations(Player player, Location loc1, Location loc2, boolean force) {
        if (loc1 != null && loc2 != null) {
            Selection selection = getSelection(player);
            selection.setBaseLoc1(loc1);
            selection.setBaseLoc2(loc2);
            this.afterSelectionUpdate(player, force);
        }
    }

    public void placeLoc1(Player player, Location loc) {
        placeLoc1(player, loc, false);
    }

    public void placeLoc1(Player player, Location loc, boolean show) {
        if (loc != null) {
            getSelection(player).setBaseLoc1(loc);
            if (show) {
                this.afterSelectionUpdate(player);
            }
        }
    }

    public void placeLoc2(Player player, Location loc) {
        placeLoc2(player, loc, false);
    }

    public void placeLoc2(Player player, Location loc, boolean show) {
        if (loc != null) {
            getSelection(player).setBaseLoc2(loc);
            if (show) {
                this.afterSelectionUpdate(player);
            }
        }
    }

    public void afterSelectionUpdate(Player player) {
        afterSelectionUpdate(player, false);
    }

    public void afterSelectionUpdate(Player player, boolean force) {
        if (!hasPlacedBoth(player)) {
            return;
        }

        Visualizer v = this.vMap.get(player.getUniqueId());
        if (v == null) {
            v = new Visualizer(player);
            this.vMap.put(player.getUniqueId(), v);
        }
        v.setStart(System.currentTimeMillis());
        v.cancelAll();
        if (force) {
            v.setLoc(null);
        }
        v.setAreas(this.getSelectionCuboid(player));
        this.showBounds(player, v);
    }

    public Location getPlayerLoc1(Player player) {
        if (player == null) {
            return null;
        }
        return getSelection(player).getResizedArea().getLowLoc();
    }

    public Selection getSelection(Player player) {
        Selection s = this.selections.get(player.getUniqueId());
        if (s == null) {
            s = new Selection(player);
            this.selections.put(player.getUniqueId(), s);
        }
        return s;
    }

    @Deprecated
    public Location getPlayerLoc1(String player) {
        return getPlayerLoc1(Bukkit.getPlayer(player));
    }

    public Location getPlayerLoc2(Player player) {
        if (player == null) {
            return null;
        }
        return getSelection(player).getResizedArea().getHighLoc();
    }

    @Deprecated
    public Location getPlayerLoc2(String player) {
        return getPlayerLoc2(Bukkit.getPlayer(player));
    }

    @Deprecated
    public CuboidArea getSelectionCuboid(String player) {
        if (!hasPlacedBoth(player)) {
            return null;
        }
        return getSelectionCuboid(Bukkit.getPlayer(player));
    }

    @Deprecated
    public boolean hasPlacedBoth(String player) {
        return hasPlacedBoth(Bukkit.getPlayer(player));
    }

    public void showSelectionInfoInActionBar(Player player) {

        if (!this.plugin.getConfigManager().useActionBarOnSelection()) {
            return;
        }

        CuboidArea cuboidArea = this.getSelectionCuboid(player);

        String Message = this.plugin.msg(lm.Select_TotalSize, cuboidArea.getSize());

        ResidencePlayer rPlayer = this.plugin.getPlayerManager().getResidencePlayer(player);
        PermissionGroup group = rPlayer.getGroup();
        if (this.plugin.getConfigManager().enableEconomy()) {
            Message += " " + this.plugin.msg(lm.General_LandCost, ((int) Math.ceil(cuboidArea.getSize() * group.getCostPerBlock())));
        }

        this.plugin.getAB().send(player, Message);

    }

    public CuboidArea getSelectionCuboid(Player player) {
        if (player == null) {
            return null;
        }
        return getSelection(player).getResizedArea();
    }

    public void showSelectionInfo(Player player) {
        if (hasPlacedBoth(player)) {
            this.plugin.msg(player, lm.General_Separator);
            CuboidArea cuboidArea = this.getSelectionCuboid(player);
            this.plugin.msg(player, lm.Select_TotalSize, cuboidArea.getSize());

            ResidencePlayer rPlayer = this.plugin.getPlayerManager().getResidencePlayer(player);
            PermissionGroup group = rPlayer.getGroup();

            if (this.plugin.getConfigManager().enableEconomy()) {
                this.plugin.msg(player, lm.General_LandCost, ((int) Math.ceil(cuboidArea.getSize() * group.getCostPerBlock())));
            }
            player.sendMessage(ChatColor.YELLOW + "X" + this.plugin.msg(lm.General_Size, cuboidArea.getXSize()));
            player.sendMessage(ChatColor.YELLOW + "Y" + this.plugin.msg(lm.General_Size, cuboidArea.getYSize()));
            player.sendMessage(ChatColor.YELLOW + "Z" + this.plugin.msg(lm.General_Size, cuboidArea.getZSize()));
            this.plugin.msg(player, lm.General_Separator);
            Visualizer v = new Visualizer(player);
            v.setAreas(this.getSelectionCuboid(player));
            this.showBounds(player, v);
        } else {
            this.plugin.msg(player, lm.Select_Points);
        }
    }

    public void showBounds(final Player player, final Visualizer v) {
        if (!this.plugin.getConfigManager().useVisualizer()) {
            return;
        }
        Visualizer tv = this.vMap.get(player.getUniqueId());
        if (tv != null) {
            tv.cancelAll();
        }

        this.vMap.put(player.getUniqueId(), v);
        v.setBaseShedId(Bukkit.getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
            @Override
            public void run() {
                if (!v.getAreas().isEmpty()) {
                    MakeBorders(player, false);
                }
                if (!v.getErrorAreas().isEmpty()) {
                    MakeBorders(player, true);
                }
                return;
            }
        }).getTaskId());
    }

    public List<Location> getLocations(Location lowLoc, Location loc, Double TX, Double TY, Double TZ, Double Range, boolean StartFromZero) {

        double eachCollumn = this.plugin.getConfigManager().getVisualizerRowSpacing();
        double eachRow = this.plugin.getConfigManager().getVisualizerCollumnSpacing();

        if (TX == 0D) {
            TX = eachCollumn + eachCollumn * 0.1;
        }
        if (TY == 0D) {
            TY = eachRow + eachRow * 0.1;
        }
        if (TZ == 0D) {
            TZ = eachCollumn + eachCollumn * 0.1;
        }

        double CollumnStart = eachCollumn;
        double RowStart = eachRow;

        if (StartFromZero) {
            CollumnStart = 0;
            RowStart = 0;
        }

        List<Location> locList = new ArrayList<Location>();

        if (lowLoc.getWorld() != loc.getWorld()) {
            return locList;
        }

        for (double x = CollumnStart; x < TX; x += eachCollumn) {
            Location CurrentX = lowLoc.clone();
            if (TX > eachCollumn + eachCollumn * 0.1) {
                CurrentX.add(x, 0, 0);
            }
            for (double y = RowStart; y < TY; y += eachRow) {
                Location CurrentY = CurrentX.clone();
                if (TY > eachRow + eachRow * 0.1) {
                    CurrentY.add(0, y, 0);
                }
                for (double z = CollumnStart; z < TZ; z += eachCollumn) {
                    Location CurrentZ = CurrentY.clone();
                    if (TZ > eachCollumn + eachCollumn * 0.1) {
                        CurrentZ.add(0, 0, z);
                    }
                    double dist = loc.distance(CurrentZ);
                    if (dist < Range) {
                        locList.add(CurrentZ.clone());
                    }
                }
            }
        }

        return locList;
    }

    public List<Location> GetLocationsWallsByData(Location loc, Double TX, Double TY, Double TZ, Location lowLoc, SelectionSides Sides,
        double Range) {
        List<Location> locList = new ArrayList<Location>();

        // North wall
        if (Sides.ShowNorthSide()) {
            locList.addAll(getLocations(lowLoc.clone(), loc.clone(), TX, TY, 0D, Range, false));
        }

        // South wall
        if (Sides.ShowSouthSide()) {
            locList.addAll(getLocations(lowLoc.clone().add(0, 0, TZ), loc.clone(), TX, TY, 0D, Range, false));
        }

        // West wall
        if (Sides.ShowWestSide()) {
            locList.addAll(getLocations(lowLoc.clone(), loc.clone(), 0D, TY, TZ, Range, false));
        }

        // East wall
        if (Sides.ShowEastSide()) {
            locList.addAll(getLocations(lowLoc.clone().add(TX, 0, 0), loc.clone(), 0D, TY, TZ, Range, false));
        }

        // Roof wall
        if (Sides.ShowTopSide()) {
            locList.addAll(getLocations(lowLoc.clone().add(0, TY, 0), loc.clone(), TX, 0D, TZ, Range, false));
        }

        // Ground wall
        if (Sides.ShowBottomSide()) {
            locList.addAll(getLocations(lowLoc.clone(), loc.clone(), TX, 0D, TZ, Range, false));
        }

        return locList;
    }

    public List<Location> GetLocationsCornersByData(Location loc, Double TX, Double TY, Double TZ, Location lowLoc, SelectionSides Sides,
        double Range) {
        List<Location> locList = new ArrayList<Location>();

        // North bottom line
        if (Sides.ShowBottomSide() && Sides.ShowNorthSide()) {
            locList.addAll(getLocations(lowLoc.clone(), loc.clone(), TX, 0D, 0D, Range, true));
        }

        // North top line
        if (Sides.ShowTopSide() && Sides.ShowNorthSide()) {
            locList.addAll(getLocations(lowLoc.clone().add(0, TY, 0), loc.clone(), TX, 0D, 0D, Range, true));
        }

        // South bottom line
        if (Sides.ShowBottomSide() && Sides.ShowSouthSide()) {
            locList.addAll(getLocations(lowLoc.clone().add(0, 0, TZ), loc.clone(), TX, 0D, 0D, Range, true));
        }

        // South top line
        if (Sides.ShowTopSide() && Sides.ShowSouthSide()) {
            locList.addAll(getLocations(lowLoc.clone().add(0, TY, TZ), loc.clone(), TX, 0D, 0D, Range, true));
        }

        // North - West corner
        if (Sides.ShowWestSide() && Sides.ShowNorthSide()) {
            locList.addAll(getLocations(lowLoc.clone().add(0, 0, 0), loc.clone(), 0D, TY, 0D, Range, true));
        }

        // North - East corner
        if (Sides.ShowEastSide() && Sides.ShowNorthSide()) {
            locList.addAll(getLocations(lowLoc.clone().add(TX, 0, 0), loc.clone(), 0D, TY, 0D, Range, true));
        }

        // South - West corner
        if (Sides.ShowSouthSide() && Sides.ShowWestSide()) {
            locList.addAll(getLocations(lowLoc.clone().add(0, 0, TZ), loc.clone(), 0D, TY, 0D, Range, true));
        }

        // South - East corner
        if (Sides.ShowSouthSide() && Sides.ShowEastSide()) {
            locList.addAll(getLocations(lowLoc.clone().add(TX, 0, TZ), loc.clone(), 0D, TY + 1, 0D, Range, true));
        }

        // West bottom corner
        if (Sides.ShowWestSide() && Sides.ShowBottomSide()) {
            locList.addAll(getLocations(lowLoc.clone().add(0, 0, 0), loc.clone(), 0D, 0D, TZ, Range, true));
        }

        // East bottom corner
        if (Sides.ShowEastSide() && Sides.ShowBottomSide()) {
            locList.addAll(getLocations(lowLoc.clone().add(TX, 0, 0), loc.clone(), 0D, 0D, TZ, Range, true));
        }

        // West top corner
        if (Sides.ShowWestSide() && Sides.ShowTopSide()) {
            locList.addAll(getLocations(lowLoc.clone().add(0, TY, 0), loc.clone(), 0D, 0D, TZ, Range, true));
        }

        // East top corner
        if (Sides.ShowEastSide() && Sides.ShowTopSide()) {
            locList.addAll(getLocations(lowLoc.clone().add(TX, TY, 0), loc.clone(), 0D, 0D, TZ, Range, true));
        }

        return locList;
    }

    public boolean MakeBorders(final Player player, final boolean error) {

        final Visualizer v = this.vMap.get(player.getUniqueId());

        if (v == null) {
            return false;
        }

        List<CuboidArea> areas = null;

        if (!error) {
            areas = v.getAreas();
        } else {
            areas = v.getErrorAreas();
        }

        Location loc = player.getLocation();
        int Range = this.plugin.getConfigManager().getVisualizerRange();

        final List<Location> locList = new ArrayList<Location>();
        final List<Location> errorLocList = new ArrayList<Location>();

        final boolean same = v.isSameLoc();
        if (!same) {
            for (CuboidArea area : areas) {
                if (area == null) {
                    continue;
                }
                CuboidArea cuboidArea = new CuboidArea(area.getLowLoc(), area.getHighLoc());
                cuboidArea.getHighLoc().add(1, 1, 1);

                SelectionSides Sides = new SelectionSides();

                double PLLX = loc.getBlockX() - Range;
                double PLLZ = loc.getBlockZ() - Range;
                double PLLY = loc.getBlockY() - Range;
                double PLHX = loc.getBlockX() + Range;
                double PLHZ = loc.getBlockZ() + Range;
                double PLHY = loc.getBlockY() + Range;

                if (cuboidArea.getLowLoc().getBlockX() < PLLX) {
                    cuboidArea.getLowLoc().setX(PLLX);
                    Sides.setWestSide(false);
                }

                if (cuboidArea.getHighLoc().getBlockX() > PLHX) {
                    cuboidArea.getHighLoc().setX(PLHX);
                    Sides.setEastSide(false);
                }

                if (cuboidArea.getLowLoc().getBlockZ() < PLLZ) {
                    cuboidArea.getLowLoc().setZ(PLLZ);
                    Sides.setNorthSide(false);
                }

                if (cuboidArea.getHighLoc().getBlockZ() > PLHZ) {
                    cuboidArea.getHighLoc().setZ(PLHZ);
                    Sides.setSouthSide(false);
                }

                if (cuboidArea.getLowLoc().getBlockY() < PLLY) {
                    cuboidArea.getLowLoc().setY(PLLY);
                    Sides.setBottomSide(false);
                }

                if (cuboidArea.getHighLoc().getBlockY() > PLHY) {
                    cuboidArea.getHighLoc().setY(PLHY);
                    Sides.setTopSide(false);
                }

                double TX = cuboidArea.getXSize() - 1;
                double TY = cuboidArea.getYSize() - 1;
                double TZ = cuboidArea.getZSize() - 1;

                if (!error && v.getId() != -1) {
                    Bukkit.getScheduler().cancelTask(v.getId());
                } else if (error && v.getErrorId() != -1) {
                    Bukkit.getScheduler().cancelTask(v.getErrorId());
                }

                locList.addAll(GetLocationsWallsByData(loc, TX, TY, TZ, cuboidArea.getLowLoc().clone(), Sides, Range));
                errorLocList.addAll(GetLocationsCornersByData(loc, TX, TY, TZ, cuboidArea.getLowLoc().clone(), Sides, Range));
            }
            v.setLoc(player.getLocation());
        } else {
            if (error) {
                locList.addAll(v.getErrorLocations());
                errorLocList.addAll(v.getErrorLocations2());
            } else {
                locList.addAll(v.getLocations());
                errorLocList.addAll(v.getLocations2());
            }
        }

        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
            @Override
            public void run() {

                int size = locList.size();
                int errorSize = errorLocList.size();

                int timesMore = 1;
                int errorTimesMore = 1;

                if (size > SelectionManager.this.plugin.getConfigManager().getVisualizerSidesCap()) {
                    timesMore = size / SelectionManager.this.plugin.getConfigManager().getVisualizerSidesCap() + 1;
                }
                if (errorSize > SelectionManager.this.plugin.getConfigManager().getVisualizerFrameCap()) {
                    errorTimesMore = errorSize / SelectionManager.this.plugin.getConfigManager().getVisualizerFrameCap() + 1;
                }

                v.addCurrentSkip();
                if (v.getCurrentSkip() > SelectionManager.this.plugin.getConfigManager().getVisualizerSkipBy()) {
                    v.setCurrentSkip(1);
                }

                try {
                    showParticles(locList, player, timesMore, error, true, v.getCurrentSkip());
                    showParticles(errorLocList, player, errorTimesMore, error, false, v.getCurrentSkip());
                } catch (Exception e) {
                    return;
                }

                if (error) {
                    v.setErrorLocations(locList);
                    v.setErrorLocations2(errorLocList);
                } else {
                    v.setLocations(locList);
                    v.setLocations2(errorLocList);
                }

                return;
            }
        });

        if (v.isOnce()) {
            return true;
        }

        if (v.getStart() + this.plugin.getConfigManager().getVisualizerShowFor() < System.currentTimeMillis()) {
            return false;
        }

        int scid = Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
            @Override
            public void run() {
                if (player.isOnline()) {
                    MakeBorders(player, error);
                }
                return;
            }
        }, this.plugin.getConfigManager().getVisualizerUpdateInterval() * 1L);
        if (!error) {
            v.setId(scid);
        } else {
            v.setErrorId(scid);
        }

        return true;

    }

    private void showParticles(List<Location> locList, Player player, int timesMore, boolean error, boolean sides, int currentSkipBy) {
        int s = 0;

        for (int i = 0; i < locList.size(); i += timesMore) {
            s++;

            if (s > this.plugin.getConfigManager().getVisualizerSkipBy()) {
                s = 1;
            }

            if (s != currentSkipBy) {
                continue;
            }

            Location l = locList.get(i);
            if (this.plugin.isSpigot()) {
                Effect effect = null;
                if (sides) {
                    effect = error ? this.plugin.getConfigManager().getOverlapSpigotSides() : this.plugin.getConfigManager().getSelectedSpigotSides();
                } else {
                    effect = error ? this.plugin.getConfigManager().getOverlapSpigotFrame() : this.plugin.getConfigManager().getSelectedSpigotFrame();
                }
                player.spigot().playEffect(l, effect, 0, 0, 0, 0, 0, 0, 1, 128);
            } else {
                if (error) {
                    if (sides) {
                        this.plugin.getConfigManager().getOverlapSides().display(0, 0, 0, 0, 1, l, player);
                    } else {
                        this.plugin.getConfigManager().getOverlapFrame().display(0, 0, 0, 0, 1, l, player);
                    }
                } else {
                    if (sides) {
                        this.plugin.getConfigManager().getSelectedSides().display(0, 0, 0, 0, 1, l, player);
                    } else {
                        this.plugin.getConfigManager().getSelectedFrame().display(0, 0, 0, 0, 1, l, player);
                    }
                }
            }

//	    if (!sameLocation)
//		trimed.add(l);
        }
    }

    public void vert(Player player, boolean resadmin) {
        if (hasPlacedBoth(player)) {
            this.sky(player, resadmin);
            this.bedrock(player, resadmin);
        } else {
            this.plugin.msg(player, lm.Select_Points);
        }
    }

    public boolean hasPlacedBoth(Player player) {
        if (player == null) {
            return false;
        }
        return getSelection(player).hasPlacedBoth();
    }

    public void sky(Player player, boolean resadmin) {
        Selection selection = this.getSelection(player);
        selection.sky(resadmin);
    }

    public void bedrock(Player player, boolean resadmin) {
        Selection selection = this.getSelection(player);
        selection.bedrock(resadmin);
    }

//    @Deprecated
//    public void qsky(Player player) {
//	Selection selection = this.getSelection(player);
//	selection.shadowSky();
//    }
//
//    @Deprecated
//    public void qbedrock(Player player) {
//	Selection selection = this.getSelection(player);
//	selection.shadowBedrock();
//    }

    public void clearSelection(Player player) {
        this.selections.remove(player.getUniqueId());
    }

    @Deprecated
    public void selectChunk(Player player) {
        Selection selection = this.getSelection(player);
        selection.selectChunk();
    }

    public boolean worldEdit(Player player) {
        this.plugin.msg(player, lm.General_WorldEditNotFound);
        return false;
    }

    public boolean worldEditUpdate(Player player) {
        this.plugin.msg(player, lm.General_WorldEditNotFound);
        return false;
    }

    public void selectBySize(Player player, int xsize, int ysize, int zsize) {
        Location myloc = player.getLocation();
        Location loc1 = new Location(myloc.getWorld(), myloc.getBlockX() + xsize, myloc.getBlockY() + ysize, myloc.getBlockZ() + zsize);
        Location loc2 = new Location(myloc.getWorld(), myloc.getBlockX() - xsize, myloc.getBlockY() - ysize, myloc.getBlockZ() - zsize);

        CuboidArea area = new CuboidArea(loc1, loc2);

//	area.getlo

        placeLoc1(player, loc1, false);
        placeLoc2(player, loc2, false);

        Selection selection = this.getSelection(player);

        if (selection.getMaxYAllowed() < area.getHighLoc().getBlockY()) {
            selection.getBaseLoc2().setY(selection.getMaxYAllowed());
        }

        if (selection.getMinYAllowed() > area.getLowLoc().getBlockY()) {
            selection.getBaseLoc1().setY(selection.getMinYAllowed());
        }

//	selection.updateBaseArea();
//	selection.updateShadowLocations();
//	selection.updateShadowArea();

        this.afterSelectionUpdate(player);
        this.plugin.msg(player, lm.Select_Success);
        showSelectionInfo(player);
    }

    public void modify(Player player, boolean shift, double amount) {
        if (!hasPlacedBoth(player)) {
            this.plugin.msg(player, lm.Select_Points);
            return;
        }
        Direction d = getDirection(player);
        if (d == null) {
            this.plugin.msg(player, lm.Invalid_Direction);
            return;
        }
        CuboidArea area = this.getSelectionCuboid(player);
        switch (d) {
            case DOWN:
                double oldy = area.getLowLoc().getBlockY();
                oldy = oldy - amount;
                if (oldy < MIN_HEIGHT) {
                    this.plugin.msg(player, lm.Select_TooLow);
                    oldy = MIN_HEIGHT;
                }
                area.getLowLoc().setY(oldy);
                if (shift) {
                    double oldy2 = area.getHighLoc().getBlockY();
                    oldy2 = oldy2 - amount;
                    area.getHighLoc().setY(oldy2);
                    this.plugin.msg(player, lm.Shifting_Down, amount);
                } else {
                    this.plugin.msg(player, lm.Expanding_Down, amount);
                }
                break;
            case MINUSX:
                double oldx = area.getLowLoc().getBlockX();
                oldx = oldx - amount;
                area.getLowLoc().setX(oldx);
                if (shift) {
                    double oldx2 = area.getHighLoc().getBlockX();
                    oldx2 = oldx2 - amount;
                    area.getHighLoc().setX(oldx2);
                    this.plugin.msg(player, lm.Shifting_West, amount);
                } else {
                    this.plugin.msg(player, lm.Expanding_West, amount);
                }
                break;
            case MINUSZ:
                double oldz = area.getLowLoc().getBlockZ();
                oldz = oldz - amount;
                area.getLowLoc().setZ(oldz);
                if (shift) {
                    double oldz2 = area.getHighLoc().getBlockZ();
                    oldz2 = oldz2 - amount;
                    area.getHighLoc().setZ(oldz2);
                    this.plugin.msg(player, lm.Shifting_North, amount);
                } else {
                    this.plugin.msg(player, lm.Expanding_North, amount);
                }
                break;
            case PLUSX:
                oldx = area.getHighLoc().getBlockX();
                oldx = oldx + amount;
                area.getHighLoc().setX(oldx);
                if (shift) {
                    double oldx2 = area.getLowLoc().getBlockX();
                    oldx2 = oldx2 + amount;
                    area.getLowLoc().setX(oldx2);
                    this.plugin.msg(player, lm.Shifting_East, amount);
                } else {
                    this.plugin.msg(player, lm.Expanding_East, amount);
                }
                break;
            case PLUSZ:
                oldz = area.getHighLoc().getBlockZ();
                oldz = oldz + amount;
                area.getHighLoc().setZ(oldz);
                if (shift) {
                    double oldz2 = area.getLowLoc().getBlockZ();
                    oldz2 = oldz2 + amount;
                    area.getLowLoc().setZ(oldz2);
                    this.plugin.msg(player, lm.Shifting_South, amount);
                } else {
                    this.plugin.msg(player, lm.Expanding_South, amount);
                }
                break;
            case UP:
                oldy = area.getHighLoc().getBlockY();
                oldy = oldy + amount;
                if (oldy > player.getLocation().getWorld().getMaxHeight() - 1) {
                    this.plugin.msg(player, lm.Select_TooHigh);
                    oldy = player.getLocation().getWorld().getMaxHeight() - 1;
                }
                area.getHighLoc().setY(oldy);
                if (shift) {
                    double oldy2 = area.getLowLoc().getBlockY();
                    oldy2 = oldy2 + amount;
                    area.getLowLoc().setY(oldy2);
                    this.plugin.msg(player, lm.Shifting_Up, amount);
                } else {
                    this.plugin.msg(player, lm.Expanding_Up, amount);
                }
                break;
            default:
                break;
        }
        updateLocations(player, area.getHighLoc(), area.getLowLoc(), true);
    }

    public boolean contract(Player player, double amount) {
        return contract(player, amount, false);
    }

    public boolean contract(Player player, double amount, @SuppressWarnings("unused") boolean resadmin) {
        if (!hasPlacedBoth(player)) {
            this.plugin.msg(player, lm.Select_Points);
            return false;
        }
        Direction d = getDirection(player);
        if (d == null) {
            this.plugin.msg(player, lm.Invalid_Direction);
            return false;
        }
        CuboidArea area = this.getSelectionCuboid(player);
        switch (d) {
            case UP:
                double oldy = area.getHighLoc().getBlockY();
                oldy = oldy - amount;
                if (oldy > player.getLocation().getWorld().getMaxHeight() - 1) {
                    this.plugin.msg(player, lm.Select_TooHigh);
                    oldy = player.getLocation().getWorld().getMaxHeight() - 1;
                }
                area.getHighLoc().setY(oldy);
                this.plugin.msg(player, lm.Contracting_Down, amount);
                break;
            case PLUSX:
                double oldx = area.getHighLoc().getBlockX();
                oldx = oldx - amount;
                area.getHighLoc().setX(oldx);
                this.plugin.msg(player, lm.Contracting_West, amount);
                break;
            case PLUSZ:
                double oldz = area.getHighLoc().getBlockZ();
                oldz = oldz - amount;
                area.getHighLoc().setZ(oldz);
                this.plugin.msg(player, lm.Contracting_North, amount);
                break;
            case MINUSX:
                oldx = area.getLowLoc().getBlockX();
                oldx = oldx + amount;
                area.getLowLoc().setX(oldx);
                this.plugin.msg(player, lm.Contracting_East, amount);
                break;
            case MINUSZ:
                oldz = area.getLowLoc().getBlockZ();
                oldz = oldz + amount;
                area.getLowLoc().setZ(oldz);
                this.plugin.msg(player, lm.Contracting_South, amount);
                break;
            case DOWN:
                oldy = area.getLowLoc().getBlockY();
                oldy = oldy + amount;
                if (oldy < MIN_HEIGHT) {
                    this.plugin.msg(player, lm.Select_TooLow);
                    oldy = MIN_HEIGHT;
                }
                area.getLowLoc().setY(oldy);
                this.plugin.msg(player, lm.Contracting_Up, amount);
                break;
            default:
                break;
        }

//	if (!ClaimedResidence.isBiggerThanMinSubzone(player, area, resadmin))
//	    return false;

        updateLocations(player, area.getHighLoc(), area.getLowLoc(), true);
        return true;
    }

    public enum selectionType {
        noLimits, ignoreY, residenceBounds;
    }

    public enum Direction {
        UP, DOWN, PLUSX, PLUSZ, MINUSX, MINUSZ
    }

    public class Selection {

        private Player player;
        private Location loc1;
        private Location loc2;

        public Selection(Player player) {
            this.player = player;
        }

        public Location getBaseLoc1() {
            return this.loc1 == null ? null : this.loc1.clone();
        }

        public void setBaseLoc1(Location loc1) {
            this.loc1 = loc1.clone();
        }

        public World getWorld() {
            if (this.loc1 != null) {
                return this.loc1.getWorld();
            }
            if (this.loc2 != null) {
                return this.loc2.getWorld();
            }
            return this.player.getWorld();
        }

        public Location getBaseLoc2() {
            return this.loc2 == null ? null : this.loc2.clone();
        }

        public void setBaseLoc2(Location loc2) {
            this.loc2 = loc2.clone();
        }

        public selectionType getSelectionRestrictions() {
            if (inSameResidence()) {
                if (SelectionManager.this.plugin.getConfigManager().isSelectionIgnoreYInSubzone()) {
                    if (hasPlacedBoth() && !this.player.hasPermission(SelectionManager.this.ignoreyinsubzonePermission)) {
                        return selectionType.residenceBounds;
                    }
                }
            } else {
                if (SelectionManager.this.plugin.getConfigManager().isSelectionIgnoreY()) {
                    if (hasPlacedBoth() && !this.player.hasPermission(SelectionManager.this.ignoreyPermission)) {
                        return selectionType.ignoreY;
                    }
                }
            }
            return selectionType.noLimits;
        }

        public int getMaxYAllowed() {
            switch (getSelectionRestrictions()) {
                case ignoreY:
                case noLimits:
                default:
                    return getWorld().getMaxHeight() - 1;
                case residenceBounds:
                    ClaimedResidence res1 = SelectionManager.this.plugin.getResidenceManager().getByLoc(this.getBaseLoc2());
                    if (res1 != null) {
                        CuboidArea area = res1.getAreaByLoc(this.getBaseLoc2());
                        if (area != null) {
                            return area.getHighLoc().getBlockY();
                        }
                    }
                    break;
            }
            return getWorld().getMaxHeight() - 1;
        }

        public int getMinYAllowed() {
            switch (getSelectionRestrictions()) {
                case ignoreY:
                case noLimits:
                default:
                    return 0;
                case residenceBounds:
                    ClaimedResidence res1 = SelectionManager.this.plugin.getResidenceManager().getByLoc(this.getBaseLoc1());
                    if (res1 != null) {
                        CuboidArea area = res1.getAreaByLoc(getBaseLoc1());
                        if (area != null) {
                            return area.getLowLoc().getBlockY();
                        }
                    }
                    break;
            }
            return 0;
        }

        private boolean inSameResidence() {
            if (!hasPlacedBoth()) {
                return false;
            }

            ClaimedResidence res1 = SelectionManager.this.plugin.getResidenceManager().getByLoc(this.getBaseLoc1());

            if (res1 == null) {
                return false;
            }

            ClaimedResidence res2 = SelectionManager.this.plugin.getResidenceManager().getByLoc(this.getBaseLoc2());

            if (res2 == null) {
                return false;
            }
            return res1.getName().equals(res2.getName());
        }

        public void vert(boolean resadmin) {
            if (hasPlacedBoth()) {
                sky(resadmin);
                bedrock(resadmin);
            } else {
                SelectionManager.this.plugin.msg(this.player, lm.Select_Points);
            }
        }

        private void shadowSky(CuboidArea area) {
            if (!hasPlacedBoth()) {
                return;
            }
            area.setHighLocation(this.getBaseArea().getHighLoc());
            area.getHighLoc().setY(this.getMaxYAllowed());
        }

        private void shadowBedrock(CuboidArea area) {
            if (!hasPlacedBoth()) {
                return;
            }
            area.setLowLocation(this.getBaseArea().getLowLoc());
            area.getLowLoc().setY(this.getMinYAllowed());
        }

        public void sky(boolean resadmin) {
            if (hasPlacedBoth()) {
                ResidencePlayer rPlayer = SelectionManager.this.plugin.getPlayerManager().getResidencePlayer(this.getPlayer());
                PermissionGroup group = rPlayer.getGroup();
                int y1 = this.getBaseLoc1().getBlockY();

                int newy = this.getMaxYAllowed();

                if (!resadmin) {
                    if (group.getMaxHeight() < newy) {
                        newy = group.getMaxHeight();
                    }
                    if (newy - y1 > (group.getMaxY() - 1)) {
                        newy = y1 + (group.getMaxY() - 1);
                    }
                }
                this.getBaseLoc2().setY(newy);

                SelectionManager.this.plugin.msg(this.player, lm.Select_Sky);
            } else {
                SelectionManager.this.plugin.msg(this.player, lm.Select_Points);
            }
        }

        public void bedrock(boolean resadmin) {
            if (hasPlacedBoth()) {
                ResidencePlayer rPlayer = SelectionManager.this.plugin.getPlayerManager().getResidencePlayer(this.getPlayer());
                PermissionGroup group = rPlayer.getGroup();

                int y2 = this.getBaseLoc2().getBlockY();

                int newy = this.getMinYAllowed();
                if (!resadmin) {
                    if (newy < group.getMinHeight()) {
                        newy = group.getMinHeight();
                    }
                    if (y2 - newy > (group.getMaxY() - 1)) {
                        newy = y2 - (group.getMaxY() - 1);
                    }
                }
                this.getBaseLoc1().setY(newy);
                SelectionManager.this.plugin.msg(this.player, lm.Select_Bedrock);
            } else {
                SelectionManager.this.plugin.msg(this.player, lm.Select_Points);
            }
        }

        public void selectChunk() {
            Chunk chunk = this.player.getWorld().getChunkAt(this.player.getLocation());
            int xcoord = chunk.getX() * 16;
            int zcoord = chunk.getZ() * 16;
            int xmax = xcoord + 15;
            int zmax = zcoord + 15;
            this.setBaseLoc1(new Location(this.player.getWorld(), xcoord, this.player.getLocation().getBlockY(), zcoord));
            this.setBaseLoc2(new Location(this.player.getWorld(), xmax, this.player.getLocation().getBlockY(), zmax));
            SelectionManager.this.plugin.msg(this.player, lm.Select_Success);
        }

        public boolean hasPlacedBoth() {
            return this.getBaseLoc1() != null && this.getBaseLoc2() != null;
        }

        public Player getPlayer() {
            return this.player;
        }

        public void setPlayer(Player player) {
            this.player = player;
        }

        public CuboidArea getBaseArea() {
            if (!this.hasPlacedBoth()) {
                return null;
            }
            return new CuboidArea(this.loc1, this.loc2);
        }

        public CuboidArea getResizedArea() {

            CuboidArea area = this.getBaseArea();
            switch (getSelectionRestrictions()) {
                case noLimits:
                    break;
                case residenceBounds:
                case ignoreY:
                    shadowSky(area);
                    shadowBedrock(area);
                    break;
                default:
                    break;
            }

            return area;
        }
    }

}
