package ahodanenok.relational.algebra;

import ahodanenok.relational.Relation;
import ahodanenok.relational.RelationSelector;
import ahodanenok.relational.Tuple;

import java.util.Objects;
import java.util.function.BiPredicate;

/**
 * Restrict operator for a single relation.
 *
 * Produces a new relation with all tuples in it satisfying some boolean predicate.
 */
public final class RestrictOperator implements RelationalOperator {

    private final Relation relation;
    private final BiPredicate<Relation, Tuple> predicate;

    public RestrictOperator(Relation relation, BiPredicate<Relation, Tuple> predicate) {
        Objects.requireNonNull(relation, "relation can't be null");
        Objects.requireNonNull(predicate, "predicate can't be null");
        this.relation = relation;
        this.predicate = predicate;
    }

    @Override
    public Relation execute() {
        RelationSelector resultRelationSelector = new RelationSelector().withSchema(relation.schema());
        relation.tuples().filter(t -> predicate.test(relation, t)).forEach(resultRelationSelector::addTuple);

        return resultRelationSelector.select();
    }
}
