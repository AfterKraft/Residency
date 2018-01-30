package com.gabizou.residency.shopStuff;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class Board {

    int StartPlace = 0;
    List<Location> Locations = new ArrayList<Location>();
    HashMap<String, Location> SignLocations = new HashMap<String, Location>();
    private Location TopLoc = null;
    private Location BottomLoc = null;

    public void clearSignLoc() {
        this.SignLocations.clear();
    }

    public void addSignLoc(String resName, Location loc) {
        this.SignLocations.put(resName, loc);
    }

    public HashMap<String, Location> getSignLocations() {
        return this.SignLocations;
    }

    public Location getSignLocByName(String resName) {
        return this.SignLocations.get(resName);
    }

    public String getResNameByLoc(Location location) {
        for (Entry<String, Location> One : this.SignLocations.entrySet()) {
            Location loc = One.getValue();
            if (!loc.getWorld().getName().equalsIgnoreCase(location.getWorld().getName())) {
                continue;
            }
            if (loc.getBlockX() != location.getBlockX()) {
                continue;
            }
            if (loc.getBlockY() != location.getBlockY()) {
                continue;
            }
            if (loc.getBlockZ() != location.getBlockZ()) {
                continue;
            }
            return One.getKey();
        }
        return null;
    }

    public List<Location> GetLocations() {
        this.Locations.clear();

        if (this.TopLoc == null || this.BottomLoc == null) {
            return null;
        }

        if (this.TopLoc.getWorld() == null) {
            return null;
        }

        int xLength = this.TopLoc.getBlockX() - this.BottomLoc.getBlockX();
        int yLength = this.TopLoc.getBlockY() - this.BottomLoc.getBlockY();
        int zLength = this.TopLoc.getBlockZ() - this.BottomLoc.getBlockZ();

        if (xLength < 0) {
            xLength = xLength * -1;
        }
        if (zLength < 0) {
            zLength = zLength * -1;
        }

        for (int y = 0; y <= yLength; y++) {
            for (int x = 0; x <= xLength; x++) {
                for (int z = 0; z <= zLength; z++) {

                    int tempx = 0;
                    int tempz = 0;

                    if (this.TopLoc.getBlockX() > this.BottomLoc.getBlockX()) {
                        tempx = this.TopLoc.getBlockX() - x;
                    } else {
                        tempx = this.TopLoc.getBlockX() + x;
                    }

                    if (this.TopLoc.getBlockZ() > this.BottomLoc.getBlockZ()) {
                        tempz = this.TopLoc.getBlockZ() - z;
                    } else {
                        tempz = this.TopLoc.getBlockZ() + z;
                    }

                    this.Locations.add(new Location(this.TopLoc.getWorld(), tempx, this.TopLoc.getBlockY() - y, tempz));
                }
            }
        }

        return this.Locations;
    }

    public void setStartPlace(int StartPlace) {
        this.StartPlace = StartPlace;
    }

    public int GetStartPlace() {
        return this.StartPlace == 0 ? 0 : (this.StartPlace - 1);
    }

    public String GetWorld() {
        return this.TopLoc.getWorld().getName();
    }

    public Location getTopLoc() {
        return this.TopLoc;
    }

    public void setTopLoc(Location topLoc) {
        this.TopLoc = topLoc;
    }

    public Location getBottomLoc() {
        return this.BottomLoc;
    }

    public void setBottomLoc(Location bottomLoc) {
        this.BottomLoc = bottomLoc;
    }
}
