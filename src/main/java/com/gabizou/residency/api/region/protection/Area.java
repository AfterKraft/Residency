package com.gabizou.residency.api.region.protection;

import com.flowpowered.math.vector.Vector3i;
import com.gabizou.residency.api.region.CuboidRegion;
import com.gabizou.residency.api.region.PolygonalRegion;
import com.gabizou.residency.api.region.Region;
import org.spongepowered.api.data.DataSerializable;

import java.util.Collection;
import java.util.Optional;

import javax.annotation.Nullable;

public interface Area<A extends Area<A>> extends Region.Identifiable, DataSerializable {

    int getPriority();

    A setPriority(int priority);

    Optional<Area<?>> getParent();

    A setParent(@Nullable Area<?> parent);

    Collection<Area<?>> getChildren();

    A addChild(Area<?> region);

    A addChildren(Area<?>... regions);

    A addChildren(Iterable<Area<?>> regions);

    A removeChild(Area<?> region);

    A removeChildren(Area<?>... regions);

    A removeChildren(Iterable<Area<?>> regions);

    Area<?> getHighestPriorityArea(Iterable<Area<?>> areas);

    Collection<Area<?>> getIntersectingAreas(Iterable<Area<?>> areas);

    Collection<Area<?>> getIntersectingAreas(Vector3i point);

    boolean doesIntersect(Region area);

    interface Builder<A extends Area<A>, B extends Builder<A, B>> extends Region.Identifiable.Builder<A, B> {

        B setParent(Area<?> parent);

        B setPriority(int priority);

    }

    interface Cuboid<C extends Cuboid<C>> extends Area<C>, CuboidRegion {

        interface Builder<C extends Cuboid<C>, B extends Builder<C, B>> extends Area.Builder<C, B>, CuboidRegion.Builder<C, B> {

        }

    }

    interface Polygonal<P extends Polygonal<P>> extends Area<P>, PolygonalRegion<P> {

        interface Builder<C extends Polygonal<C>, B extends Builder<C, B>> extends Area.Builder<C, B>, PolygonalRegion.Builder<C, B> {

        }
    }

}
