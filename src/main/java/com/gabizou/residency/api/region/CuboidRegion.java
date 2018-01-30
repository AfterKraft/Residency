package com.gabizou.residency.api.region;

import com.flowpowered.math.vector.Vector2i;

import java.util.Collection;

public interface CuboidRegion extends Region.Expandable {

    Collection<Vector2i> getPoints();

    interface Builder<C extends CuboidRegion, B extends Builder<C, B>> extends Region.Expandable.Builder<C, B> {

        B setMinY(int minimumY);

        B setMaxY(int maxY);

        B addPoint(Vector2i point);

        B addPoints(Iterable<Vector2i> points);

        B addPoints(Vector2i point, Vector2i... points);

    }

}
