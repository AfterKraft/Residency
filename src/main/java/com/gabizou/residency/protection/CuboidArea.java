package com.gabizou.residency.protection;

import com.gabizou.residency.Residence;
import com.gabizou.residency.protection.ResidenceManager.ChunkRef;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.spongepowered.api.Server;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CuboidArea {

    protected String worldName;
    private Location<World> highPoints;
    private Location<World> lowPoints;

    public CuboidArea(Location<World> startLoc, Location<World> endLoc) {
        int highx, highy, highz, lowx, lowy, lowz;
        if (startLoc.getBlockX() > endLoc.getBlockX()) {
            highx = startLoc.getBlockX();
            lowx = endLoc.getBlockX();
        } else {
            highx = endLoc.getBlockX();
            lowx = startLoc.getBlockX();
        }
        if (startLoc.getBlockY() > endLoc.getBlockY()) {
            highy = startLoc.getBlockY();
            lowy = endLoc.getBlockY();
        } else {
            highy = endLoc.getBlockY();
            lowy = startLoc.getBlockY();
        }
        if (startLoc.getBlockZ() > endLoc.getBlockZ()) {
            highz = startLoc.getBlockZ();
            lowz = endLoc.getBlockZ();
        } else {
            highz = endLoc.getBlockZ();
            lowz = startLoc.getBlockZ();
        }
        this.highPoints = new Location<>(startLoc.getExtent(), highx, highy, highz);
        this.lowPoints = new Location<>(startLoc.getExtent(), lowx, lowy, lowz);
        this.worldName = startLoc.getExtent().getName();
    }

    public CuboidArea() {
    }

    public static CuboidArea newLoad(String root, World world) throws Exception {
        if (root == null || !root.contains(":")) {
            throw new Exception("Invalid residence physical location...");
        }
        CuboidArea newArea = new CuboidArea();
        String[] split = root.split(":");
        try {
            int x1 = Integer.parseInt(split[0]);
            int y1 = Integer.parseInt(split[1]);
            int z1 = Integer.parseInt(split[2]);
            int x2 = Integer.parseInt(split[3]);
            int y2 = Integer.parseInt(split[4]);
            int z2 = Integer.parseInt(split[5]);
            newArea.lowPoints = new Location(world, x1, y1, z1);
            newArea.highPoints = new Location(world, x2, y2, z2);
            newArea.worldName = world.getName();
        } catch (Exception e) {
            throw new Exception("Invalid residence physical location...");
        }

        return newArea;
    }

    public static CuboidArea load(Map<String, Object> root, World world) throws Exception {
        if (root == null) {
            throw new Exception("Invalid residence physical location...");
        }
        CuboidArea newArea = new CuboidArea();
        int x1 = (Integer) root.get("X1");
        int y1 = (Integer) root.get("Y1");
        int z1 = (Integer) root.get("Z1");
        int x2 = (Integer) root.get("X2");
        int y2 = (Integer) root.get("Y2");
        int z2 = (Integer) root.get("Z2");
        newArea.highPoints = new Location(world, x1, y1, z1);
        newArea.lowPoints = new Location(world, x2, y2, z2);
        newArea.worldName = world.getName();
        return newArea;
    }

    public boolean isAreaWithinArea(CuboidArea area) {
        return (this.containsLoc(area.highPoints) && this.containsLoc(area.lowPoints));
    }

    public boolean containsLoc(Location loc) {
        if (loc == null) {
            return false;
        }

        if (!loc.getWorld().getName().equals(this.worldName)) {
            return false;
        }

        if (this.lowPoints.getBlockX() > loc.getBlockX()) {
            return false;
        }

        if (this.highPoints.getBlockX() < loc.getBlockX()) {
            return false;
        }

        if (this.lowPoints.getBlockZ() > loc.getBlockZ()) {
            return false;
        }

        if (this.highPoints.getBlockZ() < loc.getBlockZ()) {
            return false;
        }

        if (this.lowPoints.getBlockY() > loc.getBlockY()) {
            return false;
        }

        if (this.highPoints.getBlockY() < loc.getBlockY()) {
            return false;
        }

        return true;
    }

    public boolean checkCollision(CuboidArea area) {
        if (!area.getWorld().equals(this.getWorld())) {
            return false;
        }
        if (area.containsLoc(this.lowPoints) || area.containsLoc(this.highPoints) || this.containsLoc(area.highPoints) || this
            .containsLoc(area.lowPoints)) {
            return true;
        }
        return advCuboidCheckCollision(this.highPoints, this.lowPoints, area.highPoints, area.lowPoints);
    }

    public World getWorld() {
        return this.highPoints.getExtent();
    }

    private static boolean advCuboidCheckCollision(Location A1High, Location A1Low, Location A2High, Location A2Low) {
        int A1HX = A1High.getBlockX();
        int A1LX = A1Low.getBlockX();
        int A1HY = A1High.getBlockY();
        int A1LY = A1Low.getBlockY();
        int A1HZ = A1High.getBlockZ();
        int A1LZ = A1Low.getBlockZ();
        int A2HX = A2High.getBlockX();
        int A2LX = A2Low.getBlockX();
        int A2HY = A2High.getBlockY();
        int A2LY = A2Low.getBlockY();
        int A2HZ = A2High.getBlockZ();
        int A2LZ = A2Low.getBlockZ();
        if ((A1HX >= A2LX && A1HX <= A2HX) || (A1LX >= A2LX && A1LX <= A2HX) || (A2HX >= A1LX && A2HX <= A1HX) || (A2LX >= A1LX && A2LX <= A1HX)) {
            if ((A1HY >= A2LY && A1HY <= A2HY) || (A1LY >= A2LY && A1LY <= A2HY) || (A2HY >= A1LY && A2HY <= A1HY) || (A2LY >= A1LY
                                                                                                                       && A2LY <= A1HY)) {
                if ((A1HZ >= A2LZ && A1HZ <= A2HZ) || (A1LZ >= A2LZ && A1LZ <= A2HZ) || (A2HZ >= A1LZ && A2HZ <= A1HZ) || (A2LZ >= A1LZ
                                                                                                                           && A2LZ <= A1HZ)) {
                    return true;
                }
            }
        }
        return false;
    }

    public long getSize() {
        int xsize = (this.highPoints.getBlockX() - this.lowPoints.getBlockX()) + 1;
        int zsize = (this.highPoints.getBlockZ() - this.lowPoints.getBlockZ()) + 1;
        if (!Residence.getInstance().getConfigManager().isNoCostForYBlocks()) {
            int ysize = (this.highPoints.getBlockY() - this.lowPoints.getBlockY()) + 1;
            return xsize * ysize * zsize;
        }
        return xsize * zsize;
    }

    public int getXSize() {
        return (this.highPoints.getBlockX() - this.lowPoints.getBlockX()) + 1;
    }

    public int getYSize() {
        return (this.highPoints.getBlockY() - this.lowPoints.getBlockY()) + 1;
    }

    public int getZSize() {
        return (this.highPoints.getBlockZ() - this.lowPoints.getBlockZ()) + 1;
    }

    public Location getHighLoc() {
        return this.highPoints;
    }

    public Location getLowLoc() {
        return this.lowPoints;
    }

    public void save(DataOutputStream out) throws IOException {
        out.writeUTF(this.highPoints.getExtent().getName());
        out.writeInt(this.highPoints.getBlockX());
        out.writeInt(this.highPoints.getBlockY());
        out.writeInt(this.highPoints.getBlockZ());
        out.writeInt(this.lowPoints.getBlockX());
        out.writeInt(this.lowPoints.getBlockY());
        out.writeInt(this.lowPoints.getBlockZ());
    }

    public CuboidArea load(DataInputStream in) throws IOException {
        CuboidArea newArea = new CuboidArea();
        Server server = Residence.getInstance().getServ();
        World world = server.getWorld(in.readUTF());
        int highx = in.readInt();
        int highy = in.readInt();
        int highz = in.readInt();
        int lowx = in.readInt();
        int lowy = in.readInt();
        int lowz = in.readInt();
        newArea.highPoints = new Location(world, highx, highy, highz);
        newArea.lowPoints = new Location(world, lowx, lowy, lowz);
        newArea.worldName = world.getName();
        return newArea;
    }

    public Map<String, Object> save() {
        Map<String, Object> root = new LinkedHashMap<>();
        root.put("X1", this.highPoints.getBlockX());
        root.put("Y1", this.highPoints.getBlockY());
        root.put("Z1", this.highPoints.getBlockZ());
        root.put("X2", this.lowPoints.getBlockX());
        root.put("Y2", this.lowPoints.getBlockY());
        root.put("Z2", this.lowPoints.getBlockZ());
        return root;
    }

    public String newSave() {
        return this.lowPoints.getBlockX() + ":" + this.lowPoints.getBlockY() + ":" + this.lowPoints.getBlockZ() + ":" + this.highPoints.getBlockX()
               + ":" + this.highPoints
                   .getBlockY() + ":" + this.highPoints.getBlockZ();
    }

    public List<ChunkRef> getChunks() {
        List<ChunkRef> chunks = new ArrayList<>();
        Location high = this.highPoints;
        Location low = this.lowPoints;
        int lowX = ChunkRef.getChunkCoord(low.getBlockX());
        int lowZ = ChunkRef.getChunkCoord(low.getBlockZ());
        int highX = ChunkRef.getChunkCoord(high.getBlockX());
        int highZ = ChunkRef.getChunkCoord(high.getBlockZ());

        for (int x = lowX; x <= highX; x++) {
            for (int z = lowZ; z <= highZ; z++) {
                chunks.add(new ChunkRef(x, z));
            }
        }
        return chunks;
    }

    public void setHighLocation(Location highLocation) {
        this.highPoints = highLocation;
    }

    public void setLowLocation(Location lowLocation) {
        this.lowPoints = lowLocation;
    }
}
