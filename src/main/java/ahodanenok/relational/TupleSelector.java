package ahodanenok.relational;

import ahodanenok.relational.exception.RelationalException;

import java.util.*;

/**
 * Select a tuple from the set of all tuples.
 *
 * Tuple schema is determined by attribute names and types of corresponding values.
 * If no values are provided, then empty (0-ary) tuple will be selected.
 *
 * Nulls are not supported as attribute values.
 */
public final class TupleSelector {

    private final Set<Attribute> attributes = new HashSet<>();
    private final Map<String, Object> values = new HashMap<>();

    public TupleSelector withValue(String name, Object value) {
        Objects.requireNonNull(name, "Attribute name can't be null");
        Objects.requireNonNull(value, "Attribute value can't be null");
        return withValue(new Attribute(name, value.getClass()), value);
    }

    public TupleSelector withValue(Attribute attribute, Object value) {
        Objects.requireNonNull(attribute, "Attribute can't be null");
        Objects.requireNonNull(value, "Attribute value can't be null");

        if (!attribute.getType().equals(value.getClass())) {
            throw new RelationalException(
                    String.format("Value type '%s' for attribute '%s' doesn't equal its type '%s'",
                        value.getClass().getName(), attribute.getName(), attribute.getType().getName()));
        }

        // allow changing values only if their types are equal
        if (values.containsKey(attribute.getName()) && !attributes.contains(attribute)) {
            @SuppressWarnings("OptionalGetWithoutIsPresent")
            Attribute existingAttribute = attributes.stream()
                    .filter(a -> a.getName().equals(attribute.getName()))
                    .findFirst()
                    .get();

            String msg = String.format(
                    "Can't change attribute type for '%s', initial type is '%s', current type is '%s'",
                    existingAttribute.getName(), existingAttribute.getType().getName(), attribute.getType().getName());

            throw new RelationalException(msg);
        }

        attributes.add(attribute);
        values.put(attribute.getName(), value);

        return this;
    }

    public Tuple select() {
        return new Tuple(new TupleSchema(attributes), values);
    }
}
