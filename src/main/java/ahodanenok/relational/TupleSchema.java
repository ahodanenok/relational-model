package ahodanenok.relational;

import ahodanenok.relational.exception.AttributeNotFoundException;

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Set of attributes in a particular tuple.
 * This set is not ordered, so the only way to referer to some attribute is by its name.
 */
public final class TupleSchema {

    public static final TupleSchema EMPTY = new TupleSchema(Collections.emptySet());

    private final Set<Attribute> attributes;

    TupleSchema(Set<Attribute> attributes) {
        this.attributes = Collections.unmodifiableSet(attributes);
    }

    public int degree() {
        return attributes.size();
    }

    public Attribute getAttribute(String name) {
        return lookupAttribute(name, true);
    }

    public boolean hasAttribute(String name) {
        return lookupAttribute(name, false) != null;
    }

    private Attribute lookupAttribute(String name, boolean required) {
        Objects.requireNonNull(name, "Attribute name can't be null");
        String lookupName = name.trim();
        Optional<Attribute> result = attributes.stream()
                .filter(it -> it.getName().equals(lookupName))
                .findFirst();

        if (required && !result.isPresent()) {
            throw new AttributeNotFoundException(lookupName);
        }

        return result.orElse(null);
    }

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
