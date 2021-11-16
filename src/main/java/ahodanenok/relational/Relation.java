package ahodanenok.relational;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Relation is an unordered set of tuples, which have the same schema.
 */
public final class Relation {

    public static final Relation NULLARY_TUPLE = new Relation(RelationSchema.EMPTY, Collections.singleton(Tuple.EMPTY));
    public static final Relation NULLARY_EMPTY = new Relation(RelationSchema.EMPTY, Collections.emptySet());

    private final RelationSchema schema;
    private final Set<Tuple> tuples;

    Relation(RelationSchema schema, Set<Tuple> tuples) {
        this.schema = schema;
        this.tuples = Collections.unmodifiableSet(tuples);
    }

    public RelationSchema schema() {
        return schema;
    }

    public int degree() {
        return schema.degree();
    }

    public int cardinality() {
        return tuples.size();
    }

    /**
     * Check if relation doesn't contain any tuples (cardinality = 0).
     */
    public boolean isEmpty() {
        return cardinality() == 0;
    }

    /**
     * Extract a single tuple from the relation.
     * @throws IllegalStateException if the relation is empty or contains more than one tuple
     */
    public Tuple getSingleTuple() {
        if (tuples.size() != 1) {
            throw new IllegalStateException(
                    "To extract a single tuple it must exist and be the only tuple in the relation");
        }

        return tuples.iterator().next();
    }

    /**
     * Stream of tuples in the relation without any particular order.
     * If the relation is empty, then an empty stream will be returned.
     */
    public Stream<Tuple> tuples() {
        return tuples.stream();
    }

    /**
     * Check if tuple is present in the relation.
     */
    public boolean contains(Tuple tuple) {
        return tuples.contains(tuple);
    }

    /**
     * Check if the relation is superset of the given relation.
     */
    public boolean isSupersetOf(Relation relation) {
        return isSupersetOf(relation, false);
    }

    /**
     * Check if the relation is superset of the given relation.
     * @param proper check if proper superset
     */
    public boolean isSupersetOf(Relation relation, boolean proper) {
        Objects.requireNonNull(relation, "Relation can't be null");
        boolean superset = tuples.containsAll(relation.tuples);
        if (proper) {
            return superset && cardinality() > relation.cardinality();
        } else {
            return superset;
        }
    }

    /**
     * Check if the relation is subset of the given relation
     */
    public boolean isSubsetOf(Relation relation) {
        return isSubsetOf(relation, false);
    }

    /**
     * Check if the relation is subset of the given relation
     * @param proper check if proper subset
     */
    public boolean isSubsetOf(Relation relation, boolean proper) {
        Objects.requireNonNull(relation, "Relation can't be null");
        return relation.isSupersetOf(this, proper);
    }

    @Override
    public int hashCode() {
        return 31 * schema.hashCode() + tuples.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (!this.getClass().equals(obj.getClass())) {
            return false;
        }

        // relations are equal iff they have the same attributes and the same tuples
        Relation other = (Relation) obj;
        if (!schema.equals(other.schema)) {
            return false;
        }

        return tuples.equals(other.tuples);
    }
}
