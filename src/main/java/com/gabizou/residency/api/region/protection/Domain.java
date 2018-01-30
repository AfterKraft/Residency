package com.gabizou.residency.api.region.protection;

import com.gabizou.residency.api.membership.Membership;
import org.spongepowered.api.entity.living.player.User;

import java.util.UUID;

/**
 * A specialized {@link Protected} region that has a representation of
 * owners and members.
 * @param <D>
 */
public interface Domain<D extends Domain<D>> extends Protected<D> {

    Membership getOwners();

    D setOwners(Membership membership);

    Membership getMembers();

    D setMembers(Membership membership);

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

    interface Builder<P extends Domain<P>, B extends Builder<P, B>> extends Protected.Builder<P, B> {

        B setOwners(Membership members);

        B setMembers(Membership members);

    }

    interface Cuboid<P extends Cuboid<P>> extends Domain<P>, Protected.Cuboid<P> {


        interface Builder<P extends Cuboid<P>, B extends Cuboid.Builder<P, B>> extends Protected.Cuboid.Builder<P, B> {

        }
    }

    interface Polygonal<P extends Polygonal<P>> extends Domain<P>, Protected.Polygonal<P> {


        interface Builder<P extends Polygonal<P>, B extends Builder<P, B>> extends Protected.Polygonal.Builder<P, B> {

        }
    }
}
