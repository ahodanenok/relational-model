package ahodanenok.relational;

import java.util.HashSet;
import java.util.Set;

public final class RelationSchemaGenerator {

    private final Set<Attribute> attributes = new HashSet<>();

    public RelationSchemaGenerator withAttribute(String name, Class<?> type) {
        return withAttribute(new Attribute(name, type));
    }

    public RelationSchemaGenerator withAttribute(Attribute attribute) {
        // todo: not null
        // todo: if exists? same/different type
        attributes.add(attribute);
        return this;
    }

    public RelationSchema generate() {
        return new RelationSchema(new TupleSchema(attributes));
    }
}
