package ahodanenok.relational.algebra;

import ahodanenok.relational.*;
import ahodanenok.relational.exception.AttributeNotFoundException;
import ahodanenok.relational.expression.RelationalExpression;

import java.util.*;
import java.util.function.Predicate;

/**
 * Projection of a single tuple.
 *
 * <p>Produces a new relation with a subset of its attributes.
 */
public final class ProjectOperator implements RelationalOperator {

    private final RelationalExpression expr;
    private final Set<String> attributeNames;
    private boolean included = true;

    public ProjectOperator(RelationalExpression expr, String... attributeNames) {
        this(expr, Arrays.asList(attributeNames));
    }

    public ProjectOperator(RelationalExpression expr, List<String> attributeNames) {
        Objects.requireNonNull(expr, "Expression can't be null");
        Objects.requireNonNull(attributeNames, "Attribute names can't be null");
        this.expr = expr;
        this.attributeNames = new HashSet<>(attributeNames);
    }

    /**
     * Produce a new relation only with specified attributes. This is default mode.
     */
    public ProjectOperator includeAttributes() {
        this.included = true;
        return this;
    }

    /**
     * Produce a new relation without specified attributes
     */
    public ProjectOperator withoutAttributes() {
        this.included = false;
        return this;
    }

    @Override
    public Relation execute() {
        Relation relation = expr.execute();

        if (attributeNames.isEmpty() && included) {
            return relation.isEmpty() ? Relation.NULLARY_EMPTY : Relation.NULLARY_TUPLE;
        }

        RelationSchema schema = relation.schema();
        for (String name : attributeNames) {
            if (!schema.hasAttribute(name)) {
                throw new AttributeNotFoundException(name);
            }
        }

        RelationSchema resultSchema = projectSchema(relation);
        RelationSelector resultRelationSelector = new RelationSelector().withSchema(resultSchema);
        relation.tuples().map(t -> projectTuple(t, resultSchema)).forEach(resultRelationSelector::addTuple);

        return resultRelationSelector.select();
    }

    private RelationSchema projectSchema(Relation relation) {
        Predicate<Attribute> predicate = a -> attributeNames.contains(a.getName());
        if (!included) {
            predicate = predicate.negate();
        }

        RelationSchemaGenerator resultSchemaGenerator = new RelationSchemaGenerator();
        relation.attributes()
                .filter(predicate)
                .forEach(resultSchemaGenerator::withAttribute);

        return resultSchemaGenerator.generate();
    }

    private Tuple projectTuple(Tuple tuple, RelationSchema resultSchema) {
        TupleSelector tupleSelector = new TupleSelector();
        resultSchema.attributes().forEach(a -> tupleSelector.withValue(a.getName(), tuple.getValue(a.getName())));

        return tupleSelector.select();
    }
}
