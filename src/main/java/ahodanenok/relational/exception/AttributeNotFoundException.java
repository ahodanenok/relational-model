package ahodanenok.relational.exception;

/**
 * Thrown if during attribute lookup by its name it wasn't found.
 */
public final class AttributeNotFoundException extends RuntimeException {

    private final String name;

    public AttributeNotFoundException(String name) {
        super("Attribute '" + name + "' not found");
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
