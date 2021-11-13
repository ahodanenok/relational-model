package ahodanenok.relational;

import ahodanenok.relational.exception.AttributeAlreadyExistsException;
import ahodanenok.relational.exception.RelationalException;

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

    public TupleSchemaGenerator withAttribute(String name, Class<?> type) {
        return withAttribute(new Attribute(name, type));
    }

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

    public TupleSchema generate() {
        return new TupleSchema(new HashSet<>(attributes.values()));
    }
}
