package ahodanenok.relational.algebra;

import ahodanenok.relational.*;
import ahodanenok.relational.exception.AttributeAlreadyExistsException;

import java.util.Objects;

/**
 * Cartesian product of two relations.
 *
 * <p>Takes all possible pairs of tuples, where one tuple from the left relation and other from the right relation
 * and produces produces a new relation, where each tuple is a union of attributes in a single pair.
 *
 * <p>It is required that schemas of input relations do not have common attributes, otherwise
 * {@link AttributeAlreadyExistsException} is thrown.
 */
public final class ProductOperator implements RelationalOperator {

    private final Relation left;
    private final Relation right;

    public ProductOperator(Relation left, Relation right) {
        Objects.requireNonNull(left, "Relation can't be null: left");
        Objects.requireNonNull(right, "Relation can't be null: right");

        this.left = left;
        this.right = right;
    }

    @Override
    public Relation execute() {
        left.attributes()
                .filter(a -> right.schema().hasAttribute(a.getName()))
                .findFirst()
                .ifPresent(a -> {
                    throw new AttributeAlreadyExistsException(a);
                });

        RelationSchemaGenerator resultSchemaGenerator = new RelationSchemaGenerator();
        left.attributes().forEach(resultSchemaGenerator::withAttribute);
        right.attributes().forEach(resultSchemaGenerator::withAttribute);

        RelationSelector resultRelationSelector = new RelationSelector()
                .withSchema(resultSchemaGenerator.generate());
        left.tuples().forEach(tl ->
                right.tuples().forEach(tr ->
                        resultRelationSelector.addTuple(union(tl, tr))));

        return resultRelationSelector.select();
    }

    private Tuple union(Tuple left, Tuple right) {
        TupleSelector tupleSelector = new TupleSelector();
        left.attributes().forEach(a -> tupleSelector.withValue(a.getName(), left.getValue(a.getName())));
        right.attributes().forEach(a -> tupleSelector.withValue(a.getName(), right.getValue(a.getName())));

        return tupleSelector.select();
    }
}
