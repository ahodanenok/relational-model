package ahodanenok.relational;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

/**
 * Description of tuple attributes.
 *
 * Tuple schema consists of an unordered set of pairs (A, T),
 * where A is an attribute name and T - attribute type.
 *
 * It's is also possible that schema can be an empty set (0-tuple)
 */
public final class TupleSchema {

    private final Set<Attribute> attributes;

    public TupleSchema(Set<Attribute> attributes) {
        this.attributes = Collections.unmodifiableSet(attributes);
    }

    /**
     * Number of attributes in the schema.
     */
    public int degree() {
        return attributes.size();
    }

    /**
     * Get attribute with the given name.
     *
     * @param name attribute name, case-sensitive with leading and trailing whitespaces trimmed
     * @throws AttributeNotFoundException if attribute wasn't found
     * @throws NullPointerException if attribute is null
     */
    public Attribute getAttribute(String name) {
        Objects.requireNonNull(name, "name can't be null");

        String lookupName = name.trim();
        return attributes.stream()
                .filter(it -> it.getName().equals(lookupName))
                .findFirst()
                .orElseThrow(() -> new AttributeNotFoundException(lookupName));
    }

    /**
     * Get all attributes in the schema.
     *
     * @return unmodifiable set of attributes without any particular order, empty list for 0-tuple
     * todo: expose only iterator instead of set of attributes?
     */
    public Set<Attribute> getAttributes() {
        return attributes;
    }

    @Override
    public int hashCode() {
        return attributes.hashCode();
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

        TupleSchema other = (TupleSchema) obj;
        return attributes.equals(other.attributes);
    }
}
