package ahodanenok.relational.algebra;

import ahodanenok.relational.Relation;
import ahodanenok.relational.RelationSelector;
import ahodanenok.relational.Tuple;
import ahodanenok.relational.expression.RelationalExpression;

import java.util.Objects;
import java.util.function.BiPredicate;

/**
 * Restrict operator for a single relation.
 *
 * <p>Produces a new relation with all tuples in it satisfying some boolean predicate.
 */
public final class RestrictOperator implements RelationalOperator {

    private final RelationalExpression expr;
    private final BiPredicate<Relation, Tuple> predicate;

    public RestrictOperator(RelationalExpression expr, BiPredicate<Relation, Tuple> predicate) {
        Objects.requireNonNull(expr, "Expression can't be null");
        Objects.requireNonNull(predicate, "Predicate can't be null");
        this.expr = expr;
        this.predicate = predicate;
    }

    @Override
    public Relation execute() {
        Relation relation = expr.execute();
        RelationSelector resultRelationSelector = new RelationSelector().withSchema(relation.schema());
        relation.tuples().filter(t -> predicate.test(relation, t)).forEach(resultRelationSelector::addTuple);

        return resultRelationSelector.select();
    }
}
