package ahodanenok.relational;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Tuple is an unordered set of attributes with their values.
 *
 * Each attribute within a tuple is identified by its name and there can't be
 * more than one attribute with the same name. Attribute can have only one value,
 * which is of type defined in the attribute.
 *
 * Tuple can be empty (0-tuple), that it doesn't contain any attributes.
 */
public final class Tuple {

    public static final Tuple EMPTY = new Tuple(TupleSchema.EMPTY, Collections.emptyMap());

    private final TupleSchema schema;
    private final Map<String, Object> values;

    Tuple(TupleSchema schema, Map<String, Object> values) {
        this.schema = schema;
        this.values = values;
    }

    /**
     * Get schema of the tuple.
     */
    public TupleSchema schema() {
        return schema;
    }

    /**
     * Get number of attributes in the tuple
     */
    public int degree() {
        return schema.degree();
    }

    /**
     * Get attributes defined in this tuple
     */
    public Set<Attribute> getAttributes() {
        return schema.getAttributes();
    }

    public Object getValue(String attributeName) {
        // todo: name is not null or empty
        // todo: throw error if attribute not found
        return values.get(attributeName);
    }

    @Override
    public int hashCode() {
        return 31 * schema.hashCode() + values.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (!this.getClass().equals(obj.getClass())) {
            return false;
        }

        Tuple other = (Tuple) obj;

        // Two tuple are equal iff they have the same attributes with the same values.
        if (!schema.equals(other.schema)) {
            return false;
        }

        for (Map.Entry<String, Object> entry : values.entrySet()) {
            if (!Objects.equals(entry.getValue(), other.values.get(entry.getKey()))) {
                return false;
            }
        }

        return true;
    }
}
