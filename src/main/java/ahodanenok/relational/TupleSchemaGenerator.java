package ahodanenok.relational;

import ahodanenok.relational.exception.AttributeAlreadyExistsException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Generator for a tuple schema.
 *
 * Schema by itself is a parametrized (generic) type, which needs to be instantiated (generated)
 * by providing all its parameters - here they are names and types of containing attributes.
 */
public final class TupleSchemaGenerator {

    private final Map<String, Attribute> attributes = new HashMap<>();

    /**
     * Define a new attribute in the schema.
     *
     * If the attribute has already been added with the same type, then this invocation
     * won't add another and completes without an error.
     * But if types are different then {@link AttributeAlreadyExistsException} is thrown.
     *
     * @param name attribute name
     * @param type attribute type
     * @return reference to the current generator
     * @throws AttributeAlreadyExistsException if attribute already exists with different type
     */
    public TupleSchemaGenerator withAttribute(String name, Class<?> type) {
        return withAttribute(new Attribute(name, type));
    }

    /**
     * Define a new attribute in the schema.
     *
     * If the attribute has already been added with the same type, then this invocation
     * won't add another and completes without an error.
     * But if types are different then {@link AttributeAlreadyExistsException} is thrown.
     *
     * @param attribute attribute
     * @return reference to the current generator
     * @throws AttributeAlreadyExistsException if attribute already exists with different type
     */
    public TupleSchemaGenerator withAttribute(Attribute attribute) {
        attributes.compute(attribute.getName(), (k, existingAttribute) -> {
            if (existingAttribute != null && !attribute.equals(existingAttribute)) {
                throw new AttributeAlreadyExistsException(
                    String.format(
                        "Attribute '%s' has been already added, but with a different type '%s', type received now '%s'",
                        existingAttribute.getName(), existingAttribute.getType().getName(), attribute.getType().getName()),
                    existingAttribute);
            }

            return attribute;
        });

        return this;
    }

    /**
     * Instantiate a schema with all defined attributes in the current generator.
     * If there are no defined attributes, schema will be empty (0-ary).
     *
     * @return tuple schema
     */
    public TupleSchema generate() {
        return new TupleSchema(new HashSet<>(attributes.values()));
    }
}
