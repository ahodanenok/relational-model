package ahodanenok.relational.algebra;

import ahodanenok.relational.*;
import ahodanenok.relational.exception.AttributeAlreadyExistsException;
import ahodanenok.relational.expression.RelationalExpression;

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

    private final RelationalExpression leftExpr;
    private final RelationalExpression rightExpr;

    public ProductOperator(RelationalExpression leftExpr, RelationalExpression rightExpr) {
        Objects.requireNonNull(leftExpr, "Expression can't be null: left");
        Objects.requireNonNull(rightExpr, "Expression can't be null: right");
        this.leftExpr = leftExpr;
        this.rightExpr = rightExpr;
    }

    @Override
    public Relation execute() {
        Relation left = leftExpr.execute();
        Relation right = rightExpr.execute();

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
