package ahodanenok.relational;

/**
 * Definition of a single attribute.
 * Attribute is a pair (A, T) where A - name and T - type of an attribute.
 */
public final class Attribute {

    private final String name;
    private final Class<?> type;

    public Attribute(String name, Class<?> type) {
        // todo: name not null or empty
        // todo: type is not null
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