package ahodanenok.relational;

import java.util.Set;

/**
 * Description of a relation.
 */
public final class RelationSchema {

    private final TupleSchema schema;

    RelationSchema(TupleSchema schema) {
        this.schema = schema;
    }

    /**
     * Number of attributes in the schema.
     */
    public int degree() {
        return schema.degree();
    }

    /**
     * Get attribute with the given name.
     *
     * @param name attribute name, case-sensitive with leading and trailing whitespaces trimmed
     * @throws AttributeNotFoundException if attribute wasn't found
     * @throws NullPointerException if attribute is null
     */
    public Attribute getAttribute(String name) {
        return schema.getAttribute(name);
    }

    /**
     * Get all attributes in the schema.
     *
     * @return unmodifiable set of attributes without any particular order
     */
    public Set<Attribute> getAttributes() {
        return schema.getAttributes();
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
