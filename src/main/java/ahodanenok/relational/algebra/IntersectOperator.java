package ahodanenok.relational.algebra;

import ahodanenok.relational.Relation;
import ahodanenok.relational.RelationSelector;
import ahodanenok.relational.exception.RelationSchemaMismatchException;
import ahodanenok.relational.expression.RelationalExpression;

import java.util.Objects;

/**
 * Intersection of two relations.
 *
 * <p>Produces a new relation with each tuple in it existing in both relations.
 * Schema of all tuples must be the same.
 */
public final class IntersectOperator implements RelationalOperator {

    private final RelationalExpression leftExpr;
    private final RelationalExpression rightExpr;

    public IntersectOperator(RelationalExpression leftExpr, RelationalExpression rightExpr) {
        Objects.requireNonNull(leftExpr, "Expression can't be null: left");
        Objects.requireNonNull(rightExpr, "Expression can't be null: right");
        this.leftExpr = leftExpr;
        this.rightExpr = rightExpr;
    }

    @Override
    public Relation execute() {
        Relation left = leftExpr.execute();
        Relation right = rightExpr.execute();

        if (!left.schema().equals(right.schema())) {
            throw new RelationSchemaMismatchException(right, left.schema());
        }

        RelationSelector relationSelector = new RelationSelector().withSchema(left.schema());
        left.tuples().filter(right::contains).forEach(relationSelector::addTuple);

        return relationSelector.select();
    }
}
