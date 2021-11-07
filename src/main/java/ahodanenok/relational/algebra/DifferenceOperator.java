package ahodanenok.relational.algebra;

import ahodanenok.relational.Relation;
import ahodanenok.relational.RelationSchema;
import ahodanenok.relational.RelationSelector;
import ahodanenok.relational.exception.RelationSchemaMismatchException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Produces a new relation with each tuple in it existing only in the first argument and not in others.
 * All input relations must be of the same type and the resulting relation will also be of that type.
 * Schema of the leftmost relation is used as the schema of the resulting relation.
 */
public final class DifferenceOperator implements RelationalOperator {

    private final RelationSchema resultSchema;
    private final List<Relation> relations;

    public DifferenceOperator(Relation a, Relation b) {
        Objects.requireNonNull(a, "relation 'a' can't be null");
        Objects.requireNonNull(b, "relation 'b' can't be null");

        this.resultSchema = a.schema();
        this.relations = new ArrayList<>();
        this.relations.add(a);
        this.relations.add(b);
    }

    public DifferenceOperator addRelation(Relation relation) {
        Objects.requireNonNull(relation, "relation can't be null");
        relations.add(relation);
        return this;
    }

    @Override
    public Relation execute() {
        for (Relation r : relations) {
            if (!r.schema().equals(resultSchema)) {
                throw new RelationSchemaMismatchException(r, resultSchema);
            }
        }

        RelationSelector relationSelector = new RelationSelector().withSchema(resultSchema);
        Relation minuend = relations.get(0);
        List<Relation> subtrahend = relations.subList(1, relations.size());

        minuend.tuples()
            .filter(t -> subtrahend.stream().noneMatch(r -> r.contains(t)))
            .forEach(relationSelector::addTuple);

        return relationSelector.select();
    }
}
