package ahodanenok.relational.algebra;

import ahodanenok.relational.Relation;
import ahodanenok.relational.RelationSchema;
import ahodanenok.relational.RelationSelector;
import ahodanenok.relational.exception.RelationSchemaMismatchException;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Produces a new relation with each tuple in it existing in all of its input relations.
 * All input relations must be of the same type and the resulting relation will also be of that type.
 * Schema of the leftmost relation is used as the schema of the resulting relation.
 */
public final class IntersectOperator implements RelationalOperator {

    private final RelationSchema resultSchema;
    private final Set<Relation> relations;

    public IntersectOperator(Relation a, Relation b) {
        Objects.requireNonNull(a, "relation 'a' can't be null");
        Objects.requireNonNull(b, "relation 'b' can't be null");

        this.resultSchema = a.schema();
        this.relations = new HashSet<>();
        this.relations.add(a);
        this.relations.add(b);
    }

    public IntersectOperator addRelation(Relation relation) {
        Objects.requireNonNull(relation, "relation can't be null");
        relations.add(relation);
        return this;
    }

    @Override
    public Relation execute() {
        if (relations.size() == 1) {
            return relations.iterator().next();
        }

        for (Relation r : relations) {
            if (!r.schema().equals(resultSchema)) {
                throw new RelationSchemaMismatchException(r, resultSchema);
            }
        }

        RelationSelector relationSelector = new RelationSelector().withSchema(resultSchema);
        //noinspection OptionalGetWithoutIsPresent
        relations.stream()
            .min(Comparator.comparing(Relation::cardinality))
            .get() // relations set will contain at least one relation
            .tuples()
            .filter(t -> relations.stream().allMatch(r -> r.contains(t)))
            .forEach(relationSelector::addTuple);

        return relationSelector.select();
    }
}
