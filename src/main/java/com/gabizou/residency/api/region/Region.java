package com.gabizou.residency.api.region;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.util.ResettableBuilder;

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

    interface Builder<R extends Region, B extends Builder<R, B>> extends ResettableBuilder<R, B> {

        // todo - determine whether this builder should at least accept some points, in my honest opinion, a shaped region
        // is more buildable, this sort of abstracted region shouldn't be instanced by itself, except for things like
        // region selections. Since those are expandable though, we end up with something in between Area and Expandable.

        R build();

    }

    /**
     * A {@link Region} that is identifiable. Usually by
     * the {@link RegionManager};
     */
    interface Identifiable extends Region, org.spongepowered.api.util.Identifiable {

        String getName();

        interface Builder<I extends Identifiable, B extends Builder<I, B>> extends Region.Builder<I, B> {

            B setId(String id);

            B setName(String name);

        }

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

        interface Builder<I extends Expandable, B extends Builder<I, B>> extends Region.Builder<I, B> {

        }

    }
}
