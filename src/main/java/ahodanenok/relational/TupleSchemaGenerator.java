package ahodanenok.relational;

import java.util.HashSet;
import java.util.Set;

public final class TupleSchemaGenerator {

    private final Set<Attribute> attributes = new HashSet<>();

    public TupleSchemaGenerator withAttribute(String name, Class<?> type) {
        return withAttribute(new Attribute(name, type));
    }

    public TupleSchemaGenerator withAttribute(Attribute attribute) {
        // todo: throw error if already added or ignore that?
        // todo: throw error if attribute already added but with different type
        attributes.add(attribute);
        return this;
    }

    public TupleSchema generate() {
        return new TupleSchema(attributes);
    }
}
