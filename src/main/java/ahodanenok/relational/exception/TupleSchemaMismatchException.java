package ahodanenok.relational.exception;

import ahodanenok.relational.RelationSchema;
import ahodanenok.relational.Tuple;

/**
 * Indicate that the schema of a tuple is not compatible with the target schema.
 */
public final class TupleSchemaMismatchException extends RuntimeException {

    private final Tuple mismatchedTuple;
    private final RelationSchema targetSchema;

    public TupleSchemaMismatchException(Tuple mismatchedTuple, RelationSchema targetSchema) {
        super("Tuple's schema is not compatible with relation's schema.");
        this.mismatchedTuple = mismatchedTuple;
        this.targetSchema = targetSchema;
    }

    public Tuple getMismatchedTuple() {
        return mismatchedTuple;
    }

    public RelationSchema getTargetSchema() {
        return targetSchema;
    }
}
