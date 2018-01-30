package com.gabizou.residency.region;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.gabizou.residency.api.region.Region;

import java.util.Iterator;
import java.util.Optional;

public class RegionImpl implements Region {

    @Override
    public Vector3d getCenter() {
        return null;
    }

    @Override
    public Vector3d getLogicalMinimumPoint() {
        return null;
    }

    @Override
    public Vector3d getLogicalMaximumPoint() {
        return null;
    }

    @Override
    public int getMaxY() {
        return 0;
    }

    @Override
    public int getMinY() {
        return 0;
    }

    @Override
    public boolean containsPoint(Vector3d point) {
        return false;
    }

    @Override
    public boolean containsPoint(int x, int y, int z) {
        return false;
    }

    @Override
    public int getVolume() {
        return 0;
    }

    @Override
    public <T extends Region> Optional<T> asRegionType(Class<T> regionType) {
        return Optional.empty();
    }

    @Override
    public Iterator<Vector3i> iterator() {
        return null;
    }
}
