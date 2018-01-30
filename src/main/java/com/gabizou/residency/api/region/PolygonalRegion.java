package com.gabizou.residency.api.region;

import com.flowpowered.math.vector.Vector3i;

import java.util.Collection;

public interface PolygonalRegion<P extends PolygonalRegion<P>> extends Region.Expandable {

    Collection<Vector3i> getPoints();

    P setPoints(Iterable<Vector3i> points);


    interface Builder<C extends PolygonalRegion, B extends PolygonalRegion.Builder<C, B>> extends Region.Expandable.Builder<C, B> {

    }
}
