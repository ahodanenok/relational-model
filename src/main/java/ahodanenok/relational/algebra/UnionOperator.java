package ahodanenok.relational.algebra;

import ahodanenok.relational.Relation;
import ahodanenok.relational.RelationSelector;
import ahodanenok.relational.exception.RelationSchemaMismatchException;

import java.util.Objects;

/**
 * Union of two relations.
 *
 * Produces a new relation with all tuples existing in both relations.
 * Schema of all tuples must be the same.
 */
public final class UnionOperator implements RelationalOperator {

    private final Relation left;
    private final Relation right;

    public UnionOperator(Relation left, Relation right) {
        Objects.requireNonNull(left, "Relation can't be null: left");
        Objects.requireNonNull(right, "Relation can't be null: right");

        this.left = left;
        this.right = right;
    }

    @Override
    public Relation execute() {
        if (!left.schema().equals(right.schema())) {
            throw new RelationSchemaMismatchException(right, left.schema());
        }

        RelationSelector relationSelector = new RelationSelector().withSchema(left.schema());
        left.tuples().forEach(relationSelector::addTuple);
        right.tuples().forEach(relationSelector::addTuple);

        return relationSelector.select();
    }
}
