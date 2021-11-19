package ahodanenok.relational.algebra;

import ahodanenok.relational.*;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Natural join of two relations.
 *
 * <p>Produces a new relation containing tuples that have the same values of the common attributes in both relations.
 * If there are no common attributes - result is the same as of the cartesian product operator.
 */
public final class JoinOperator implements RelationalOperator {

    private final Relation left;
    private final Relation right;

    public JoinOperator(Relation left, Relation right) {
        Objects.requireNonNull(left, "Relation can't be null: left");
        Objects.requireNonNull(right, "Relation can't be null: right");
        this.left = left;
        this.right = right;
    }

    @Override
    public Relation execute() {
        RelationSchemaGenerator resultSchemaGenerator = new RelationSchemaGenerator();
        left.attributes().forEach(resultSchemaGenerator::withAttribute);
        right.attributes().forEach(resultSchemaGenerator::withAttribute);

        RelationSchema resultSchema = resultSchemaGenerator.generate();

        Set<String> commonAttributes = resultSchema.attributes()
                .map(Attribute::getName)
                .filter(a -> left.schema().hasAttribute(a) && right.schema().hasAttribute(a))
                .collect(Collectors.toSet());

        RelationSelector resultRelationSelector = new RelationSelector().withSchema(resultSchema);
        left.tuples().forEach(tl -> {
            right.tuples().forEach(tr -> {
                if (commonAttributes.stream().allMatch(a -> Objects.equals(tl.getValue(a), tr.getValue(a)))) {
                    resultRelationSelector.addTuple(union(tl, tr));
                }
            });
        });

        return resultRelationSelector.select();
    }

    private Tuple union(Tuple left, Tuple right) {
        TupleSelector tupleSelector = new TupleSelector();
        left.attributes().forEach(a -> tupleSelector.withValue(a.getName(), left.getValue(a.getName())));
        right.attributes().forEach(a -> tupleSelector.withValue(a.getName(), right.getValue(a.getName())));

        return tupleSelector.select();
    }
}
