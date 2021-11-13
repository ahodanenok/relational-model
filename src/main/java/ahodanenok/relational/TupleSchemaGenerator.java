package ahodanenok.relational;

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
                String msg = String.format(
                        "Can't add attribute '%s' of type '%s' as it has already been added with type '%s'",
                        attribute.getName(), attribute.getType().getName(), existingAttribute.getType().getName());

                throw new RelationalException(msg);
            }

            return attribute;
        });

        return this;
    }

    public TupleSchema generate() {
        return new TupleSchema(new HashSet<>(attributes.values()));
    }
}
