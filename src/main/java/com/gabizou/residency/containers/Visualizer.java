package com.gabizou.residency.containers;

import com.gabizou.residency.protection.ClaimedResidence;
import com.gabizou.residency.protection.CuboidArea;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Visualizer {

    private Player player;
    private long start;
    private List<CuboidArea> areas = new ArrayList<CuboidArea>();
    private List<CuboidArea> errorAreas = new ArrayList<CuboidArea>();
    private int id = -1;
    private int errorId = -1;
    private int baseShedId = -1;
    private boolean once = false;
    private int starting = 0;
    private int currentSkip = 0;

    private List<Location> locations = new ArrayList<Location>();
    private List<Location> errorLocations = new ArrayList<Location>();
    private List<Location> locations2 = new ArrayList<Location>();
    private List<Location> errorLocations2 = new ArrayList<Location>();
    private Location loc = null;

    public Visualizer(Player player) {
        this.player = player;
        this.start = System.currentTimeMillis();
    }

    public void cancelAll() {
        if (this.id != -1) {
            Bukkit.getScheduler().cancelTask(this.id);
        }
        if (this.errorId != -1) {
            Bukkit.getScheduler().cancelTask(this.errorId);
        }
        if (this.baseShedId != -1) {
            Bukkit.getScheduler().cancelTask(this.baseShedId);
        }
    }

    public boolean isSameLoc() {
        if (this.loc == null) {
            return false;
        }
        if (this.loc.getWorld() != this.player.getWorld()) {
            return false;
        }
        if (!this.errorAreas.isEmpty() && this.errorLocations.isEmpty()) {
            return false;
        }
        if (this.loc.distance(this.player.getLocation()) > 1) {
            return false;
        }

        return true;
    }

    public long getStart() {
        return this.start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public Player getPlayer() {
        return this.player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public List<CuboidArea> getAreas() {
        return this.areas;
    }

    public void setAreas(CuboidArea area) {
        this.areas = new ArrayList<CuboidArea>();
        this.areas.add(area);
    }

    public void setAreas(ClaimedResidence res) {
        if (res != null) {
            this.areas = Arrays.asList(res.getAreaArray());
        }
    }

    public void setAreas(CuboidArea[] areas) {
        this.areas = Arrays.asList(areas);
    }

    public void setAreas(ArrayList<CuboidArea> areas) {
        this.areas = areas;
    }

    public List<CuboidArea> getErrorAreas() {
        return this.errorAreas;
    }

    public void setErrorAreas(CuboidArea errorArea) {
        this.errorAreas.add(errorArea);
    }

    public void setErrorAreas(ClaimedResidence res) {
        if (res != null) {
            this.errorAreas = Arrays.asList(res.getAreaArray());
        }
    }

    public void setErrorAreas(CuboidArea[] errorAreas) {
        this.errorAreas = Arrays.asList(errorAreas);
    }

    public void setErrorAreas(ArrayList<CuboidArea> errorAreas) {
        this.errorAreas = errorAreas;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getErrorId() {
        return this.errorId;
    }

    public void setErrorId(int errorId) {
        this.errorId = errorId;
    }

    public boolean isOnce() {
        return this.once;
    }

    public void setOnce(boolean once) {
        this.once = once;
    }

    public List<Location> getLocations() {
        return this.locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }

    public List<Location> getLocations2() {
        return this.locations2;
    }

    public void setLocations2(List<Location> locations) {
        this.locations2 = locations;
    }

    public List<Location> getErrorLocations() {
        return this.errorLocations;
    }

    public void setErrorLocations(List<Location> errorLocations) {
        this.errorLocations = errorLocations;
    }

    public List<Location> getErrorLocations2() {
        return this.errorLocations2;
    }

    public void setErrorLocations2(List<Location> errorLocations) {
        this.errorLocations2 = errorLocations;
    }

    public Location getLoc() {
        return this.loc;
    }

    public void setLoc(Location loc) {
        this.loc = loc;
    }

    public int getStarting() {
        return this.starting;
    }

    public void setStarting(int starting) {
        this.starting = starting;
    }

    public int getBaseShedId() {
        return this.baseShedId;
    }

    public void setBaseShedId(int baseShedId) {
        this.baseShedId = baseShedId;
    }

    public int getCurrentSkip() {
        return this.currentSkip;
    }

    public void setCurrentSkip(int currentSkip) {
        this.currentSkip = currentSkip;
    }

    public void addCurrentSkip() {
        this.currentSkip++;
    }
}
