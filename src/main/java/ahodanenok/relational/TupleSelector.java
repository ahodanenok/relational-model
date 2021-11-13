package ahodanenok.relational;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

    private final Map<String, Object> values = new HashMap<>();

    public TupleSelector withValue(String attributeName, Object value) {
        // todo: name not null or empty
        // todo: name not exists
        // todo: null value allowed?
        values.put(attributeName, value);
        return this;
    }

    public Tuple select() {
        return new Tuple(new TupleSchema(attributes), values);
    }
}
