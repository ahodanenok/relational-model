package ahodanenok.relational;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Unordered set of attributes in a relation.
 * All tuples in a relation have only the attributes defined in its schema.
 */
public final class RelationSchema {

    public static RelationSchema EMPTY = new RelationSchema(new TupleSchema(Collections.emptySet()));

    private final TupleSchema schema;

    RelationSchema(TupleSchema schema) {
        this.schema = schema;
    }

    public int degree() {
        return schema.degree();
    }

    public Attribute getAttribute(String name) {
        return schema.getAttribute(name);
    }

    public boolean hasAttribute(String name) {
        return schema.hasAttribute(name);
    }

    public Stream<Attribute> attributes() {
        return schema.attributes();
    }

    @Override
    public int hashCode() {
        return schema.hashCode();
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

        RelationSchema other = (RelationSchema) obj;
        return schema.equals(other.schema);
    }
}
