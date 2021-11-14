package ahodanenok.relational;

import ahodanenok.relational.exception.TupleSchemaMismatchException;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public final class RelationSelector {

    private RelationSchema schema;
    private final Set<Tuple> tuples = new HashSet<>();

    public RelationSelector withSchema(RelationSchema schema) {
        Objects.requireNonNull(schema, "Relation schema can't be null");
        this.schema = schema;
        return this;
    }

    public RelationSelector addTuple(Tuple tuple) {
        RelationSchema tupleSchema = new RelationSchema(tuple.schema());
        if (schema != null && !tupleSchema.equals(schema)) {
            throw new TupleSchemaMismatchException(tuple, schema);
        }

        tuples.add(tuple);
        if (schema == null) {
            schema = tupleSchema;
        }

        return this;
    }

    public Relation select() {
        RelationSchema targetSchema = schema;

        // if schema is null, then no tuples were added and we can't infer it
        if (targetSchema == null) {
            throw new RelationalException("Schema must be specified explicitly if the relation is empty");
        }

        return new Relation(schema, tuples);
    }
}
