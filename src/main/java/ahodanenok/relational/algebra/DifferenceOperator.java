package ahodanenok.relational.algebra;

import ahodanenok.relational.Relation;
import ahodanenok.relational.RelationSelector;
import ahodanenok.relational.exception.RelationSchemaMismatchException;

import java.util.Objects;

/**
 * Difference of two relations.
 *
 * <p>Produces a new relation with each tuple in it existing only in the left relation, but not in the right.
 * Schema of all tuples must be the same.
 */
public final class DifferenceOperator implements RelationalOperator {

    private final Relation left;
    private final Relation right;

    public DifferenceOperator(Relation left, Relation right) {
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
        left.tuples().filter(t -> !right.contains(t)).forEach(relationSelector::addTuple);

        return relationSelector.select();
    }
}
