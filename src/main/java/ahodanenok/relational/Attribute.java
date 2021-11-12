package ahodanenok.relational;

import java.util.Objects;

/**
 * Label for a domain (type) used in a relation/tuple.
 * Attribute names are case-sensitive and returned as is, except leading and trailing whitespaces are trimmed.
 */
public final class Attribute {

    private final String name;
    private final Class<?> type;

    public Attribute(String name, Class<?> type) {
        Objects.requireNonNull(name, "name can't be null");
        Objects.requireNonNull(type, "type can't be null");

        name = name.trim();
        if (name.isEmpty()) {
            throw new IllegalArgumentException("name can't be empty");
        }

        this.name = name;
        this.type = type;
    }

    /**
     * Name of the attribute
     */
    public String getName() {
        return name;
    }

    /**
     * Type of the attribute
     */
    public Class<?> getType() {
        return type;
    }

    public int hashCode() {
        return 31 * name.hashCode() + type.hashCode();
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

        Attribute other = (Attribute) obj;
        return name.equals(other.name) && type.equals(other.type);
    }
}
