package ahodanenok.relational.algebra;

import ahodanenok.relational.Relation;
import ahodanenok.relational.RelationSchema;
import ahodanenok.relational.RelationSelector;
import ahodanenok.relational.exception.RelationSchemaMismatchException;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Produces a new relation with each tuple in it existing in any of its input relations.
 * All input relations must be of the same type and the resulting relation will also be of that type.
 * Schema of the leftmost relation is used as the schema of the resulting relation.
 */
public class UnionOperator implements RelationalOperator {

    private final RelationSchema resultSchema;
    private final Set<Relation> relations;

    public UnionOperator(Relation a, Relation b) {
        Objects.requireNonNull(a, "relation 'a' can't be null");
        Objects.requireNonNull(b, "relation 'b' can't be null");

        this.resultSchema = a.schema();
        this.relations = new HashSet<>();
        this.relations.add(a);
        this.relations.add(b);
    }

    public UnionOperator addRelation(Relation relation) {
        Objects.requireNonNull(relation, "relation can't be null");
        this.relations.add(relation);
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
        for (Relation r : relations) {
            r.tuples().forEach(relationSelector::addTuple);
        }

        return relationSelector.select();
    }
}
