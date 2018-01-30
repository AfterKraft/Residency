package com.gabizou.residency.api.region.protection;

import org.spongepowered.api.entity.living.player.User;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface Protected<P extends Protected<P>> extends Area<P> {

    <T> Optional<T> getFlag(Flag<T> flag);

    <T> P setFlag(Flag<T> flag, T value);

    Map<Flag<?>, Object> getFlags();

    <T> Optional<T> getFlag(Flag<T> flag, User playerContext);

    <T> Optional<T> getFlag(Flag<T> flag, UUID playerContext);

    <T> Optional<T> getFlag(Flag<T> flag, String playerContext);

    <T> P setFlag(Flag<T> flag, T value, User playerContext);

    <T> P setFlag(Flag<T> flag, T value, UUID playerContext);

    <T> P setFlag(Flag<T> flag, T value, String playerContext);

    interface Builder<P extends Protected<P>, B extends Builder<P, B>> extends Area.Builder<P, B> {

    }

    interface Cuboid<P extends Cuboid<P>> extends Protected<P>, Area.Cuboid<P> {


        interface Builder<P extends Cuboid<P>, B extends Cuboid.Builder<P, B>> extends Area.Cuboid.Builder<P, B> {

        }
    }

    interface Polygonal<P extends Polygonal<P>> extends Protected<P>, Area.Polygonal<P> {


        interface Builder<P extends Polygonal<P>, B extends Builder<P, B>> extends Area.Polygonal.Builder<P, B> {

        }
    }
}
