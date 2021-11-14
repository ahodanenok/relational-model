package ahodanenok.relational.algebra;

import ahodanenok.relational.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Natural join of two relations.
 *
 * Produces a new relation containing tuples that have the same values of the common attributes in both relations.
 * If there are no common attributes - result is the same as of the cartesian product operator.
 */
public final class JoinOperator implements RelationalOperator {

    private final Relation left;
    private final Relation right;

    public JoinOperator(Relation left, Relation right) {
        Objects.requireNonNull(left, "Left relation can't be null");
        Objects.requireNonNull(right, "Right relation can't be null");
        this.left = left;
        this.right = right;
    }

    @Override
    public Relation execute() {
        RelationSchemaGenerator resultSchemaGenerator = new RelationSchemaGenerator();
        left.schema().getAttributes().forEach(resultSchemaGenerator::withAttribute);
        right.schema().getAttributes().forEach(resultSchemaGenerator::withAttribute);

        RelationSchema resultSchema = resultSchemaGenerator.generate();

        Set<String> commonAttributes = new HashSet<>();
        for (Attribute a : resultSchema.getAttributes()) {
            if (left.schema().hasAttribute(a.getName()) && right.schema().hasAttribute(a.getName())) {
                commonAttributes.add(a.getName());
            }
        }

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
        for (Attribute a : left.getAttributes()) {
            tupleSelector.withValue(a.getName(), left.getValue(a.getName()));
        }
        for (Attribute a : right.getAttributes()) {
            tupleSelector.withValue(a.getName(), right.getValue(a.getName()));
        }

        return tupleSelector.select();
    }
}
