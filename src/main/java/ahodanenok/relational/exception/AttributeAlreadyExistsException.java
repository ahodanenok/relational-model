package ahodanenok.relational.exception;

import ahodanenok.relational.Attribute;

/**
 * Thrown if it's necessary to show that attribute with the given name is already defined in a tuple or relation.
 */
public class AttributeAlreadyExistsException extends RelationalException {

    private final Attribute existingAttribute;

    public AttributeAlreadyExistsException(Attribute existingAttribute) {
        this(String.format("Attribute '%s' already exists", existingAttribute.getName()), existingAttribute);
    }

    public AttributeAlreadyExistsException(String message, Attribute existingAttribute) {
        super(message);
        this.existingAttribute = existingAttribute;
    }

    public Attribute getExistingAttribute() {
        return existingAttribute;
    }
}
