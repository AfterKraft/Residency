package com.gabizou.residency.api.region;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;

import java.util.Collection;
import java.util.Optional;

public interface Region extends Iterable<Vector3i> {

    Vector3d getCenter();

    Vector3d getLogicalMinimumPoint();

    Vector3d getLogicalMaximumPoint();

    int getMaxY();

    int getMinY();

    boolean containsPoint(Vector3d point);

    boolean containsPoint(int x, int y, int z);

    int getVolume();

    <T extends Region> Optional<T> asRegionType(Class<T> regionType);

    /**
     * A {@link Region} that is identifiable. Usually by
     * the {@link RegionManager};
     */
    interface Identifiable extends Region, org.spongepowered.api.util.Identifiable {

        String getName();

    }

    /**
     * A region that is expandable after creation. Certain
     * regions can encompass entire worlds such that the
     * region cannot be expanded any further.
     */
    interface Expandable extends Region {

        /**
         * Expands this region to fit the provided
         * {@link Vector3i} points, such that if any
         * points are already within this region, only
         * the "larger" points will be considered.
         *
         * @param points The collection of points
         * @return True, if the region expanded
         */
        boolean expand(Collection<Vector3i> points);
    }
}
