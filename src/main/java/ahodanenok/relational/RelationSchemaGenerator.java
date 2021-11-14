package ahodanenok.relational;

/**
 * Generator for a relation schema.
 *
 * Schema by itself is a parametrized (generic) type, which needs to be instantiated (generated)
 * by providing all its parameters - here they are names and types of containing attributes.
 */
public final class RelationSchemaGenerator {

    private final TupleSchemaGenerator tupleSchemaGenerator = new TupleSchemaGenerator();

    public RelationSchemaGenerator withAttribute(String name, Class<?> type) {
        return withAttribute(new Attribute(name, type));
    }

    public RelationSchemaGenerator withAttribute(Attribute attribute) {
        tupleSchemaGenerator.withAttribute(attribute);
        return this;
    }

    public RelationSchema generate() {
        return new RelationSchema(tupleSchemaGenerator.generate());
    }
}
