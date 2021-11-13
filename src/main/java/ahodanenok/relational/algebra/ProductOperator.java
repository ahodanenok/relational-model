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
        Objects.requireNonNull(left, "relation 'a' can't be null");
        Objects.requireNonNull(right, "relation 'b' can't be null");

        this.left = left;
        this.right = right;
    }

    @Override
    public Relation execute() {
        for (Attribute a : left.schema().getAttributes()) {
            if (right.schema().hasAttribute(a.getName())) {
                throw new AttributeAlreadyExistsException(a);
            }
        }

        RelationSchemaGenerator resultSchemaGenerator = new RelationSchemaGenerator();
        left.schema().getAttributes().forEach(resultSchemaGenerator::withAttribute);
        right.schema().getAttributes().forEach(resultSchemaGenerator::withAttribute);

        RelationSelector resultRelationSelector = new RelationSelector()
                .withSchema(resultSchemaGenerator.generate());
        left.tuples().forEach(tl ->
                right.tuples().forEach(tr ->
                        resultRelationSelector.addTuple(union(tl, tr))));

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
