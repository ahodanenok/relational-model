package ahodanenok.relational;

import ahodanenok.relational.exception.AttributeNotFoundException;

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
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

    public static final TupleSchema EMPTY = new TupleSchema(Collections.emptySet());

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
     * @param name attribute name, case-sensitive, leading and trailing whitespaces will be trimmed
     * @throws AttributeNotFoundException if attribute wasn't found
     * @throws NullPointerException if attribute is null
     */
    public Attribute getAttribute(String name) {
        Objects.requireNonNull(name, "name can't be null");
        return lookupAttribute(name, true);
    }

    /**
     * Check if attribute with the given name exists in the schema.
     * @param name attribute name, case-sensitive, leading and trailing whitespaces will be trimmed
     */
    public boolean hasAttribute(String name) {
        return lookupAttribute(name, false) != null;
    }

    private Attribute lookupAttribute(String name, boolean required) {
        String lookupName = name.trim();
        Optional<Attribute> result = attributes.stream()
                .filter(it -> it.getName().equals(lookupName))
                .findFirst();

        if (required && !result.isPresent()) {
            throw new AttributeNotFoundException(lookupName);
        }

        return result.orElse(null);
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
