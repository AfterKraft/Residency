package com.gabizou.residency.api.region.protection;

import com.google.common.reflect.TypeToken;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.util.annotation.CatalogedBy;

import java.util.Collection;
import java.util.Optional;

/**
 * A flag is simply a marker of a type of rule for a
 * {@link Protected Protected Region} such that there may be multiple values
 * or a deterministic type set of values for a flag to set on said region.
 * @param <T> The type of value element values
 */
@CatalogedBy(Flags.class)
public interface Flag<T> extends CatalogType {

    Optional<T> getDefaultValue();

    Optional<T> getBestValue(Iterable<T> values);

    TypeToken<T> getToken();

    /**
     * Validates the string input depending on the {@link TypeToken type}
     * of object this flag is utilizing. As an example, if the type is a
     * {@link Number} of sorts, any exceptions can be propogated upwards
     * for more understandable input when changing a flag on a region.
     *
     * <p>Otherwise, if the provided input is simply not available, an
     * exception will be thrown to be properly handled by the caller.</p>
     *
     * @param input The input to parse
     * @return The flag value
     */
    T fromString(String input);

    /**
     * Returns a raw understandable value that can be utilized for storing
     * either in {@link DataContainer}s or something thereof that is exposed
     * for administrators to manipulate, either through configuration files,
     * database files, or through commands.
     *
     * @param value The value
     * @return
     */
    String getStringRepresentation(T value);

    /**
     * An assistance primarily for command parsing to propose tab completion
     * lists if a singular value is not available. Refer to
     * {@link #getBestValue(Iterable)} for determining the optimal value from
     * returned elements.
     *
     * @param input The input string to parse
     * @return A collection of possible values
     */
    Collection<T> getPossibleValues(String input);

}
