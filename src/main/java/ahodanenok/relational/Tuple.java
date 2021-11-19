package ahodanenok.relational;

import ahodanenok.relational.exception.AttributeNotFoundException;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Tuple is an unordered set of attributes with their values.
 *
 * Each attribute within a tuple is identified by its name and there can't be
 * more than one attribute with the same name. Attribute can have only one value,
 * which is of type defined in the attribute.
 *
 * Tuple can be empty (0-ary), that is it doesn't contain any attributes.
 * Nulls are not supported as attribute values.
 */
public final class Tuple {

    public static final Tuple EMPTY = new Tuple(TupleSchema.EMPTY, Collections.emptyMap());

    private final TupleSchema schema;
    private final Map<String, Object> values;

    Tuple(TupleSchema schema, Map<String, Object> values) {
        this.schema = schema;
        this.values = values;
    }

    public TupleSchema schema() {
        return schema;
    }

    public int degree() {
        return schema.degree();
    }

    public Stream<Attribute> attributes() {
        return schema.attributes();
    }

    public Object getValue(String attributeName) {
        Objects.requireNonNull(attributeName, "Attribute name can't be null");

        attributeName = attributeName.trim();
        Object value = values.get(attributeName);
        if (value == null) {
            throw new AttributeNotFoundException(attributeName);
        }

        return value;
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
