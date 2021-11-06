package ahodanenok.relational.exception;

/**
 * Thrown if it's necessary to show that attribute with the given name is already defined in a tuple or relation.
 */
public class AttributeAlreadyExistsException extends RuntimeException {

    private final String name;

    public AttributeAlreadyExistsException(String name) {
        super(String.format("Attribute '%s' already exists", name));
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
