package ahodanenok.relational.algebra;

import ahodanenok.relational.*;
import ahodanenok.relational.exception.AttributeNotFoundException;

import java.util.*;
import java.util.function.Predicate;

/**
 * Projection of a single tuple.
 *
 * <p>Produces a new relation with a subset of its attributes.
 */
public final class ProjectOperator implements RelationalOperator {

    private final Relation relation;
    private final Set<String> attributeNames;
    private boolean included = true;

    public ProjectOperator(Relation relation, String... attributeNames) {
        this(relation, Arrays.asList(attributeNames));
    }

    public ProjectOperator(Relation relation, List<String> attributeNames) {
        Objects.requireNonNull(relation, "Relation can't be null");
        Objects.requireNonNull(attributeNames, "Attribute names can't be null");
        this.relation = relation;
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
        if (attributeNames.isEmpty() && included) {
            // todo: DEE, DUM!
            return Relation.EMPTY;
        }

        RelationSchema schema = relation.schema();
        for (String name : attributeNames) {
            if (!schema.hasAttribute(name)) {
                throw new AttributeNotFoundException(name);
            }
        }

        RelationSchema resultSchema = projectSchema();
        RelationSelector resultRelationSelector = new RelationSelector().withSchema(resultSchema);
        relation.tuples().map(t -> projectTuple(t, resultSchema)).forEach(resultRelationSelector::addTuple);

        return resultRelationSelector.select();
    }

    private RelationSchema projectSchema() {
        RelationSchemaGenerator resultSchemaGenerator = new RelationSchemaGenerator();
        Predicate<Attribute> predicate = a -> attributeNames.contains(a.getName());
        if (!included) {
            predicate = predicate.negate();
        }
        relation.schema().getAttributes().stream()
                .filter(predicate)
                .forEach(resultSchemaGenerator::withAttribute);

        return resultSchemaGenerator.generate();
    }

    private Tuple projectTuple(Tuple tuple, RelationSchema resultSchema) {
        TupleSelector tupleSelector = new TupleSelector();
        for (Attribute a : resultSchema.getAttributes()) {
            tupleSelector.withValue(a.getName(), tuple.getValue(a.getName()));
        }

        return tupleSelector.select();
    }
}
