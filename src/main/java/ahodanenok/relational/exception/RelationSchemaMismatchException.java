package ahodanenok.relational.exception;

import ahodanenok.relational.Relation;
import ahodanenok.relational.RelationSchema;

/**
 * Indicate that the schema of a relation is not compatible with the target schema.
 */
public class RelationSchemaMismatchException extends RuntimeException {

    private final Relation mismatchedRelation;
    private final RelationSchema targetSchema;

    public RelationSchemaMismatchException(Relation mismatchedRelation, RelationSchema targetSchema) {
        super("Relation's schema is not compatible with the target schema.");
        this.mismatchedRelation = mismatchedRelation;
        this.targetSchema = targetSchema;
    }

    public Relation getMismatchedRelation() {
        return mismatchedRelation;
    }

    public RelationSchema getTargetSchema() {
        return targetSchema;
    }
}
