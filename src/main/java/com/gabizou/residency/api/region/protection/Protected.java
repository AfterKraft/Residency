package com.gabizou.residency.api.region.protection;

import com.gabizou.residency.api.membership.Membership;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface Protected<P extends Protected<P>> extends Area<P> {

    Membership getOwners();

    P setOwners(Membership membership);

    Membership getMembers();

    P setMembers(Membership membership);

    default boolean isOwner(User user) {
        return getOwners().contains(user);
    }

    default boolean isOwner(String name) {
        return getOwners().contains(name);
    }

    default boolean isOwner(UUID uuid) {
        return getOwners().contains(uuid);
    }

    default boolean isMember(User user) {
        return getMembers().contains(user);
    }

    default boolean isMember(String name) {
        return getMembers().contains(name);
    }

    default boolean isMember(UUID uuid) {
        return getMembers().contains(uuid);
    }

    <T> Optional<T> getFlag(Flag<T> flag);

    <T> P setFlag(Flag<T> flag, T value);

    Map<Flag<?>, Object> getFlags();

    <T> Optional<T> getFlag(Flag<T> flag, User playerContext);

    <T> Optional<T> getFlag(Flag<T> flag, UUID playerContext);

    <T> Optional<T> getFlag(Flag<T> flag, String playerContext);

    <T> P setFlag(Flag<T> flag, T value, User playerContext);

    <T> P setFlag(Flag<T> flag, T value, UUID playerContext);

    <T> P setFlag(Flag<T> flag, T value, String playerContext);

    interface Cuboid<P extends Cuboid<P>> extends Protected<P>, Area.Cuboid<P> {

    }

    interface Polygonal<P extends Polygonal<P>> extends Protected<P>, Area.Polygonal<P> {

    }
}
